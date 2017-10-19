package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.data.beans.GoodMSBean;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.commodity.CommodityDataController;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.ShareUtils;
import qfpay.wxshop.utils.Toaster;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import cn.sharesdk.framework.ShareSDK;
/**
 * 买家秀发布成功界面
 */
@EActivity(R.layout.edititem_done_layout)
public class ManPromoSuccessActivity extends BaseActivity {
	@ViewById
	Button btn_preview;
	@ViewById
	TextView tv_title;
	@ViewById
	ImageView iv_success;
	@ViewById
	ImageButton btn_back;
	@ViewById
	View ll_newitem;
	@ViewById
	View line_up;
	@ViewById
	View line_down;
	@ViewById
	View layout_show_label;

	@Extra
	GoodsBean gb;
	@Extra
	GoodMSBean gms;
	@Extra
	int pos;
	@Extra
	String from;
    @Bean
    CommodityDataController commodityController;

	@AfterViews
	void init() {
		btn_preview.setText("预览");
		tv_title.setText("秒杀设置成功");
		if (gb == null) {
			Toaster.l(this, "数据是空！");
			return;
		}
		if (!initShare) {
//			ShareSDK.initSDK(this);
			initShare = true;
		}
		iv_success.setImageResource(R.drawable.man_promo_success);
		if (from == null) {
			Toaster.l(ManPromoSuccessActivity.this, "异常了");
			finish();
		}
		gb.setMsBean(gms);
		ll_newitem.setVisibility(View.INVISIBLE);
		line_down.setVisibility(View.INVISIBLE);
		line_up.setVisibility(View.INVISIBLE);
		layout_show_label.setVisibility(View.GONE);

        commodityController.reloadCurrentData();
	}

	@Click
	void btn_back() {
		finish_activity2Manlist();
	}

	private boolean initShare;

	@Click
	void btn_preview() {

//		Intent intent = new Intent(this, ManagePreViewActivity.class);
//		intent.putExtra(ConstValue.TITLE, "商品预览");
//		String url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + gb.getGoodsId() + "?ga_medium=android_mmwdapp_seckill_&ga_source=entrance";
//		intent.putExtra(ConstValue.URL, url);
//		startActivity(intent);


//        GoodsBean gb = new2Old(data.model);
//        gb.setMsBean(new2OldMS(data.model));
        ManagePreViewActivity_.intent(ManPromoSuccessActivity.this).
                title("商品预览").ga_medium("android_mmwdapp_seckillshare_").
                url("http://" + WxShopApplication.app.getDomainMMWDUrl() + "/item/" + gb.getGoodsId() + "?ga_medium=android_mmwdapp_seckillshare_&ga_source=entrance").
                gooditem(gb).start();

	}

	private void finish_activity2Manlist() {

		if (gms == null) {
			Toaster.l(this, "空了！");
		}
		WxShopApplication.psb.setNeedRefresh(true);
		WxShopApplication.psb.setPos(pos);
		WxShopApplication.psb.setGms(gms);
		finish();
	}

	@Click(R.id.ll_share_moments)
	void shareMoments() {
		MobAgentTools.OnEventMobOnDiffUser(this,
				"seckill_sharegoods_circle_manage");
		ShareUtils.momentsGoodItem(gb, ManPromoSuccessActivity.this, "android_mmwdapp_seckill_wctimeline");

	}

	@Click(R.id.ll_share_friends)
	void shareWX() {
		MobAgentTools.OnEventMobOnDiffUser(this,
				"seckill_sharegoods_wechat_manage");
		ShareUtils.friendGoodItem(gb, ManPromoSuccessActivity.this, "android_mmwdapp_seckill_wcfriend");
	}

	@Click(R.id.ll_share_onekey)
	void shareOneKey() {
		MobAgentTools.OnEventMobOnDiffUser(this,
				"seckill_sharegoods_onekey_manage");

		WxShopApplication.shareBean = ShareUtils.getShareBean(gb, ManPromoSuccessActivity.this);
		Intent intent = new Intent(ManPromoSuccessActivity.this, ShareActivity.class);
		intent.putExtra(ConstValue.gaSrcfrom, "android_mmwdapp_seckill_");
		intent.putExtra("share_content_type", ShareActivity.SHARE_CONTENT_MANPRO_SUCCESS);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if (initShare) {
		// ShareSDK.stopSDK(this);
		// }
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish_activity2Manlist();
	}
}
