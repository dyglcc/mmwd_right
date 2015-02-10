package qfpay.wxshop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.ImageWrapper;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * 店铺店铺图更换选择页面
 */

@EActivity(R.layout.selectpic_layout)
public class SelectPicActivity extends BaseActivity {
    public static final int REQUEST_SAVE = 103;

    @ViewById
    ListView listview;
    @ViewById
    TextView tv_title;
    @ViewById
    Button btn_complete;

    List<ImageWrapper> mDataList = new ArrayList<ImageWrapper>();

    @AfterViews
    void init() {
        tv_title.setText("更换店铺背景图");
        btn_complete.setVisibility(View.INVISIBLE);
        mDataList = getData();
        listview.setAdapter(new MyAdapter());
    }

    List<ImageWrapper> getData() {
        List<ImageWrapper> imgs = new ArrayList<ImageWrapper>();
        String[] urls = getResources().getStringArray(R.array.header_img_url);
        for (int i = 0; i < urls.length; i++) {
            ImageWrapper img = new ImageWrapper();
            img.setImgPath(urls[i]);
            imgs.add(img);
        }
        return imgs;
    }

    @Click
    void btn_back() {
        finish();
    }

    @ItemClick
    void listview(int position) {
        Intent intent = new Intent(this, ShopHeaderPreviewActivity_.class);
        intent.putExtra(ConstValue.TITLE, "预览店铺效果");
        intent.putExtra("wrapper", mDataList.get(position));
        intent.putExtra("isLoadFirst", false);
        startActivityForResult(intent, REQUEST_SAVE);

        Map<String, String> map = new HashMap<String, String>();
        map.put("shejishizuopin_index", position + "");
        MobAgentTools.OnEventMobOnDiffUser(this, "PIC_TOU_Shejishi_zuopin", map);
    }

    @OnActivityResult(REQUEST_SAVE)
    void onSaveDone(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectPicActivity.this).inflate(R.layout.selectpic_item, null);
            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.image);
            LayoutParams params = iv.getLayoutParams();
            params.height = processHeight();
            iv.setLayoutParams(params);
            Picasso.with(SelectPicActivity.this).load(QFCommonUtils.generateQiniuUrl(mDataList.get(position).getImgURL(), 600, 600 * 33 / 64))
                    .fit().centerCrop().into(iv);
            return convertView;
        }

        int processHeight() {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            @SuppressWarnings("deprecation")
            int height = wm.getDefaultDisplay().getHeight();
            return height / 5;
        }
    }
}
