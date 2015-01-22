package qfpay.wxshop.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.NoticeCenterActivity;
import qfpay.wxshop.data.beans.NoticeItemBean;
import qfpay.wxshop.ui.web.*;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@EViewGroup(R.layout.list_item_notice)
public class NoticeItem extends LinearLayout {
	@ViewById
	TextView tv_read,tv_date,tv_content,tv_detail;
	@ViewById
	ImageView iv_read;
	@ViewById
	View layout_parent;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format2 = new SimpleDateFormat("MM-dd");
	private Context context;
	
	private Handler handler;

	public NoticeItem(Context context) {
		super(context);
	}

	public void setValues(NoticeItemBean gb,Context context,Handler handler) {

		this.context = context;
		this.handler = handler;
		if (gb == null) {
			return;
		}
		
		tv_content.setText(gb.getContent());
		if(gb.getClickable()!=null && gb.getClickable().equals("1")){
			tv_detail.setVisibility(View.VISIBLE);
		}else{
			tv_detail.setVisibility(View.INVISIBLE);
		}
		showImg(gb);
		
		setDateStr(gb);

		setclickListener(gb);


	}

	private void showImg(NoticeItemBean gb2) {
		if(gb2.getUnread() == null){
			return;
		}
		if(gb2.getUnread().equals("0")){
			iv_read.setVisibility(View.INVISIBLE);
		}else if(gb2.getUnread().equals("1")){
			iv_read.setVisibility(View.VISIBLE);
		}
	}

	private void setDateStr(NoticeItemBean gb2) {
		try {
			Date date = format.parse(gb2.getCreated());
			String dateStr = format2.format(date);
			tv_date.setText(dateStr);
		} catch (ParseException e) {
			T.e(e);
		}
	}

	private void setclickListener(final NoticeItemBean gb2) {

		layout_parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(gb2.getClickable() == null){
					return;
				}
				if(gb2.getClickable().equals("1")){
					go2ReadingActivity(gb2);
				}
				
			}
		});

	}

	protected void go2ReadingActivity(NoticeItemBean gb) {
		gb.setUnread("0");
		CommonWebActivity_.intent(context)
				.url(gb.getLink())
				.title(gb.getTitle()).start();
		handler.sendEmptyMessage(NoticeCenterActivity.NOTIFY_DATA);
	}

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
