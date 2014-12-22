package qfpay.wxshop.data.handler;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.Toaster;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class MainHandler extends Handler {
	
	Context context;
	Handler handler;
	public MainHandler(Context context){
		this.context = context;
	}
	public MainHandler(Context context,Handler handler){
		this.context = context;
		this.handler = handler;
	}
	private void showErrorMsg(String errorMsg){
		if(handler!=null){
			handler.sendEmptyMessage(ConstValue.MSG_ERROR_FROM_MAINHANDLER);
		}
//		T.w(errorMsg);
		Toaster.l(context,errorMsg);
	}
	public abstract void onSuccess(Bundle bundle);
	public abstract void onFailed(Bundle bundle);
	public void handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		if(handler!=null){
			handler.sendEmptyMessage(ConstValue.MSG_ERROR_FROM_MAINHANDLER);
		}
		switch (msg.what) {
		case ConstValue.NET_RETURN_SUCCESS:

			if (bundle != null) {
					
					String errorString = bundle.getString(ConstValue.ERROR_MSG);
					if(ConstValue.SUCCESS.equals(errorString)){
						onSuccess(bundle);
					}else{
						onFailed(bundle);
						showErrorMsg(errorString);
					}
			}

			break;
		case ConstValue.NET_RETURN_HTTP:// 服务器返回的401之类的，权限验证没通过
			if (401 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				showErrorMsg(context.getResources().getString(R.string.error404));
			} else if (404 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				showErrorMsg(context.getResources().getString(R.string.error404));
			} else if (500 == bundle.getInt(ConstValue.NET_RETURN_HTTP_ERROR)) {
				showErrorMsg(context.getResources().getString(R.string.error500));
			}
			break;
		case ConstValue.NET_RETURN_NO_CONNECT:// 无网络
			showErrorMsg(context.getResources().getString(R.string.errorNoNet));
			break;
		case ConstValue.NET_RETURN_CANCEL:// 用户取消
			break;
		case ConstValue.NET_ACCESS_REFUSE:// 访问拒绝
			showErrorMsg(context.getResources().getString(R.string.access_refused));
			break;
		case ConstValue.NET_UNKNOWHOST_EXCEPTION:// 无法解析服务器
			showErrorMsg(context.getResources().getString(R.string.bad_net));
			break;
		case ConstValue.NET_CONNECT_SERVER_ERROR:// 无法连接服务器
			showErrorMsg(context.getResources().getString(R.string.error500));
			break;
		case ConstValue.NET_RETURN_TIMEOUT:// 超时
			showErrorMsg(context.getResources().getString(R.string.timeout));
			break;
		default:
			showErrorMsg(context.getResources().getString(R.string.error500));
			break;
		}
	}

};
