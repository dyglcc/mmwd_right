package qfpay.wxshop.data.handler;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.UploadPicMulImpl;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

public abstract class MainHandlerMulSelectPics extends MainHandler {
	
	private UploadPicMulImpl net;
	public MainHandlerMulSelectPics(Context context,UploadPicMulImpl net){
		super(context);
		this.net = net;
	}
	
	protected boolean accessSuccess = false;

	public abstract void onSuccess(Bundle bundle);
	public abstract void onFailed(Bundle bundle);
	public abstract void onFinish(boolean success,UploadPicMulImpl net);
	public void handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		switch (msg.what) {
		case ConstValue.NET_RETURN_SUCCESS:

			if (bundle != null) {
					
					String errorString = bundle.getString(ConstValue.ERROR_MSG);
					if(ConstValue.SUCCESS.equals(errorString)){

						accessSuccess = true;
						onSuccess(bundle);
					}else{
						onFailed(bundle);
						T.w("出错了:"+errorString);
						Toaster.l(context,errorString);
					}
			}

			break;
		case ConstValue.NET_RETURN_HTTP:// 服务器返回的401之类的，权限验证没通过
			if (401 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				T.i( "401 error");
				Toaster.l(context,context.getResources().getString(R.string.error404));
			} else if (404 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				T.i( "401 error");
				Toaster.l(context,context.getResources().getString(R.string.error404));
			} else if (500 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				// showDialog(ConstValue.SERVER_ERROR);
				T.i( "error 500");
				Toaster.l(context,context.getResources().getString(R.string.error500));
			}
			break;
		case ConstValue.NET_RETURN_NO_CONNECT:// 无网络
			// showDialog(ConstValue.NET_ERROR);
			T.i( "无网路");
			Toaster.l(context,context.getResources().getString(R.string.errorNoNet));
			break;
		case ConstValue.NET_RETURN_CANCEL:// 用户取消
			break;
		case ConstValue.NET_CONNECT_SERVER_ERROR:// 无法连接服务器
			// showDialog(ConstValue.ACCESS_ERROR);
			T.i( "error 500");
			Toaster.l(context,context.getResources().getString(R.string.error500));
			break;
		case ConstValue.NET_RETURN_TIMEOUT:// 超时
			// showDialog(ConstValue.ACCESS_ERROR);
			T.i( "error 500");
			Toaster.l(context,context.getResources().getString(R.string.error500));
			break;
		default:
			T.i( "error 500");
			Toaster.l(context,context.getResources().getString(R.string.error500));
			break;
		}
		onFinish(accessSuccess,net);
	}

};
