package qfpay.wxshop.ui.selectpic;

import java.io.Serializable;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AlbumActivity extends Activity {
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	List<ImageBucket> dataList;
	ListView listView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
//	public static Bitmap bimap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);
		helper = AlbumHelper.getHelper();
		helper.init(this);

		initData();
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		dataList = helper.getImagesBucketList(false);
//		bimap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.icon_addpic_unfocused);
		WxShopApplication.paths.clear();
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		BitmapCache instance = BitmapCache.getInstance();
		instance.init(this);
		listView = (ListView) findViewById(R.id.listview);
		adapter = new ImageBucketAdapter(AlbumActivity.this, dataList,instance);
		listView.setAdapter(adapter);
		
		
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				WxShopApplication.paths.clear();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
				 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
				 */
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				Intent intent = new Intent(AlbumActivity.this,
						ImageGridActivity.class);
				intent.putExtra(AlbumActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				finish();
			}

		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AlbumHelper.getHelper().Destory();
		BitmapCache.getInstance().destory();
		System.gc();
	}
}
