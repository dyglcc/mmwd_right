package qfpay.wxshop.ui.view;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.OfficialGoodItemBean;
import qfpay.wxshop.ui.main.fragment.OfficalListFragment;
import qfpay.wxshop.ui.web.*;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.list_item_official_goods)
public class OfficialGoodsItem extends LinearLayout {
	@ViewById
	TextView tv_goods_name;
	OfficialGoodItemBean gb;
    @ViewById
    RelativeLayout layout_profit;
//	@ViewById
//	TagViews layout_tags;
	@ViewById
	ImageView iv_official,iv_recommend;
	View line1, layout_img, layout_read_info;
	View layout_action;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int width, height;
	private Handler handler;
	private Context context;

    @ViewById
    TextView lowPrice_0,tv_profit;



	public OfficialGoodsItem(Context context) {
		super(context);
	}

	public void setValues(OfficialGoodItemBean gb, int pos, int itemWidth) {
		this.gb = gb;

		if (gb == null) {
			return;
		}

		tv_goods_name.setText(gb.getTitle());
		TextPaint tp2 = tv_goods_name.getPaint();
		tp2.setStyle(Style.FILL);
//		lowPrice.setText(gb.getWholesale_price());
        lowPrice_0.setText(getPrice(gb.getPrice()));

//        if(OfficalListFragment.pos_order == OfficalListFragment.PROFIT){
//            layout_profit.setVisibility(View.GONE);
//            layout_guide.setVisibility(View.VISIBLE);
//        }else{
            float guide = Float.parseFloat(gb.getPrice());
            float price = Float.parseFloat(gb.getWholesale_price());
            BigDecimal decimal = new BigDecimal((guide - price)+"");

            tv_profit.setText(getPrice(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
            layout_profit.setVisibility(View.VISIBLE);
//            layout_guide.setVisibility(View.GONE);
//        }


		setclickListener(gb, pos);

		setExtraImage(gb.getImg(),itemWidth);
		
//		showRecommend(gb.is);

//		setTags(gb);

	}

	private String getPrice(String price) {
		String priceR = price;
		if(priceR.endsWith(".00")){
			priceR = priceR.substring(0,priceR.indexOf(".00"));
		}
		if(priceR.endsWith(".0")){
			priceR = priceR.substring(0,priceR.indexOf(".0"));
		}
        if(priceR.indexOf(".")!=-1 && priceR.endsWith("0")){
            priceR = price.substring(0,price.length()-1);
        }
		return priceR;
	}

//	private void setTags(OfficialGoodItemBean gb2) {
//		String[] tags = gb2.getTags();
//		List<Tag> tag = new ArrayList<Tag>();
//		for (int i = 0; i < tags.length; i++) {
//			Tag t = new Tag(tags[i], false);
//			tag.add(t);
//		}
//		layout_tags.setData(tag);
//	}

	private void setExtraImage(String imageUrl,int itemW) {
		
		FrameLayout.LayoutParams para = (android.widget.FrameLayout.LayoutParams) iv_official.getLayoutParams();
		para.height = itemW;
		para.width = itemW;
		
		if (imageUrl == null || imageUrl.equals("")) {
			iv_official.setBackgroundResource(R.drawable.list_item_default);
			return;
		}

		Picasso.with(context).load(Utils.getThumblePic(imageUrl, 300, 300))
				.fit().error(R.drawable.list_item_default).centerCrop()
				.into(iv_official);
	}

	private void setclickListener(OfficialGoodItemBean gb, final int pos) {

		// btn_del.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// MobAgentTools.OnEventMobOnDiffUser(context,
		// "Click_HybridText_Delete");
		//
		// showDialogConfirm("确定要删除嘛?", pos);
		// }
		// });
		//
		// btn_edit.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// MobAgentTools.OnEventMobOnDiffUser(context,
		// "Click_HybridText_Edit");
		// SSNEditActivity_.intent(context).item(gb).editpos(pos)
		// .startForResult(SSNEditActivity.SSN_EDIT);
		// }
		// });
		// iv_extra_1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// go2SsnPreviewActivity();
		// }
		// });
		// layout_img.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// go2SsnPreviewActivity();
		// }
		// });
		// btn_share.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		//
		// MobAgentTools.OnEventMobOnDiffUser(context,
		// "Click_HybridText_Share");
		// fragment.sharebean = gb;
		// handler.sendEmptyMessage(SSNListFragment.SSN_SHARE);
		//
		// }
		// });

	}

//	protected void go2SsnPreviewActivity() {
//		String title = gb.getDescr();
//		if (gb == null || gb.getId() == null) {
//			Toaster.l(context, "货源数据异常");
//			return;
//		}
//		if (gb.getTitle() != null) {
//			title = gb.getTitle();
//		}
//		CommonWebActivity_.intent(context)
//				.url(WDConfig.getInstance().WD_URL + "qmm/item/" + gb.getId())
//				.title(title).start();
//	}

	protected void showDialogConfirmCanclePromo(String content, final int pos) {
		FragmentActivity activity = (FragmentActivity) context;
		Utils.showNativeDialog(activity, context.getString(R.string.mm_hint),
				content, context.getString(R.string.cancel),
				context.getString(R.string.OK), false, -1,
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {

					}
				});
	}

	protected void showDialogConfirm(String content, final int pos) {
		// FragmentActivity activity = (FragmentActivity) context;
		// Utils.showNativeDialog(activity, context.getString(R.string.mm_hint),
		// content, context.getString(R.string.cancel),
		// context.getString(R.string.OK), false, -1,
		// new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// SSnDelNetImpl netDel = new SSnDelNetImpl(
		// (Activity) context);
		// Bundle bun = new Bundle();
		// bun.putString("mid", gb.getMid());
		// netDel.request(bun, new MainHandler(context) {
		//
		// @Override
		// public void onSuccess(Bundle bundle) {
		// Message msg = handler.obtainMessage();
		// Bundle bun = new Bundle();
		// bun.putInt("pos", pos);
		// bun.putSerializable(
		// SSNPublishActivity.SSN_DEL_BEAN, gb);
		// msg.setData(bun);
		// msg.what = SSNListFragment.SSN_DEL;
		// handler.sendMessage(msg);
		// }
		//
		// @Override
		// public void onFailed(Bundle bundle) {
		//
		// }
		// });
		//
		// }
		// });
	}

}
