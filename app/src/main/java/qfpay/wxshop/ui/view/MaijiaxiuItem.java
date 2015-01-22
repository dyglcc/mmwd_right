package qfpay.wxshop.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.MaijiaxiuDelNetImpl;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseActivity_;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseNetProcesser;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.loveplusplus.demo.image.ImagePagerActivity;

public class MaijiaxiuItem extends LinearLayout {
	BuyersShowReleaseNetProcesser bsrsp;
	TextView tv_text;
	TextView tv_date;
	TextView tv_time;
	BuyerShowBean gb;
	private AQuery aquery;
	private Context context;
	private MaijiaxiuFragment fragment;
	private Handler handler;
	Button btn_del, btn_edit;
	ImageView iv_0, iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_extra_1;
	LinearLayout jiugongge;
	View line1, line2;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public MaijiaxiuItem(Context context) {
		super(context);
	}

	public MaijiaxiuItem(Context context, MaijiaxiuFragment fragment, BuyerShowBean gb, AQuery aq,
			Handler handler, int pos, BuyersShowReleaseNetProcesser bean) {
		this(context);
		LayoutInflater.from(getContext()).inflate(R.layout.list_item_maijiaxiu,
				this);
		this.handler = handler;
		this.aquery = aq;
		this.context = context;
		this.fragment = fragment;

		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_date = (TextView) findViewById(R.id.tv_date);
		jiugongge = (LinearLayout) findViewById(R.id.layout_images);
		iv_0 = (ImageView) findViewById(R.id.iv_0);
		iv_1 = (ImageView) findViewById(R.id.iv_1);
		iv_2 = (ImageView) findViewById(R.id.iv_2);
		iv_3 = (ImageView) findViewById(R.id.iv_3);
		iv_4 = (ImageView) findViewById(R.id.iv_4);
		iv_5 = (ImageView) findViewById(R.id.iv_5);
		iv_6 = (ImageView) findViewById(R.id.iv_6);
		iv_7 = (ImageView) findViewById(R.id.iv_7);
		iv_8 = (ImageView) findViewById(R.id.iv_8);
		iv_extra_1 = (ImageView) findViewById(R.id.iv_extra_0);
		btn_del = (Button) findViewById(R.id.btn_del);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_time = (TextView) findViewById(R.id.tv_time);

		line1 = findViewById(R.id.line_1);
		line2 = findViewById(R.id.line_2);
		setValues(gb, pos);
		this.bsrsp = bean;

	}

	private int getIndex(int pos, List<ImageBean> hm_images) {

		if (pos == 4 && hm_images.size() == 4) {
			return 2;
		}
		if (pos == 5 && hm_images.size() == 4) {
			return 3;
		}
		return pos;
	}

	private void addListener(View view, final int position,
			final List<ImageBean> list) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 得到url
				int len = list.size();
				String[] urls = new String[len];
				for (int i = 0; i < len; i++) {
					ImageBean ib = list.get(i);
					urls[i] = ib.getUrl();
				}

