package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.volley.RequestQueue;
import com.adhoc.volley.toolbox.Volley;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class VolleyNet {
    private static VolleyNet ourInstance = null;

    public static VolleyNet getInstance(Context context) {

        if(ourInstance == null){
            ourInstance = new VolleyNet(context);
        }
        return ourInstance;
    }

    public RequestQueue getVolley() {
        return volley;
    }

    private RequestQueue volley;

    private Context mContext;

    private VolleyNet(Context context) {

        this.mContext = context;

        volley = Volley.newRequestQueue(mContext);
    }
}
