package qfpay.wxshop.utils;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.GoodMSBean;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import android.app.Activity;

public class ShareUtils {

	// 发送给朋友
	public static void friendGoodItem(GoodsBean gb, Activity context) {
		if(!WxShopApplication.api.isWXAppInstalled()){
			Toaster.s(context, context.getString(R.string.tip_needinstallkehuduan));
			return;
		}
		WeiXinDataBean wdb = new WeiXinDataBean();
		if (gb.getMsBean() != null && gb.getMsBean().getId() != null) {
			wdb.title = "【限时秒杀】" + gb.getGoodName() + "特价"
					+ gb.getMsBean().getNewprice() + "元，手快有、手慢无哦！";
		} else {
			wdb.title = "【新品推荐】" + gb.getGoodName() + "仅需"
					+ gb.getPriceStr() + "元，欢迎进店选购下单哟！";
		}
		String desc = gb.getGoodDesc();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		wdb.description = desc;
		wdb.scope = ConstValue.friend_share;
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + gb.getGoodsId();
		wdb.imgUrl = gb.getImageUrl();
		
		UtilsWeixinShare.shareWeb(wdb,ConstValue.android_mmwdapp_manageshare_wcfriend, context);
	}

	public static ShareBean getShareBean(GoodsBean gb, Activity context) {
		ShareBean sb = new ShareBean();
		if (gb == null || gb.getImageUrl() == null
				|| gb.getImageUrl().equals("")) {
			Toaster.l(context, "亲，店铺里面空空如也，先发布个商品再分享吧");
			return null;
		}
		if(gb.getSrcimgUrl() == null){
			sb.imgUrl = gb.getImageUrl();
			
		}else{
			sb.imgUrl = Utils.getThumblePic(gb.getSrcimgUrl(), ConstValue.shareBigPic);
		}
		// if (jsonObject.has("MsgImg")) {
		// shareBean.imgUrl = jsonObject.getString("MsgImg");
		// }
		sb.link = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + gb.getGoodsId();

		// if (jsonObject.has("link")) {
		// shareBean.link = jsonObject.getString("link");
		// }
		GoodMSBean gms = gb.getMsBean();
		if (gms != null && gms.getId() != null) {
			sb.title = "【限时秒杀】" + gb.getGoodName() + "特价" + gms.getNewprice()
					+ "元，手快有、手慢无哦！";
			
		} else {
			String descString = gb.getGoodDesc();
			if(descString.length() > 100){
				descString = descString.substring(0,98)+"...";
			}
			sb.title = "亲,我的店铺又有新宝贝了哦! " + gb.getGoodName() + " 仅需" + gb.getPriceStr()
					+ "元,点击宝贝链接" + "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + gb.getGoodsId() + " 直接下单购买哦";
			
//			sb.title = "【新品推荐】" + gb.getGoodName() + "仅需" + gb.getPriceStr()
//					+ "元，欢迎进店选购下单哟！";
			sb.from = "utils";
		}

		String desc = gb.getGoodDesc();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		sb.desc = desc;
		
		// QQzone
		sb.qq_imageUrl =gb.getImageUrl();
		sb.qqTitle = "亲,我的店铺又有新宝贝了哦! ";
		sb.qqTitle_url = sb.link;
		sb.qqText = sb.title;
		return sb;

	}

	// 发送朋友圈
	public static void momentsGoodItem(GoodsBean gb,Activity context) {
		if(!WxShopApplication.api.isWXAppInstalled()){
			Toaster.s(context, context.getString(R.string.tip_needinstallkehuduan));
			return;
		}
		WeiXinDataBean wdb = new WeiXinDataBean();
		MobAgentTools.OnEventMobOnDiffUser(context, "weixin_share_moment_begin");
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + gb.getGoodsId();
		wdb.imgUrl = gb.getImageUrl();
		String desc = gb.getGoodDesc();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		wdb.description = desc;
		// 判断是否是秒杀
		GoodMSBean gms = gb.getMsBean();
		if (gms != null && gms.getId() != null) {
			wdb.title = "【限时秒杀】" + gb.getGoodName() + "特价" + gms.getNewprice()
					+ "元，手快有、手慢无哦！";
		} else {
			wdb.title = "【新品推荐】" + gb.getGoodName() + "仅需" + gb.getPriceStr()
					+ "元，欢迎进店选购下单哟！";
		}
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb,ConstValue.android_mmwdapp_manageshare_wctimeline, context);

	}

}
