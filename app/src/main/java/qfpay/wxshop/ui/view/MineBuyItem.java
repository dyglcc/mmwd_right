package qfpay.wxshop.ui.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.MyHuoyuanItemBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.netImpl.FactoryContractNetImpl;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.ui.web.huoyuan.CommonWebActivityHuoyuan_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.list_item_my_huoyuan)
public class MineBuyItem extends LinearLayout {
	@ViewById
	View btn_contract, btn_order, parent;
	@ViewById
	TextView tv_goods_name, tv_order_status, tv_datestr, tv_count, tv_amount;
	@ViewById
	ImageView iv_contract_b, iv_order_detail, iv_order_status, iv_official;
	MyHuoyuanItemBean gb;
	private Context context;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日购买");
	private SimpleDateFormat dateSrcFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public MineBuyItem(Context context) {
		super(context);
		this.context = context;
	}

	public void setValues(MyHuoyuanItemBean gb, Context context) {
		this.gb = gb;

		if (gb == null) {
			return;
		}

		tv_goods_name.setText(gb.getGood_name());
		tv_order_status.setText(gb.getShip_status());
		tv_count.setText(gb.getAmount());
		tv_amount.setText(gb.getPayable() + "元");
		tv_datestr.setText(getDateStr(gb.getCreate_time()));

		setOrderStats(gb);

		setExtraImage(gb.getGood_img());

		setclickListener(gb);

	}

	private String getDateStr(String create_time) {
		String str = "";
		if (create_time == null || create_time.equals("")) {
			return str;
		}
		try {
			Date date = dateSrcFormat.parse(create_time);
			str = dateFormat.format(date);
		} catch (ParseException e) {
			T.e(e);
		}
		return str;
	}

	private void setOrderStats(MyHuoyuanItemBean gb2) {

		if (gb2 == null || gb2.getStatus().equals("")) {
			return;
		}
		int status = Integer.parseInt(gb2.getStatus());
		switch (status) {
		case 4:
			tv_order_status.setTextColor(R.color.text_color_green);
			tv_order_status.setText("已发货");
			iv_order_status.setImageResource(R.drawable.icon_hy_done);
			break;

		// case 1:
		// tv_order_status.setTextColor(R.color.text_content);
		// iv_order_status.setImageResource(R.drawable.icon_hy_normal);
		// tv_order_status.setText("待付款");
		// break;
		// case 2:
		// tv_order_status.setTextColor(R.color.text_content);
		// iv_order_status.setImageResource(R.drawable.icon_hy_normal);
		// tv_order_status.setText("已付款");
		// break;
		default:
			tv_order_status.setTextColor(R.color.text_content);
			iv_order_status.setImageResource(R.drawable.icon_hy_normal);
			tv_order_status.setText("未发货");
			break;
		}
	}

	private void setExtraImage(String imageBean) {
		if (imageBean == null || imageBean.equals("")) {
			iv_official.setBackgroundResource(R.drawable.list_item_default);
			return;
		}

		Picasso.with(context).load(Utils.getThumblePic(imageBean, 450, 450))
				.fit().error(R.drawable.list_item_default).centerCrop()
				.into(iv_official);
	}