				Intent intent = new Intent(context, ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
				context.startActivity(intent);
			}
		});
	}

	private void addListener(View view, final int position, final String url) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
						new String[] { url });
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
				context.startActivity(intent);
			}
		});
	}

	private void setTextImageListener(TextView tv_text) {
		tv_text.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility") public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Layout layout = ((TextView) v).getLayout();
					int x = (int) event.getX();
					int y = (int) event.getY();
					if (layout != null) {
						int line = layout.getLineForVertical(y);
						int offset = layout.getOffsetForHorizontal(line, x);
						T.i("index" + offset);
						if (offset == gb.getContent().length() + 1) {

                            CommonWebActivity_.intent(context).url("http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/"
                                    + gb.getGood_id()
                                    + "?from=app_preview").title("查看商品详情").start();

						}
					}
				}
				return true;
			}
		});
	}

	public void setValues(BuyerShowBean gb, int pos) {
		this.gb = gb;

		if (gb == null) {
			return;
		}
		String html = gb.getContent();
		if (gb.getGood_id() != null && !gb.getGood_id().equals("0")
				&& !gb.getGood_id().equals("")) {
			html += "<img src=\"\">";
			CharSequence charSequence = Html.fromHtml(html, new ImageGetter() {
				@Override
				public Drawable getDrawable(String arg0) {
					Drawable drawable = context.getResources().getDrawable(
							R.drawable.maijiaxiu_link);
					// 下面这句话不可缺少
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					return drawable;
				}
			}, null);

			tv_text.setText(charSequence);
		} else {
			tv_text.setText(html);
		}

		tv_date.setText(MaijiaxiuFragment.dateStrs.get(pos));
		tv_text.setTag(pos);
		setTextImageListener(tv_text);
		List<ImageBean> images = gb.getHm_images();
		displayImages(images);

		// button del edit
		setclickListener(gb, pos);

		addListener(iv_0, 0, gb.getHm_images());
		addListener(iv_1, 1, gb.getHm_images());
		addListener(iv_2, 2, gb.getHm_images());
		addListener(iv_3, 3, gb.getHm_images());
		addListener(iv_4, getIndex(pos, gb.getHm_images()), gb.getHm_images());
		addListener(iv_5, getIndex(pos, gb.getHm_images()), gb.getHm_images());
		addListener(iv_6, 6, gb.getHm_images());
		addListener(iv_7, 7, gb.getHm_images());
		addListener(iv_8, 8, gb.getHm_images());

		List<ImageBean> hm_images = gb.getHm_images();
		if (hm_images.size() != 0) {
			ImageBean ib = hm_images.get(0);
			if (ib != null && ib.getUrl() != null && !ib.getUrl().equals("")) {
				addListener(iv_extra_1, 0, ib.getUrl());
			}
		}
		if (currentDiffNext(gb, pos)) {
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.GONE);

		} else {
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.VISIBLE);
		}
		//
		String updateTime = getTimeStr(gb.getUpdate_time(), pos);
		tv_time.setText(updateTime);

	}

	private String getTimeStr(String update_time, int pos) {
		try {
			Date dateNow = new Date();
			Date date = format.parse(update_time);
			long dateM = date.getTime();
			long deta = ((dateNow.getTime() - dateM) / 1000 / 24 / 60 / 60);
			if (pos == 0) {
				if (deta == 0) {
					if ((dateNow.getTime() - dateM) < ConstValue.minute5) {
						return "刚刚";
					}
				}
			}
			if (deta == 0) {
				return update_time.substring(11, update_time.length() - 3);
			} else {
				return deta + "天前";
			}
		} catch (ParseException e) {
			T.e(e);
		}
		return "发布时间";
	}

	private boolean currentDiffNext(BuyerShowBean gb2, int pos) {
		if (pos + 1 >= MaijiaxiuFragment.data.size()) {
			return true;
		}
		BuyerShowBean buyerShowBean = MaijiaxiuFragment.data.get(pos + 1);
		if (buyerShowBean == null) {
			return true;
		}
		CharSequence dateString = buyerShowBean.getChineseDate();
		if (dateString == null) {
			return true;
		}
		CharSequence dateString2 = gb2.getChineseDate();
		if (dateString.equals(dateString2)) {
			return true;
		} else {
			return false;
		}
	}

