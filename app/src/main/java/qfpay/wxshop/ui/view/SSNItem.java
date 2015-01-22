package qfpay.wxshop.ui.view;

import java.text.SimpleDateFormat;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.*;
import qfpay.wxshop.activity.SSNPublishActivity;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.netImpl.SSnDelNetImpl;
import qfpay.wxshop.ui.main.fragment.SSNListFragment;
import qfpay.wxshop.ui.web.*;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SSNItem extends LinearLayout {
	TextView tv_title, tv_read, tv_zan, tv_month, tv_day;
	SSNItemBean gb;
	private Context context;
	private SSNListFragment fragment;
	private Handler handler;
	LinearLayout btn_del, btn_edit, btn_share, layout_date;
	ImageView iv_extra_1, iv_demo;
	View line1, layout_img, layout_read_info;
	View layout_action;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int width, height;

	public SSNItem(Context context) {
		super(context);
	}

	public SSNItem(Context context, SSNListFragment fragment, SSNItemBean gb,
			Handler handler, int pos, int widthPixels) {
		this(context);
		LayoutInflater.from(getContext()).inflate(R.layout.list_item_ssn, this);
		this.handler = handler;
		this.context = context;
		this.fragment = fragment;

		tv_day = (TextView) findViewById(R.id.tv_day);
		tv_month = (TextView) findViewById(R.id.tv_month);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_read = (TextView) findViewById(R.id.tv_read);
		// tv_today_read = (TextView) findViewById(R.id.tv_today_read);
		tv_zan = (TextView) findViewById(R.id.tv_zan);

		iv_extra_1 = (ImageView) findViewById(R.id.iv_extra_0);
		iv_demo = (ImageView) findViewById(R.id.iv_demo);
		btn_del = (LinearLayout) findViewById(R.id.layout_del);
		btn_share = (LinearLayout) findViewById(R.id.layout_share);
		btn_edit = (LinearLayout) findViewById(R.id.layout_edit);
		layout_date = (LinearLayout) findViewById(R.id.layout_date);
		layout_action = findViewById(R.id.layout_action);

		layout_read_info = findViewById(R.id.layout_img);
		layout_img = findViewById(R.id.layout_read_info);

		line1 = findViewById(R.id.line_1);
		android.view.ViewGroup.LayoutParams layoutParams = iv_extra_1
				.getLayoutParams();
		width = RectangleLayout.measuredWidth;
		layoutParams.height = (int) (widthPixels * 0.48f);
		height = layoutParams.height;
		iv_extra_1.setLayoutParams(layoutParams);
		setValues(gb, pos);

		

	}

	private void iv_demoSetting(SSNItemBean gb2) {
		// gb2.
	}

	public void setValues(SSNItemBean gb, int pos) {
		this.gb = gb;

		if (gb == null) {
			return;
		}
		tv_read.setText(gb.getAlluv() + "");
		// tv_today_read.setText(gb.getToday_read() + "");
		tv_zan.setText(gb.getLikes() + "");
		tv_title.setText(gb.getTitle());
		if (gb.getUpdate_time() != null && gb.getUpdate_time().length() > 10) {
			CharSequence dateString = SSNListFragment.dateStrs.get(pos);
			if (dateString != null && !dateString.equals("")) {
				String str = gb.getUpdate_time();
				layout_date.setVisibility(View.VISIBLE);
				tv_month.setText(str.substring(5, 7) + "月");
				tv_day.setText(str.substring(8, 10));
			} else {
				layout_date.setVisibility(View.INVISIBLE);
			}
		}
		if (gb.getId().equals(SSNListFragment.DEMO_ID)) {
			iv_demo.setVisibility(View.VISIBLE);
			layout_action.setVisibility(View.GONE);
		} else {
			iv_demo.setVisibility(View.GONE);
			layout_action.setVisibility(View.VISIBLE);
		}
		setclickListener(gb, pos);
		setExtraImage(gb.getImg_url());
		iv_demoSetting(gb);

	}

	private void setExtraImage(String imageBean) {
		if (imageBean == null || imageBean.equals("")) {
			iv_extra_1.setBackgroundResource(R.drawable.list_item_default);
			return;
		}

		Picasso.with(context).load(Utils.getThumblePic(imageBean, 450,450)).fit().error(R.drawable.list_item_default).centerCrop().into(iv_extra_1);
	}

	private void setclickListener(final SSNItemBean gb, final int pos) {

		btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobAgentTools.OnEventMobOnDiffUser(context,
						"Click_HybridText_Delete");

				showDialogConfirm("确定要删除嘛?", pos);
			}
		});

		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobAgentTools.OnEventMobOnDiffUser(context,
						"Click_HybridText_Edit");
				SSNEditActivity_.intent(context).item(gb).editpos(pos)
						.startForResult(SSNEditActivity.SSN_EDIT);
			}
		});
		iv_extra_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				go2SsnPreviewActivity();
			}
		});
		layout_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				go2SsnPreviewActivity();
			}
		});
		btn_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				MobAgentTools.OnEventMobOnDiffUser(context,
						"Click_HybridText_Share");
				fragment.sharebean = gb;
				handler.sendEmptyMessage(SSNListFragment.SSN_SHARE);
				
			}
		});

	}

	protected void go2SsnPreviewActivity() {
		String title = "碎碎念";
		if (gb == null || gb.getId() == null) {
			Toaster.l(context, "碎碎念数据异常");
			return;
		}
		if (gb.getTitle() != null) {
			title = gb.getTitle();
		}
		CommonWebActivity_
				.intent(context)
				.url(Utils.getSSNurl(gb)).title(title).start();
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
		FragmentActivity activity = (FragmentActivity) context;
		Utils.showNativeDialog(activity, context.getString(R.string.mm_hint),
				content, context.getString(R.string.cancel),
				context.getString(R.string.OK), false, -1,
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						SSnDelNetImpl netDel = new SSnDelNetImpl(
								(Activity) context);
						Bundle bun = new Bundle();
						bun.putString("mid", gb.getMid());
						netDel.request(bun, new MainHandler(context) {

							@Override
							public void onSuccess(Bundle bundle) {
								Message msg = handler.obtainMessage();
								Bundle bun = new Bundle();
								bun.putInt("pos", pos);
								bun.putSerializable(
										SSNPublishActivity.SSN_DEL_BEAN, gb);
								msg.setData(bun);
								msg.what = SSNListFragment.SSN_DEL;
								handler.sendMessage(msg);
							}

							@Override
							public void onFailed(Bundle bundle) {

							}
						});

					}
				});
	}

//	public static CharSequence getDateString(int pos, String datestr) {
//		if (SSNListFragment.dateStrs.containsKey(pos)) {
//			return SSNListFragment.dateStrs.get(pos);
//		}
//		if (datestr != null) {
//			if (SSNListFragment.dateStrs.containsValue(datestr)) {
//				SSNListFragment.dateStrs.put(pos, "");
//			} else {
//				SSNListFragment.dateStrs.put(pos, datestr);
//			}
//		} else {
//			return null;
//		}
//
//		return SSNListFragment.dateStrs.get(pos);
//	}
}
