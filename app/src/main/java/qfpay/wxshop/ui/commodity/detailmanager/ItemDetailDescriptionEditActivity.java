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
@EActivity(R.layout.itemdetail_descriptionedit)
public class ItemDetailDescriptionEditActivity extends BaseActivity {
    @ViewById EditText et_description;

    @Extra    String   description;

    @AfterViews void onInit() {
        et_description.setText(description);
    }

    @Click(R.id.tv_save) void onEditDone() {
        Intent intent = new Intent();
        intent.putExtra("description", et_description.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click void iv_close() {
        onBackPressed();
    }
}