//	private CharSequence getDateString(int pos, String datestr) {
//		if (MaijiaxiuFragment.dateStrs.containsKey(pos)) {
//			return MaijiaxiuFragment.dateStrs.get(pos);
//		}
////		if (datestr != null) {
////			if (MaijiaxiuFragment.dateStrs.containsValue(datestr)) {
////				MaijiaxiuFragment.dateStrs.put(pos, "");
////			} else {
////				MaijiaxiuFragment.dateStrs.put(pos, datestr);
////			}
////		} else {
////			return null;
////		}
//
//		return MaijiaxiuFragment.dateStrs.get(pos);
//	}

	private void displayImages(List<ImageBean> images) {
		int size = images.size();
		if (size > 1) {
			initView2Gone(images);
		}
		switch (size) {
		case 0:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.GONE);
			break;
		case 1:
			iv_extra_1.setVisibility(View.VISIBLE);
			jiugongge.setVisibility(View.GONE);
			// VISIBLEImages(images, iv_extra_1);
			setExtraImage(images.get(0));
			break;
		case 2:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1);
			break;
		case 3:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2);
			break;
		case 4:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_3, iv_4);
			break;
		case 5:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2, iv_3, iv_4);
			break;
		case 6:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2, iv_3, iv_4, iv_5);
			break;
		case 7:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2, iv_3, iv_4, iv_5, iv_6);
			break;
		case 8:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2, iv_3, iv_4, iv_5, iv_6,
					iv_7);
			break;
		case 9:
			iv_extra_1.setVisibility(View.GONE);
			jiugongge.setVisibility(View.VISIBLE);
			VISIBLEImages(images, iv_0, iv_1, iv_2, iv_3, iv_4, iv_5, iv_6,
					iv_7, iv_8);
			break;
		default:
			break;
		}

	}

	private void setExtraImage(ImageBean imageBean) {

		if (imageBean == null || imageBean.getUrl() == null
				|| imageBean.getUrl().equals("")) {
			return;
		}
		aquery.id(iv_extra_1).image(
				Utils.getThumblePic(imageBean.getUrl(), 400), true, true, 0,
				R.drawable.list_item_default);

	}

	/**
	 * 初始化九宫格
	 * 如果只有1，2，3张图片，那么隐藏第二三排
	 * 同理，4，5，6张图片，隐藏第三排
	 * 有6，7，8张图片的话三排全部显示。
	 * */
	private void initView2Gone(List<ImageBean> images) {
		for (int i = 0; i < jiugongge.getChildCount(); i++) {
			LinearLayout viewchild = (LinearLayout) jiugongge.getChildAt(i);
			viewchild.setVisibility(View.VISIBLE);
			for (int j = 0; j < viewchild.getChildCount(); j++) {
				View view = viewchild.getChildAt(j);
				view.setVisibility(View.INVISIBLE);
			}
		}
		int size = images.size();
		switch (size) {
		case 1:
		case 2:
		case 3:
			jiugongge.getChildAt(0).setVisibility(View.VISIBLE);
			jiugongge.getChildAt(1).setVisibility(View.GONE);
			jiugongge.getChildAt(2).setVisibility(View.GONE);
			break;
		case 4:
		case 5:
		case 6:
			jiugongge.getChildAt(0).setVisibility(View.VISIBLE);
			jiugongge.getChildAt(1).setVisibility(View.VISIBLE);
			jiugongge.getChildAt(2).setVisibility(View.GONE);
			break;
		case 7:
		case 8:
		case 9:
			jiugongge.getChildAt(0).setVisibility(View.VISIBLE);
			jiugongge.getChildAt(1).setVisibility(View.VISIBLE);
			jiugongge.getChildAt(2).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

	// private ImageOptions getImageOption() {
	// ImageOptions options = new ImageOptions();
	// // options.round = 5;
	// options.fileCache = true;
	// return options;
	// }

	private void VISIBLEImages(List<ImageBean> images, ImageView... imageViews) {
		int size = images.size();
		if (imageViews.length != size) {
			return;
		}
		for (int i = 0; i < size; i++) {
			ImageBean imageb = images.get(i);
			if (imageb == null) {
				continue;
			}

			View parent = (View) imageViews[i].getParent();
			parent.setVisibility(View.VISIBLE);
			aquery.id(imageViews[i]).image(
					Utils.getThumblePic(imageb.getUrl(), 260), true, true, 0,
					R.drawable.list_item_default);
		}
	}

	private void setclickListener(final BuyerShowBean gb, final int pos) {

		btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobAgentTools.OnEventMobOnDiffUser(context,
						"click_maijiaxiu_del");

				showDialogConfirm("确定要删除嘛?", pos);
			}
		});

		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobAgentTools.OnEventMobOnDiffUser(context,
						"click_maijiaxiu_edit");

				bsrsp.setMaijiaxiuLinstener(fragment, pos);
				BuyersShowReleaseActivity_.intent(fragment).bean(gb)
						.startForResult(MaijiaxiuFragment.ACTION_EDIT_MAIJIAXIU);
			}
		});

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

						MaijiaxiuDelNetImpl netDel = new MaijiaxiuDelNetImpl(
								(Activity) context);
						Bundle bun = new Bundle();
						bun.putString("mid", gb.getMid());
						netDel.request(bun, new MainHandler(context) {

							@Override
							public void onSuccess(Bundle bundle) {
								Message msg = handler.obtainMessage();
								Bundle bun = new Bundle();
								bun.putInt("pos", pos);
								bun.putSerializable("buyshowbean", gb);
								msg.setData(bun);
								msg.what = MaijiaxiuFragment.MAIJIAXIU_DEL;
								handler.sendMessage(msg);
							}

							@Override
							public void onFailed(Bundle bundle) {

							}
						});

					}
				});
	}

	public BuyerShowBean getBuyerShowBean() {
		return gb;
	}

}
