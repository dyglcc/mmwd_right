package qfpay.wxshop.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;

@EActivity(R.layout.main_whatis_onekeybehalf)
public class OnekeybehalfWhatisActivty extends BaseActivity {

	@ViewById
	Button btn_back,btn_save;
	@ViewById
	TextView tv_title;



	@AfterViews
	void init() {
		ActionBar bar = getSupportActionBar();
		bar.hide();
		setCustomView();
	}
	public void setCustomView(){
		btn_back = (Button)findViewById(R.id.btn_back);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("一键代发教程");
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {


            }
		});
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

                finish();
            }
		});
    }



}
