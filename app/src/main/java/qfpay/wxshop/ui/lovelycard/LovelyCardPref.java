package qfpay.wxshop.ui.lovelycard;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by LiFZhe on 12/1/14.
 */
@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface LovelyCardPref {
    @DefaultString("")
    public String name();

    @DefaultString("")
    public String label_first();

    @DefaultString("")
    public String label_second();

    @DefaultString("")
    public String descript();

    @DefaultString("")
    public String imgUrl();
}
