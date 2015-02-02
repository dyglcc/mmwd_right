package qfpay.wxshop.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.ManagePreViewActivity_;
import qfpay.wxshop.activity.SSNPublishActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.OnekeybehalfItemBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.netImpl.OneKeybehalfDelNetImpl;
import qfpay.wxshop.getui.ImageUtils;
import qfpay.wxshop.ui.commodity.CommodityShare;
import qfpay.wxshop.ui.main.fragment.OneKeyBeHalfListFragment;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import android.os.Handler;


@EViewGroup(R.layout.onkeybehalf_list_item)
public class OnekeybeHalfItem extends LinearLayout {
    private static final int SIZE_MENU_HEIGHT_DP = 82;
    private static final String DATE_FORMATSTR = "MM-dd";

    @ViewById
    ImageView iv_icon, iv_indicator;
    @ViewById
    TextView tv_name, tv_time,
            tv_price,
            tv_commission,
            tv_salesvolume,
            tv_time_text,
            tv_price_text,
            tv_commission_text,
            tv_salesvolume_text;
    @ViewById
    TextView tv_menu_share, tv_menu_preview, tv_menu_offshelf;
    @ViewById
    LinearLayout ll_name, ll_menu;
    @ViewById
    View v_line;
    @ViewById
    ImageView iv_carriage;

    @ViewById
    View ll_menu_share,ll_menu_offshelf,ll_menu_preview;

    private Context context;
    private Handler handler;
    private CommodityShare shareUtils;
    private OnekeybehalfItemBean data;

    public OnekeybeHalfItem(Context context) {
        super(context);
    }

    public OnekeybeHalfItem setData(OnekeybehalfItemBean data,Handler handler,Context context) {
        this.data = data;

        this.context = context;

        this.handler = handler;

        if (data.isMenuOpened) {
            openMenu();
            v_line.setVisibility(View.INVISIBLE);
        } else {
            closeMenu();
            v_line.setVisibility(View.VISIBLE);
        }

        tv_name.setText(data.getTitle() + "");
        tv_price.setText(data.getPrice() );
        tv_commission.setText("￥" +data.getCps_value() + "");
        tv_salesvolume.setText( data.getSales() + "");
        tv_time.setText(getDateStr(data.getCps_created()));
        String imgUrl = QFCommonUtils.generateQiniuUrl(data.getImg(), ImageUtils.ImageSizeForUrl.MID);
        Picasso.with(getContext()).load(imgUrl).fit().centerCrop().placeholder(R.drawable.list_item_default).
                error(R.drawable.list_item_default).into(iv_icon);

        processTitleWidth();
        
        settextColor(data.getIs_agent_actived());

        return this;
    }



