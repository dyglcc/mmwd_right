package qfpay.wxshop.activity;

import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import qfpay.wxshop.R;
import qfpay.wxshop.utils.Toaster;

@EActivity(R.layout.copyurlguide_layout)
public class CopyUrlGuideActivity extends BaseActivity {
	@Click
	void btn_know() {
		finish();
		Toaster.s(this, "链接复制成功");
	}
}
