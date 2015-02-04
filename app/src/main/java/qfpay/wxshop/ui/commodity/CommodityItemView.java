package qfpay.wxshop.ui.commodity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.ManPromoActivity_;
import qfpay.wxshop.activity.ManagePreViewActivity_;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.beans.GoodMSBean;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.getui.ImageUtils.ImageSizeForUrl;
import qfpay.wxshop.ui.commodity.CommodityListFragment.CommodityWrapper;
import qfpay.wxshop.ui.commodity.detailmanager.ItemDetailManagerActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.Toaster;

@EViewGroup(R.layout.commodity_list_item)
public class CommodityItemView extends LinearLayout {
	private static final int SIZE_MENU_HEIGHT_DP = 82;
	private static final String DATE_FORMATSTR    = "MM-dd";
	
	@ViewById ImageView    iv_icon, iv_promotation, iv_indicator, iv_menu_top, iv_top;
	@ViewById TextView     tv_name, tv_time, tv_price, tv_stock, tv_salesvolume;
	@ViewById TextView     tv_menu_promotation, tv_menu_share, tv_menu_edit, tv_menu_top, tv_menu_preview, tv_menu_offshelf;
	@ViewById LinearLayout ll_name, ll_menu;
	@ViewById View         v_line;
	
	private CommodityShare        shareUtils;
	private CommodityWrapper      data;
	@Bean CommodityDataController controller;
	
	public CommodityItemView(Context context) {
		super(context);
	}
	