    private void settextColor(int status) {
        if(0 == status){
            tv_name.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_price.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_commission.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_time.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_salesvolume.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_price_text.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_commission_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_time_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_salesvolume_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            iv_carriage.setVisibility(View.VISIBLE);

            ll_menu_preview.setVisibility(View.GONE);
            ll_menu_share.setVisibility(View.GONE);

        }else{
            tv_name.setTextColor(getResources().getColor(R.color.text_content));
            tv_price.setTextColor(getResources().getColor(R.color.text_content));

            tv_commission.setTextColor(getResources().getColor(R.color.common_text_red));
            tv_time.setTextColor(getResources().getColor(R.color.text_content));
            tv_salesvolume.setTextColor(getResources().getColor(R.color.common_text_red));

            tv_price_text.setTextColor(getResources().getColor(R.color.text_content));

            tv_commission_text.setTextColor(getResources().getColor(R.color.text_content));
            tv_time_text.setTextColor(getResources().getColor(R.color.text_content));
            tv_salesvolume_text.setTextColor(getResources().getColor(R.color.text_content));
            iv_carriage.setVisibility(View.GONE);

            ll_menu_preview.setVisibility(View.VISIBLE);
            ll_menu_share.setVisibility(View.VISIBLE);
        }
    }


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMATSTR);

    private String getDateStr(String cps_created) {

        if (cps_created == null || cps_created.equals("")) {
            return null;
        }
        try {
            Date date = format.parse(cps_created);
            return format1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置当前Item的title宽度,为了防止显示不出来title后面的两个小图标
     */
    void processTitleWidth() {
        ViewTreeObserver ob = ll_name.getViewTreeObserver();
        ob.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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


    @Click
    void ll_menu_share() {

        Message msg = handler.obtainMessage();
        msg.what = OneKeyBeHalfListFragment.SSN_SHARE;
        Bundle bun = new Bundle();
        bun.putSerializable("share",data);
        msg.setData(bun);
        msg.sendToTarget();

    }


    @Click
    void ll_menu_preview() {
        MobAgentTools.OnEventMobOnDiffUser(getContext(), "management_goodspreview");

        GoodsBean gb = new2Old(data);
        ManagePreViewActivity_.intent(getContext()).ga_medium("android_mmwdapp_previewshare_").
                title("商品预览").
                url(WDConfig.getInstance().getGoodPreviewUrl() + data.getId() + "?fx_refer=qfuid_"+WxShopApplication.dataEngine.getUserId()+"&ga_medium=android_mmwdapp_preview_&ga_source=entrance").
                gooditem(gb).start();
    }

    @Click
    void ll_menu_offshelf() {
        String msg = "";
        msg = "取消代理该商品吗？";
        QFCommonUtils.showDialogCancelBehalf((FragmentActivity) getContext(), "喵喵提示",
                msg, new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        removeData();

                    }
                });
        MobAgentTools.OnEventMobOnDiffUser(getContext(), "remove");
    }

    private void removeData() {

        AbstractNet net = new OneKeybehalfDelNetImpl(context);
        Bundle bun = new Bundle();
        bun.putString("itemid", data.getId());
        net.request(bun, new MainHandler(context) {

            @Override
            public void onSuccess(Bundle bundle) {

                Message msg = handler.obtainMessage();
                msg.what = OneKeyBeHalfListFragment.SSN_DEL;
                Bundle bun = new Bundle();
                bun.putSerializable(SSNPublishActivity.SSN_DEL_BEAN,data);
                msg.setData(bun);
                msg.sendToTarget();
            }

            @Override
            public void onFailed(Bundle bundle) {

            }
        });

}

    @SuppressLint("NewApi")
    private void openMenu() {
        if (!data.isAni) {
            changeMenuHeight(QFCommonUtils.dip2px(getContext(), SIZE_MENU_HEIGHT_DP));
            iv_indicator.setRotation(180);
            return;
        }

        ValueAnimator menuAni = ValueAnimator.ofFloat(0, QFCommonUtils.dip2px(getContext(), SIZE_MENU_HEIGHT_DP));
        menuAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
        menuAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                changeMenuHeight((Float) arg0.getAnimatedValue());
            }
        });
        menuAni.setDuration(100);
        menuAni.start();

        data.isAni = false;
    }

    private void changeMenuHeight(float height) {
        android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) ll_menu.getLayoutParams();
        lp.height = (int) height;
        ll_menu.requestLayout();
    }

    public GoodsBean new2Old(OnekeybehalfItemBean data) {
        GoodsBean gb = new GoodsBean();
        gb.setCreateDateStr(data.getCps_created());
        gb.setGoodDesc(data.getDescr());
        gb.setGoodName(data.getTitle());
        gb.setGoodsId(data.getId() + "");
        gb.setImageUrl(QFCommonUtils.generateQiniuUrl(data.getImg(), ImageUtils.ImageSizeForUrl.MIN));
        gb.setPostage("");
        gb.setPriceStr(data.getPrice());
        gb.setSaled(data.getAmount());
        gb.setStock("");
        return gb;
    }



}

