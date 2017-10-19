package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.utils.T;
import com.adhoc.volley.Response;
import com.adhoc.volley.VolleyError;
import com.adhoc.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class IncrementStat {
    private static IncrementStat ourInstance = new IncrementStat();

    public static IncrementStat getInstance() {
        return ourInstance;
    }

    private IncrementStat() {
    }

    /*
         * Increment experiment stats.
         */
    public void incrementStatObj(Context context, final String key, Object inc) {
        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.REPORT_STAT));
        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
        keyValueCampaign.put(KeyFields.STAT_KEY, key);
        keyValueCampaign.put(KeyFields.STAT_VALUE, inc);
//        T.i("report key : " +key+" value : " + inc );

        JSONObject object = BuildParameters.getInstance(context).buildParametersForServer(keyValueCampaign);

        JsonObjectRequest request = new JsonObjectRequest(
                AdhocConstants.ADHOC_SERVER_URL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                T.i("response -- key " + key);
                // 没有返回
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                T.w(error.getMessage());
                T.e(error);
            }

        });
        request.setShouldCache(false);
        VolleyNet.getInstance(context).getVolley().add(request);
    }
}
