package qfpay.wxshop.ui.buyersshow;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import com.squareup.picasso.Picasso;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BuyersShowNetService;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.GoodNetWrapper;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.GoodWrapper;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@EActivity(R.layout.goodslistforbuyersshow_layout)
public class GoodsListForBuyersShowActivity extends BaseActivity {
	public static final String RESULT_GOOD_KEY = "data";
	
	@Bean RetrofitWrapper retrofitWrapper;
	@ViewById ListView listview;
	List<GoodWrapper> mGoods = new ArrayList<GoodWrapper>();
	
	@AfterViews
	void init() {
		getListFromServer();
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE)
	void getListFromServer() {
		try {
			GoodNetWrapper wrapper = retrofitWrapper.getNetService(BuyersShowNetService.class).getGoodList();
			if (wrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				List<GoodWrapper> goodsList = wrapper.getData().getGood();
				if (goodsList != null && !goodsList.isEmpty()) {
					mGoods = goodsList;
				}
				onListGetted();
			} else {
				showErrorMsg("服务器异常啦~检查一下重试吧~");
			}
		} catch (Exception e) {
			showErrorMsg("网络好像有点拥堵~重试一下吧~");
			e.printStackTrace();
		}
	}
	
	@UiThread
	void onListGetted() {
		listview.setAdapter(new GoodsListAdapter());
	}
	
	@ItemClick
	void listview(int position) {
		Intent intent = new Intent();
		intent.putExtra(RESULT_GOOD_KEY, mGoods.get(position));
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Click
	void btn_back() {
		finish();
	}
	
	class GoodsListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mGoods.size();
		}

		@Override
		public Object getItem(int position) {
			return mGoods.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GoodsListItem item = (GoodsListItem) convertView;
			if (item == null) {
				item = new GoodsListItem(GoodsListForBuyersShowActivity.this);
			}
			item.setData(mGoods.get(position));
			return item;
		}
	}
	
	class GoodsListItem extends LinearLayout {
		ImageView icon;
		TextView name;

		public GoodsListItem(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.goodslistforbuyersshow_item, this);
			icon = (ImageView) findViewById(R.id.iv_icon);
			name = (TextView) findViewById(R.id.tv_name);
		}
		
		public void setData(GoodWrapper gw) {
			if (gw == null) {
				icon.setVisibility(View.INVISIBLE);
				name.setText("不链接商品");
			} else {
				icon.setVisibility(View.VISIBLE);
				Picasso.with(getContext()).load(Utils.getThumblePic(gw.getGood_img(), 100, 100)).fit().centerCrop()
					.placeholder(R.drawable.list_item_default).error(R.drawable.list_item_default).into(icon);
				name.setText(gw.getGood_name());
			}
		}
	}
	
	@UiThread
	void showErrorMsg(String msg) {
		Toaster.s(this, msg);
	}
}
