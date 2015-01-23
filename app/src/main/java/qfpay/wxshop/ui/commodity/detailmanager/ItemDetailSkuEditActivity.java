package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Intent;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;

/**
 *
 *
 * Created by LiFZhe on 1/21/15.
 */
@EActivity(R.layout.itemdetail_skuedit)
public class ItemDetailSkuEditActivity extends BaseActivity {
    @ViewById EditText et_name, et_price, et_count;

    @Extra    SkuViewModel skuViewModel;

    @AfterViews void onInit() {
        et_name.setText(skuViewModel.getName());
        et_price.setText(skuViewModel.getPrice());
        et_count.setText(skuViewModel.getAmount());
    }

    @Click void tv_save() {
        skuViewModel.setName(et_name.getText().toString());
        skuViewModel.setPrice(et_price.getText().toString());
        skuViewModel.setAmount(et_count.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("SkuViewModel", skuViewModel);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click void iv_close() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
