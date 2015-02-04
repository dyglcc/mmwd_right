package qfpay.wxshop.share.wexinShare;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class UtilsWeixinShare {

	public static void shareWeb(WeiXinDataBean wdb, String ga_st_extra,
								Context context) {
		if (!processeImg(wdb, ga_st_extra, context)) {
			return;
		}

		WXWebpageObject webpage = new WXWebpageObject();
		if (ga_st_extra != null) {
			if (wdb.url.contains("?")) {
				webpage.webpageUrl = wdb.url+"&ga_medium="+ga_st_extra +"&ga_source=entrance";
			} else {
				webpage.webpageUrl = wdb.url+"?ga_medium="+ga_st_extra +"&ga_source=entrance";
			}
		} else {
			webpage.webpageUrl = wdb.url;
		}
		T.i("share url is: " + webpage.webpageUrl);
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = wdb.title;
		msg.description = wdb.description;
		msg.thumbData = wdb.thumbData;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = Utils.buildTransaction("webpage");
		req.message = msg;
		req.scene = wdb.scope ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		/**
		 * 通过日志来获取thumbdata 是否有误，尝试换另外一种thumbdata 生成方式
		 * */
//		Thread tt1 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Process logcatProcess = null;
//				BufferedReader bufferedReader = null;
//				try {
//					/** 获取系统logcat日志信息 */
//
//					String[] delLog = new String[] { "logcat", "-c" };
//					Runtime.getRuntime().exec(delLog);
//					// adb logcat MicroMsg.SDK.WXApiImplV10:E qfpay.wxshop:d *:S
//					// 相当于在命令行运行 logcat -s dalvikm , -s表示过滤，
//					// 第三个参数表示过滤的条件。如果没有第三个参数，数组长度2，肯定也是可以的。下面有logcat的使 用方法
//					String[] running = new String[] { "logcat",
//							"MicroMsg.SDK.WXMediaMessage:E",
//							"qfpay.wxshop:d *:S" };
//					logcatProcess = Runtime.getRuntime().exec(running);
//
//					bufferedReader = new BufferedReader(new InputStreamReader(
//							logcatProcess.getInputStream()));
//
//					String line;
//					// 筛选需要的字串
//					String strFilter = "thumbData is invalid";
//
//					while ((line = bufferedReader.readLine()) != null) {
//						// 读出每行log信息
//						if (line.indexOf(strFilter) >= 0) {
//							/** 检测到strFilter的log日志语句，进行你需要的处理 */
//							UtilsWeixinShare.wdb.thumbData = BitmapUtil
//									.bmpToByteArray(map, true);
//							T.i("图片编码失败，重新编码 has retry" + UtilsWeixinShare.wdb.hasRetry);
//							if(!UtilsWeixinShare.wdb.hasRetry){
//								UtilsWeixinShare.wdb.hasRetry = true;
//								String url = UtilsWeixinShare.wdb.imgUrl.substring(0,UtilsWeixinShare.wdb.imgUrl.indexOf("?"));
//								UtilsWeixinShare.wdb.imgUrl = Utils.getThumblePic(url, 20);
//								shareWeb(UtilsWeixinShare.wdb,
//										UtilsWeixinShare.ga_st_extra,
//										UtilsWeixinShare.context);
//							}
//
//							break;
//						}
//					}
//
//				} catch (Exception e) {
//
//					e.printStackTrace();
//				}
//			}
//		});
//		tt1.start();

        if(WxShopApplication.dataEngine.isFirstWeixinShare()){

            share2Times(req);

        }else{
            WxShopApplication.api.sendReq(req);
        }
	}

    private static void share2Times(final SendMessageToWX.Req req) {
        WxShopApplication.api.sendReq(req);
        WxShopApplication.dataEngine.isSetFirstWeixinShare(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    WxShopApplication.api.sendReq(req);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // public static boolean processeImg(final WeiXinDataBean wdb,final String
	// extra, final Context context) {
	// if (wdb.thumbData == null) {
	// Glide.with(context).load(wdb.imgUrl).asBitmap().into(new
	// SimpleTarget<Bitmap>(100, 100) {
	// @Override public void onResourceReady(Bitmap arg0, GlideAnimation<? super
	// Bitmap> arg1) {
	// byte[] bytes = BitmapUtil.bmpToByteArray(arg0, true);
	// wdb.thumbData = bytes;
	// shareWeb(wdb,extra, context);
	// }
	// });
	//
	// return false;
	// }
	// return true;
	// }

	public static Bitmap map;

	public static boolean processeImg(final WeiXinDataBean wdb,
									  final String extra, final Context context) {
		if (wdb.thumbData == null) {

			ImageView iv = new ImageView(WxShopApplication.app);

			AQuery aQuery = new AQuery(WxShopApplication.app);
			aQuery.id(iv).image(wdb.imgUrl, true, true, 120, 0,
					new BitmapAjaxCallback() {

						@Override
						protected void callback(String url, ImageView iv,
												Bitmap bm, AjaxStatus status) {
							map = bm;
							wdb.thumbData = BitmapUtil.bmpToByteArray(bm, true);
							shareWeb(wdb, extra, context);
							super.callback(url, iv, bm, status);
						}

					});

			return false;
		}
		return true;
	}

	// ImageView iv = new ImageView(WxShopApplication.app);
	//
	// AQuery aQuery = new AQuery(WxShopApplication.app);
	// aQuery.id(iv).image(wdb.imgUrl,
	// true, true, 120, 0,
	// new BitmapAjaxCallback() {
	//
	// @Override
	// protected void callback(String url, ImageView iv,
	// Bitmap bm, AjaxStatus status) {
	// wdb.thumbData = BitmapUtil.bmpToByteArray(bm, true);
	// shareWeb(wdb);
	// super.callback(url, iv, bm, status);
	// }
	//
	// });

}
