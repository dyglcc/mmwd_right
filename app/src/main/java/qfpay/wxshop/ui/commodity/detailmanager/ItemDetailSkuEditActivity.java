package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.data.model.SKUModel;
import qfpay.wxshop.utils.Toaster;

/**
 *
 *
 * Created by LiFZhe on 1/21/15.
 */
@EActivity(R.layout.itemdetail_skuedit)
public class ItemDetailSkuEditActivity extends BaseActivity {
    @ViewById EditText       et_name, et_price, et_count;
    @ViewById Button         btn_delete;
    @ViewById TextView       tv_title;

    @Extra    SkuViewModel   skuViewModel;
    @Extra    int            position;
    @Extra    List<SKUModel> skuModelList; // 当前SkuList的数量, 添加的时候在这个步骤的时候其实List里面并没有这个Sku, 编辑的时候则已经包含了当前这个Sku

    @AfterViews void onInit() {
        if (skuViewModel != null) {
            et_name.setText(skuViewModel.getName());
            et_price.setText(skuViewModel.getPrice());
            et_count.setText(skuViewModel.getAmount());
            btn_delete.setVisibility(View.VISIBLE);
            tv_title.setText("编辑规格");
        } else {
            tv_title.setText("添加规格");
        }
        et_name.requestFocus();
        et_name.setFocusable(true);
    }

    @EditorAction void et_count() {
        tv_save();
    }

    @Click void tv_save() {
        boolean isNameNull = et_name.getText().toString().equals("") || et_name.getText() == null;
        if (skuViewModel == null && isNameNull && !skuModelList.isEmpty()) {
            Toaster.s(this, "有多个规格的时候规格名称必须填写");
            return;
        }

        if (skuViewModel != null && isNameNull && position != skuModelList.size() - 1) {
            Toaster.s(this, "有多个规格的时候规格名称必须填写");
            return;
        }

        if (skuViewModel == null && ! checkSkuName(et_name.getText().toString(), skuModelList)) {
            Toaster.s(this, "规格名称不可重复");
            return;
        }

        if (skuViewModel != null && !checkSkuName(et_name.getText().toString(), skuModelList, position)) {
            Toaster.s(this, "规格名称不可重复");
            return;
        }

        if (et_price.getText() == null || et_price.getText().toString().equals("")) {
            Toaster.s(this, "价格不可为空");
            return;
        }

        if (Float.parseFloat(et_price.getText().toString()) <= 0f) {
            Toaster.s(this, "价格必须大于0");
            return;
        }

        if (et_count.getText() == null || et_count.getText().toString().equals("")) {
            Toaster.s(this, "数量不可为空");
            return;
        }

        if (skuViewModel == null && Integer.parseInt(et_count.getText().toString(), 10) == 0) {
            Toaster.s(this, "新发布的商品库存必须大于零");
            return;
        }

        if (skuViewModel == null) skuViewModel = new SkuViewModel();

        skuViewModel.setName(et_name.getText().toString());
        skuViewModel.setPrice(et_price.getText().toString());
        skuViewModel.setAmount(et_count.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("SkuViewModel", skuViewModel);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    public boolean checkSkuName(String name, List<SKUModel> skuModels) {
        for (SKUModel model : skuModels) {
            if (model.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkSkuName(String name, List<SKUModel> skuModels, int withoutPosition) {
        for (SKUModel model : skuModels) {
            if (model.getName().equals(name) && withoutPosition != skuModels.indexOf(model)) {
                return false;
            }
        }
        return true;
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