	public CommodityItemView setData(CommodityWrapper data, CommodityShare shareUtils) {
		this.data = data;
		this.shareUtils = shareUtils;
		
		if (data.isMenuOpened) {
			openMenu();
			v_line.setVisibility(View.INVISIBLE);
		} else {
			closeMenu();
			v_line.setVisibility(View.VISIBLE);
		}
		
		tv_name.setText(data.model.getName() + "");
		tv_stock.setText(data.model.getStock() + "");
		tv_salesvolume.setText(data.model.getSalesCount() + "");
		try {
			tv_time.setText(new SimpleDateFormat(DATE_FORMATSTR, Locale.getDefault()).format(data.model.getCreateTime().getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			tv_time.setText("00-00");
		}
		String imgUrl = QFCommonUtils.generateQiniuUrl(data.model.getImgUrl(), ImageSizeForUrl.MID);
		Picasso.with(getContext()).load(imgUrl).fit().centerCrop().placeholder(R.drawable.list_item_default).
			error(R.drawable.list_item_default).into(iv_icon);
		
		processTitleWidth();
		processTopUI(controller.isTop(data.model));
		processSalesPromotion(data.model.getSalesPromotion());
		return this;
	}
	
	/**
	 * 设置当前Item的title宽度,为了防止显示不出来title后面的两个小图标
	 */
	void processTitleWidth() {
		ViewTreeObserver ob = ll_name.getViewTreeObserver();
		ob.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (ll_name.getWidth() >= 0) {
					int llw = ll_name.getWidth();
					int ivw = QFCommonUtils.dip2px(getContext(), 30) * 2 + QFCommonUtils.dip2px(getContext(), 8);
					tv_name.setMaxWidth(llw - ivw);
					ll_name.getViewTreeObserver().removeOnPreDrawListener(this);
				}
				return true;
			}
		});
	}
	


	@Click void ll_menu_edit() {
		String goodid = data.model.getID() + "";
		if (goodid == null || goodid.equals("")) {
			Toaster.l(getContext(), "出错了,刷新界面试试");
			return;
		}
        ItemDetailManagerActivity_.intent(getContext()).
                id(Integer.parseInt(goodid, 10)).
                isPromotation(data.model.getSalesPromotion() != null && data.model.getSalesPromotion().getPromotionID() > 0).
                start();
	}
	



	private void changeMenuHeight(float height) {
		android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) ll_menu.getLayoutParams();
		lp.height = (int) height;
		ll_menu.requestLayout();
	}


    public GoodsBean new2Old(CommodityModel model) {
        GoodsBean gb = new GoodsBean();
        gb.setCreateDateStr(model.getCreateTimeForOld());
        gb.setEditPos(controller.getIndex(model));
        gb.setGoodDesc(model.getDescript());
        gb.setGoodName(model.getName());
        gb.setGoodsId(model.getID() + "");
        gb.setGoodstate(model.getCommodityStateForOld() + "");
        gb.setImageUrl(QFCommonUtils.generateQiniuUrl(model.getImgUrl(), ImageSizeForUrl.MIN));
        gb.setPostage(model.getPostage() + "");
        gb.setPriceStr(model.getPrice() + "");
        gb.setSaled(model.getSalesCount() + "");
        gb.setStock(model.getStock() + "");
        gb.setWeight(model.getSortWeight());
        return gb;
    }

    /**
     * 反转当前的置顶显示
     */
    public void processTopUI(boolean currentState) {
        if (currentState) {
            iv_menu_top.setImageResource(R.drawable.shopmanager_item_down);
            tv_menu_top.setText(R.string.item_menu_title_canceltop);
            iv_top.setVisibility(View.VISIBLE);
        } else {
            iv_menu_top.setImageResource(R.drawable.shopmanager_item_top);
            tv_menu_top.setText(R.string.item_menu_title_top);
            iv_top.setVisibility(View.GONE);
        }
    }

    /**
     * 控制当前秒杀按钮的显示
     */
    public void processSalesPromotion(SalesPromotionModel salesPromotion) {
        if (salesPromotion != null && salesPromotion.getCommodityID() != 0) {
            iv_promotation.setVisibility(View.VISIBLE);
            tv_menu_promotation.setText("关闭秒杀");
            tv_price.setText(salesPromotion.getPromotionPrice() + "");
        } else {
            iv_promotation.setVisibility(View.GONE);
            tv_menu_promotation.setText("秒杀活动");
            tv_price.setText(data.model.getPrice() + "");
        }
    }

    @Click
    void ll_menu_share() {
        shareUtils.onShare(data.model);
    }

    @Click
    void ll_menu_promotation() {
        SalesPromotionModel salesPromotion = data.model.getSalesPromotion();
        if (salesPromotion != null && salesPromotion.getCommodityID() != 0) {
            QFCommonUtils.showDialog((FragmentActivity) getContext(), getResources().getString(R.string.promotion_dialog_title_cancel),
                    getResources().getString(R.string.promotion_dialog_msg_cancel),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            controller.cancelPromotion(data.model);
                        }
                    });
        } else {
            MobAgentTools.OnEventMobOnDiffUser(getContext(), "manage_seckill");
            ManPromoActivity_.intent(getContext()).gb(new2Old(data.model)).pos(controller.getIndex(data.model)).
                    from("GoodlistFragment").start();
        }
    }


    @Click
    void ll_menu_top() {
        if (controller.isTop(data.model)) {
            QFCommonUtils.showDialog((FragmentActivity) getContext(), getResources().getString(R.string.item_dialog_title_taketop_cancel),
                    getResources().getString(R.string.item_dialog_msg_taketop_cancel),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            controller.cancelTop(data.model);
                            processTopUI(false);
                        }
                    });
            return;
        }
        if (controller.getTopList() != null) {
            QFCommonUtils.showDialog((FragmentActivity) getContext(), getResources().getString(R.string.item_dialog_title_taketop_replace),
                    getResources().getString(R.string.item_dialog_msg_taketop_replace),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            controller.takeTop(data.model);
                            processTopUI(false);
                        }
                    });
        } else {
            controller.takeTop(data.model);
            processTopUI(false);
            QFCommonUtils.showDialog((FragmentActivity) getContext(), getResources().getString(R.string.item_dialog_title_taketop_success),
                    getResources().getString(R.string.item_dialog_msg_taketop_success),
                    getResources().getString(R.string.item_dialog_negative_taketop),
                    getResources().getString(R.string.item_dialog_positivebtn_taketop), false, -1,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {

//						Intent intent = new Intent(getContext(), ManagePreViewActivity.class);
//						intent.putExtra(ConstValue.TITLE, "店铺预览");
//						intent.putExtra(ConstValue.URL,
//                                WDConfig.getInstance().getShopUrl()
//                                + WxShopApplication.dataEngine.getShopId()
//                                + "?ga_medium=android_mmwdapp_stickpreview_&ga_source=entrance");
//						getContext().startActivity(intent);

                            GoodsBean gb = new2Old(data.model);
                            gb.setMsBean(new2OldMS(data.model));
                            ManagePreViewActivity_.intent(getContext()).
                                    title("商品预览").
                                    url(WDConfig.getInstance().getGoodPreviewUrl() + data.model.getID() + "?ga_medium=android_mmwdapp_preview_&ga_source=entrance").
                                    gooditem(gb).start();
                        }
                    });
        }
    }


    @Click
    void ll_menu_preview() {
        MobAgentTools.OnEventMobOnDiffUser(getContext(), "management_goodspreview");

        GoodsBean gb = new2Old(data.model);
        gb.setMsBean(new2OldMS(data.model));
        ManagePreViewActivity_.intent(getContext()).ga_medium("android_mmwdapp_previewshare_").
                title("商品预览").
                url(WDConfig.getInstance().getGoodPreviewUrl() + data.model.getID() + "?ga_medium=android_mmwdapp_preview_&ga_source=entrance").
                gooditem(gb).start();
//		Intent intent = new Intent(getContext(), ManagePreViewActivity.class);
//		intent.putExtra(ConstValue.TITLE, "商品预览");
//		intent.putExtra(ConstValue.URL, "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/item/" + data.model.getID() + "?ga_medium=android_mmwdapp_managepreview_&ga_source=entrance&from=app_preview");
//		getContext().startActivity(intent);
    }

    @Click
    void ll_menu_offshelf() {
        String msg = "";
        if (data.model.getSortWeight() > 0) {
            msg = "这是置顶的商品诶！确定下架吗？";
        } else {
            msg = "下架该商品吗？";
        }
        QFCommonUtils.showDialog((FragmentActivity) getContext(), getResources().getString(R.string.item_dialog_title_offshelf),
                msg, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        controller.remove(data.model);
                    }
                });
        MobAgentTools.OnEventMobOnDiffUser(getContext(), "remove");
    }

    @SuppressLint("NewApi")
    private void openMenu() {
        if (!data.isAni) {
            changeMenuHeight(QFCommonUtils.dip2px(getContext(), SIZE_MENU_HEIGHT_DP));
            iv_indicator.setRotation(180);
            return;
        }

        ValueAnimator menuAni = ValueAnimator.ofFloat(0, QFCommonUtils.dip2px(getContext(), SIZE_MENU_HEIGHT_DP));
        menuAni.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                changeMenuHeight((Float) arg0.getAnimatedValue());
            }
        });
        menuAni.setDuration(100);
        menuAni.start();

        data.isAni = false;
    }

    @SuppressLint("NewApi")
    private void closeMenu() {
        if (!data.isAni) {
            changeMenuHeight(0);
            iv_indicator.setRotation(0);
            return;
        }

        ValueAnimator menuAni = ValueAnimator.ofFloat(QFCommonUtils.dip2px(getContext(), SIZE_MENU_HEIGHT_DP), 0);
        menuAni.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                changeMenuHeight((Float) arg0.getAnimatedValue());
            }
        });
        menuAni.setDuration(100);
        menuAni.start();

        data.isAni = false;
    }


    public GoodMSBean new2OldMS(CommodityModel model) {
        GoodMSBean bean = null;
        SalesPromotionModel msModel = model.getSalesPromotion();
        if(msModel == null){
            return bean;
        }
        if (msModel.getPromotionFlag() == SalesPromotionModel.PromotionState.STARTED && msModel.getCommodityID() != 0) {
            bean = new GoodMSBean();
            bean.setNewprice(msModel.getPromotionPrice() + "");
            bean.setId(msModel.getPromotionID() + "");
        }
        return bean;

    }
}
