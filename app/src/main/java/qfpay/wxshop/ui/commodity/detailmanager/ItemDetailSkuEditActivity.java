package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
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
    @ViewById EditText     et_name, et_price, et_count;
    @ViewById Button       btn_delete;
    @ViewById TextView     tv_title;

    @Extra    SkuViewModel skuViewModel;
    @Extra    int          position;

    @AfterViews void onInit() {
        if (skuViewModel != null) {
            et_name.setText(skuViewModel.getName());
            et_price.setText(skuViewModel.getPrice());
            et_count.setText(skuViewModel.getAmount());
            btn_delete.setVisibility(View.VISIBLE);
            tv_title.setText("编辑规格");
        } else {
            skuViewModel = new SkuViewModel();
            tv_title.setText("添加规格");
        }
        et_name.requestFocus();
        et_name.setFocusable(true);
    }

    @EditorAction void et_count() {
        tv_save();
    }

    @Click void tv_save() {
        skuViewModel.setName(et_name.getText().toString());
        skuViewModel.setPrice(et_price.getText().toString());
        skuViewModel.setAmount(et_count.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("SkuViewModel", skuViewModel);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Click void iv_close() {
        onBackPressed();
    }

    @Click void btn_delete() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
    }
}
