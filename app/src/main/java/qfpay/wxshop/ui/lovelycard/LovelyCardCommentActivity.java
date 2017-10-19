package qfpay.wxshop.ui.lovelycard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import org.androidannotations.annotations.sharedpreferences.Pref;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.LovelyCardNetService;
import qfpay.wxshop.data.netImpl.LovelyCardNetService.CommentBean;
import qfpay.wxshop.data.netImpl.LovelyCardNetService.CommonNetBean;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.*;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.ui.view.XListView.IXListViewListener;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
/**
 * 萌片页描述编辑页
 * */
@EActivity(R.layout.lovelycard_comment)
public class LovelyCardCommentActivity extends BaseActivity implements IXListViewListener {
	private static final int LIST_PAGE_LENGTH = 15;

	@ViewById XListView listView;
    @ViewById RelativeLayout rl_null;

	@Bean RetrofitWrapper mNetWrapper;
	@Pref AppStateSharePreferences_ pref;

	private LovelyCardNetService mNetService;
	private List<CommentBean> mData = new ArrayList<CommentBean>();
	private MyAdapter mAdapter;
	private int mCurrentIndex = 1;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@AfterViews
	void onInit() {
		// 处理ActionBar相关内容
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("萌片页评论");
		// 初始化ListView
        mAdapter = new MyAdapter();
		listView.setAdapter(mAdapter);
		listView.setPullRefreshEnable(true);
		listView.setAutoLoadEnable(false);
		listView.setXListViewListener(this);
		listView.setPullLoadEnable(false);
		// 初始化网络框架
		String url = WxShopApplication.app.getDomainMMWDUrl();
		if (!url.contains("http://")) {
			url = "http://" + url;
		}
		mNetService = mNetWrapper.getNetService(LovelyCardNetService.class, url);
		// 获取数据并显示
		loadData(true);
	}

	/**
	 * 从服务器中获取数据,包括重载和获取更多
	 * @param isReload 是否是重载
	 */
	@Background
	void loadData(boolean isReload) {
		int start = mCurrentIndex;
		if (isReload) {
			// 列表数据获取从1开始
			start = 1;
		}

		try {
			CommonNetBean bean = mNetService.getCommentList(WxShopApplication.dataEngine.getShopId(), start, LIST_PAGE_LENGTH);
			if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				List<CommentBean> result = bean.data.records;
				onDataLoaded(result, isReload);
			} else {
				toast(bean.getResperr());
			}
		} catch (Exception e) {
			T.e(e);
		}
	}

	@UiThread
	void onDataLoaded(List<CommentBean> data, boolean isClear) {
		processListViewWithNewData(data);
		if (mData == null) mData = new ArrayList<CommentBean>();
		if (isClear) mData.clear();
		mData.addAll(data);
		mCurrentIndex = mData.size() + 1;
		mAdapter.notifyDataSetChanged();
		// 保存最新的评论时间,用于首页判断小红点
		if (!mData.isEmpty())
			pref.theFirstLCTime().put(mData.get(0).created);
		else
			rl_null.setVisibility(View.VISIBLE);
	}

	/**
	 * 在获取数据成功以后处理ListView的状态
	 */
	private void processListViewWithNewData(List<CommentBean> data) {
		// 取消刷新状态
		listView.stopRefresh();
		listView.stopLoadMore();
		// 判断是否还有下一页
		if (data.size() < LIST_PAGE_LENGTH) {
			listView.setPullLoadEnable(false);
		} else {
			listView.setPullLoadEnable(true);
		}
	}

	@Override
	public void onRefresh() {
		loadData(true);
	}

	@Override
	public void onLoadMore() {
		loadData(false);
	}

	class MyAdapter extends BaseAdapter {
		String format = "yyyy-MM-dd HH:mm:ss";

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public CommentBean getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(LovelyCardCommentActivity.this).inflate(R.layout.lovelycard_comment_item, parent, false);
			}
			
			ImageView icon    = (ImageView) convertView.findViewById(R.id.iv);
			TextView  title   = (TextView)  convertView.findViewById(R.id.tv_title);
			TextView  content = (TextView)  convertView.findViewById(R.id.tv_content);
			TextView  date    = (TextView)  convertView.findViewById(R.id.tv_date);

			if (getItem(position).userinfo != null) {
				Picasso.with(LovelyCardCommentActivity.this).load(getItem(position).userinfo.headimgurl).into(icon);
				title.setText(getItem(position).userinfo.nickname);
			}
			content.setText(getItem(position).content);

			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				Date time = dateFormat.parse(getItem(position).created);
				date.setText(new SimpleDateFormat("MM.dd HH:mm").format(time));
			} catch (ParseException e) {
				T.e(e);
			}
			return convertView;
		}
		
	}
	
	@UiThread
	void toast(String msg) {
		Toaster.s(this, msg);
	}
}