	private void setclickListener(final MyHuoyuanItemBean gb) {

		btn_contract.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AbstractNet net = new FactoryContractNetImpl(context);
				Bundle bun = new Bundle();
				bun.putString("qf_uid", gb.getQf_uid());
				bun.putString("order", gb.getId());
				net.request(bun, new MainHandler(context) {

					@Override
					public void onSuccess(Bundle bundle) {
						final Map<String, String> map = getStringContract(bundle);
						if (map.size() == 0) {
							Toaster.l(context, "没有联系方式");
							return;
						}
						showDialogContract(bundle);
					}

					// private String[] getStringarrays(Map<String, String> map)
					// {
					// String[] strs = new String[map.size()];
					// int i = 0;
					// for (Entry<String, String> entry : map.entrySet()) {
					// strs[i++] = entry.getKey();
					// }
					// // string 放 key ，qq，weixin，mobile
					// for (int j = 0; j < strs.length; j++) {
					// // 放value
					// map.put(j + "", map.get(strs[j]));
					// }
					// return strs;
					// }

					@Override
					public void onFailed(Bundle bundle) {

					}
				});

			}
		});
		btn_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(context!=null){
					MobAgentTools.OnEventMobOnDiffUser(context, "click_Supply_of_goods_Order_details");
				}
				
				go2DetailActivity();
			}
		});
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (gb == null || gb.getGoodid() != null
						&& !gb.getGoodid().equals("")) {
					CommonWebActivityHuoyuan_
							.intent(context)
							.url("http://"+ WxShopApplication.app.getDomainMMWDUrl()+"/h5/b-item.html?itemid=" + gb.getGoodid())
							.title("商品详情")
							.startForResult(
									MaijiaxiuFragment.ACTION_HUOYUAN_ADD);
				}

			}
		});

	}

	protected Map<String, String> getStringContract(Bundle bun) {
		Map<String, String> map = new HashMap<String, String>();
		String qq = bun.getString("qq");
		String mobile = bun.getString("mobile");
		String weixin = bun.getString("weixin");
		if (qq != null && !qq.equals("")) {
			map.put("QQ号：" + qq, qq);
		}
		if (mobile != null && !mobile.equals("")) {
			map.put("电话：" + mobile, mobile);
		}
		if (weixin != null && !weixin.equals("")) {
			map.put("微信：" + weixin, weixin);
		}

		return map;
	}

	protected void go2DetailActivity() {
		String title = "订单详情";
		if (gb == null || gb.getId() == null) {
			Toaster.l(context, "订单数据异常");
			return;
		}
		CommonWebActivity_.intent(context).url(getDetailUrl(gb)).title(title)
				.start();
	}

	private String getDetailUrl(MyHuoyuanItemBean gb2) {
		return "http://"+WxShopApplication.app.getDomainMMWDUrl() + "/my/order_detail/"
				+ gb2.getId();
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

	// private void openApp() {
	// Intent intent = new Intent();
	// ComponentName cmp = new ComponentName("com.tencent.mm",
	// "com.tencent.mm.ui.LauncherUI");
	// intent.setAction(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_LAUNCHER);
	// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.setComponent(cmp);
	// try {
	// ((Activity) context).startActivityForResult(intent, 0);
	// } catch (Exception e) {
	// Toaster.l(context, context.getString(R.string.install_weichat_qin)
	// + context.getString(R.string.focus_on_qmm_weichat));
	// }
	// }

	private void showDialogContract(Bundle bundle) {

		final Dialog dialog = new Dialog(context, R.style.myDialogTheme);

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_contract_layout, null);
		dialog.setContentView(v);

		View line1 = v.findViewById(R.id.line1);
		View line2 = v.findViewById(R.id.line2);
		View parent = v.findViewById(R.id.parent);
		// View line3 = v.findViewById(R.id.line3);

        parent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

		TextView tvQQ = (TextView) v.findViewById(R.id.tv_qq);
		final String qq = bundle.getString("qq");
		tvQQ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toaster.l(context, "已复制QQ号");
				Utils.saveClipBoard((Activity) context, qq);
				dialog.dismiss();
			}
		});

		if (qq != null && !qq.equals("")) {
			tvQQ.setText("QQ号：" + qq);
		} else {
			tvQQ.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
		}

		TextView tvWeixin = (TextView) v.findViewById(R.id.tv2);
		final String weixin = bundle.getString("weixin");
		tvWeixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toaster.l(context, "已复制微信号");
				Utils.saveClipBoard((Activity) context, weixin);
				dialog.dismiss();
			}
		});
		if (weixin != null && !weixin.equals("")) {
			tvWeixin.setText("微信号：" + weixin);
		} else {
			tvWeixin.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
		}
		TextView tvMobile = (TextView) v.findViewById(R.id.tv3);
		final String mobile = bundle.getString("mobile");
		tvMobile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ mobile));
				context.startActivity(intent);
				dialog.dismiss();
			}
		});
		if (mobile != null && !mobile.equals("")) {
			tvMobile.setText("电话：" + mobile);
		} else {
			tvMobile.setVisibility(View.GONE);
			// line3.setVisibility(View.GONE);
		}

		// dialog.setTitle("Custom Dialog");

		/*
		 * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
		 * 对象,这样这可以以同样的方式改变这个Activity的属性.
		 */
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

		/*
		 * lp.x与lp.y表示相对于原始位置的偏移.
		 * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
		 * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
		 * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
		 * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
		 * 当参数值包含Gravity.CENTER_HORIZONTAL时
		 * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
		 * 当参数值包含Gravity.CENTER_VERTICAL时
		 * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
		 * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
		 * Gravity.CENTER_VERTICAL.
		 * 
		 * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
		 * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
		 * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
		 */
		// lp.x = 100; // 新位置X坐标
		// lp.y = 100; // 新位置Y坐标
		// lp.width = 300; // 宽度
		// lp.height = 300; // 高度
		lp.alpha = 1f; // 透明度

		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);

		/*
		 * 将对话框的大小按屏幕大小的百分比设置
		 */
		// WindowManager m = ((Activity) context).getWindowManager();
		// Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		// WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		// // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		// p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
		// dialogWindow.setAttributes(p);

		dialog.show();

	}
}