//public void setValues(SSNItemBean gb, int pos) {
//        this.gb = gb;
//
//        if (gb == null) {
//            return;
//        }
//        tv_read.setText(gb.getAlluv() + "");
//        // tv_today_read.setText(gb.getToday_read() + "");
//        tv_zan.setText(gb.getLikes() + "");
//        tv_title.setText(gb.getTitle());
//        if (gb.getUpdate_time() != null && gb.getUpdate_time().length() > 10) {
//            CharSequence dateString = SSNListFragment.dateStrs.get(pos);
//            if (dateString != null && !dateString.equals("")) {
//                String str = gb.getUpdate_time();
//                layout_date.setVisibility(View.VISIBLE);
//                tv_month.setText(str.substring(5, 7) + "月");
//                tv_day.setText(str.substring(8, 10));
//            } else {
//                layout_date.setVisibility(View.INVISIBLE);
//            }
//        }
//        if (gb.getId().equals(SSNListFragment.DEMO_ID)) {
//            iv_demo.setVisibility(View.VISIBLE);
//            layout_action.setVisibility(View.GONE);
//        } else {
//            iv_demo.setVisibility(View.GONE);
//            layout_action.setVisibility(View.VISIBLE);
//        }
//        setclickListener(gb, pos);
//        setExtraImage(gb.getImg_url());
//        iv_demoSetting(gb);
//
//    }
//
//    private void setExtraImage(String imageBean) {
//        if (imageBean == null || imageBean.equals("")) {
//            iv_extra_1.setBackgroundResource(R.drawable.list_item_default);
//            return;
//        }
//
//        Picasso.with(context).load(Utils.getThumblePic(imageBean, 450, 450)).fit().error(R.drawable.list_item_default).centerCrop().into(iv_extra_1);
//    }
//
//    private void setclickListener(final SSNItemBean gb, final int pos) {
//
//        btn_del.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                MobAgentTools.OnEventMobOnDiffUser(context,
//                        "Click_HybridText_Delete");
//
//                showDialogConfirm("确定要删除嘛?", pos);
//            }
//        });
//
//        btn_edit.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                MobAgentTools.OnEventMobOnDiffUser(context,
//                        "Click_HybridText_Edit");
//                SSNEditActivity_.intent(context).item(gb).editpos(pos)
//                        .startForResult(SSNEditActivity.SSN_EDIT);
//            }
//        });
//        iv_extra_1.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                go2SsnPreviewActivity();
//            }
//        });
//        layout_img.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                go2SsnPreviewActivity();
//            }
//        });
//        btn_share.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                MobAgentTools.OnEventMobOnDiffUser(context,
//                        "Click_HybridText_Share");
//                fragment.sharebean = gb;
//                handler.sendEmptyMessage(SSNListFragment.SSN_SHARE);
//
//            }
//        });
//
//    }
//
//    protected void go2SsnPreviewActivity() {
//        String title = "碎碎念";
//        if (gb == null || gb.getId() == null) {
//            Toaster.l(context, "碎碎念数据异常");
//            return;
//        }
//        if (gb.getTitle() != null) {
//            title = gb.getTitle();
//        }
//        CommonWebActivity_
//                .intent(context)
//                .url(Utils.getSSNurl(gb)).title(title).start();
//    }
//
//    protected void showDialogConfirmCanclePromo(String content, final int pos) {
//        FragmentActivity activity = (FragmentActivity) context;
//        Utils.showNativeDialog(activity, context.getString(R.string.mm_hint),
//                content, context.getString(R.string.cancel),
//                context.getString(R.string.OK), false, -1,
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//
//                    }
//                });
//    }
//
//    protected void showDialogConfirm(String content, final int pos) {
//        FragmentActivity activity = (FragmentActivity) context;
//        Utils.showNativeDialog(activity, context.getString(R.string.mm_hint),
//                content, context.getString(R.string.cancel),
//                context.getString(R.string.OK), false, -1,
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//
//                        SSnDelNetImpl netDel = new SSnDelNetImpl(
//                                (Activity) context);
//                        Bundle bun = new Bundle();
//                        bun.putString("mid", gb.getMid());
//                        netDel.request(bun, new MainHandler(context) {
//
//                            @Override
//                            public void onSuccess(Bundle bundle) {
//                                Message msg = handler.obtainMessage();
//                                Bundle bun = new Bundle();
//                                bun.putInt("pos", pos);
//                                bun.putSerializable(
//                                        SSNPublishActivity.SSN_DEL_BEAN, gb);
//                                msg.setData(bun);
//                                msg.what = SSNListFragment.SSN_DEL;
//                                handler.sendMessage(msg);
//                            }
//
//                            @Override
//                            public void onFailed(Bundle bundle) {
//
//                            }
//                        });
//
//                    }
//                });
//    }
//
//}

