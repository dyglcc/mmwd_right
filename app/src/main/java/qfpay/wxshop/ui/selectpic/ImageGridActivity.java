package qfpay.wxshop.ui.selectpic;

import java.io.File;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.ui.commodity.detailmanager.ItemDetailManagerActivity_;
import qfpay.wxshop.ui.selectpic.ImageGridAdapter.TextCallback;
import qfpay.wxshop.utils.Toaster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	TextView bt;
	private HorizontalScrollView horiScroll;
	private LinearLayout layout;
	public static final int REFRESH_HORISCROLLVIEW_ADD = 10;
	public static final int REFRESH_HORISCROLLVIEW_REMOVE = REFRESH_HORISCROLLVIEW_ADD + 1;
	private AQuery aq;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
                Toaster.s(ImageGridActivity.this, "最多选择9张图片");
				break;
			case REFRESH_HORISCROLLVIEW_ADD:
				Bundle bunAdd = msg.getData();
				ImageItem itemAdd = (ImageItem) bunAdd.getSerializable("item");
				addSelectedImageView(itemAdd);

				break;
			case REFRESH_HORISCROLLVIEW_REMOVE:
				Bundle bun = msg.getData();
				ImageItem item = (ImageItem) bun.getSerializable("item");
				removeImageView(item);

				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_grid);

		WxShopApplication.paths.clear();
		
		BitmapCache.getInstance().init(this);

		aq = new AQuery(this);
		layout = (LinearLayout) findViewById(R.id.selected_image_layout);
		horiScroll = (HorizontalScrollView) findViewById(R.id.scrollview);
		AlbumHelper.getHelper().init(this);

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		initView();
		bt = (TextView) findViewById(R.id.btn_ok);
//		bt.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				ArrayList<String> list = new ArrayList<String>();
//			}
//
//		});
		
		findViewById(R.id.btn_complete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ImageGridActivity.this, ItemDetailManagerActivity_.class);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			}
		});
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				WxShopApplication.paths.clear();
				startActivity(new Intent(ImageGridActivity.this,AlbumActivity.class));
			}
		});
		
	}

	protected void removeImageView(ImageItem item) {

		for(int i=0;i<layout.getChildCount();i++){
			View view = layout.getChildAt(i);
			ImageItem itemChild = (ImageItem) view.getTag();
			if(item == itemChild){
				layout.removeView(view);
				break;
			}
		}
	}

	protected void addSelectedImageView(final ImageItem itemAdd) {

		ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(
				R.layout.choose_imageview, layout, false);
		imageView.setTag(itemAdd);
		layout.addView(imageView);
//		imageView.setImageBitmap(BitmapFactory.decodeFile(itemAdd.thumbnailPath));
		aq.id(imageView).image(new File(itemAdd.imagePath),104);
		imageView.postDelayed(new Runnable() {
			@Override
			public void run() {

				int off = layout.getMeasuredWidth() - horiScroll.getWidth();
				if (off > 0) {
					horiScroll.smoothScrollTo(off, 0);
				}
			}
		}, 100);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WxShopApplication.paths.remove(itemAdd);
				itemAdd.isSelected= false;
				adapter.notifyDataSetChanged();
				removeImageView(itemAdd);
				bt.setText("已选" + "(" + layout.getChildCount() + "/9)");
			}
		});

	}

	private void initView() {
		BitmapCache cache = BitmapCache.getInstance();
		cache.init(this);
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler,cache);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				 bt.setText("已选" + "(" + count + "/9)");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
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
