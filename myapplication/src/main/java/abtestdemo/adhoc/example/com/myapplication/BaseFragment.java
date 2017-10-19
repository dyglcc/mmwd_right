package abtestdemo.adhoc.example.com.myapplication;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

/**
 * Created by dongyuangui on 15-5-14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        T.i("wwd", "Fsdfsume:" + this.getClass().getName());
        super.onResume();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

}
