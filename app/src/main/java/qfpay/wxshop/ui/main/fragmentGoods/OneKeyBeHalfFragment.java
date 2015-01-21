package qfpay.wxshop.ui.main.fragmentGoods;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.fragment.BaseFragment;

/**
 * Created by dongyuangui
 */

@EFragment(R.layout.main_onkeybehalf_list)
public class OneKeyBeHalfFragment extends BaseFragment {


    @ViewById
    View layout_own, layout_onkey_behalf;

    @ViewById
    LinearLayout layout_content;


    @AfterViews
    void init() {


    }

    @Click
    void layout_own() {

    }



}
