package com.adhoc.adhocsdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.adhoc.utils.T;
import com.adhoc.utils.Utils;
import com.adhoc.volley.Response;
import com.adhoc.volley.VolleyError;
import com.adhoc.volley.toolbox.JsonObjectRequest;
import com.adhoc.volley.toolbox.MultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class GetExperimentFlag {

    private static final String JSON_ERROR_STR = "Failed to get experiment flags.";
    private static GetExperimentFlag ourInstance;


    private Context  mContext;
    private static ExperimentFlags mFlags = null;

    public static GetExperimentFlag getInstance(Context context) {
        if(ourInstance == null){
            return new GetExperimentFlag(context);
        }
        return ourInstance;
    }

    private GetExperimentFlag(Context context) {

        mContext = context;

    }

    // 从本地去模块开关
    private ExperimentFlags getLocalFlags() {
        ExperimentFlags flags = new ExperimentFlags(new JSONObject());
        // init flag state
        flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_NULL.toString());

        String str = getlocalFlagsString();

        if (!str.equals("")) {
            try {
                JSONObject jsObj = new JSONObject(str);

                flags = new ExperimentFlags(jsObj);

                flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());
                return flags;
            } catch (JSONException e) {

                T.i(JSON_ERROR_STR);

            }
        }
        return flags;
    }

    /*
 * 去本地flags，并访问服务器
 */
    public ExperimentFlags getExperimentFlags(Context context) {
        mContext = context;

        // 检查网络如果网络未联通返回本地保存

        mFlags = getLocalFlags();

        String result = "null";

        if (mFlags != null && mFlags.getRawFlags() != null) {

            result = mFlags.getRawFlags().toString();
        }

        T.i("取本地flags 结果：" + result);

        getNewestFlagsFromServer(context);


        return mFlags;

    }

    private String getlocalFlagsString() {
        return Utils.getStringShareData(Utils.getSharePreference(mContext),
                AdhocConstants.PREFS_ABTEST_FLAGS);
    }

    private void saveSharePrefFlags(JSONObject obj) {
        // check changed
        String str = Utils.getStringShareData(Utils.getSharePreference(mContext), AdhocConstants.PREFS_ABTEST_FLAGS);

        // changed
        if (!str.equals(obj.toString())) {
            T.i("flag changed save change flag true");
            Utils.saveBooleanShareData(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED, true);
        }

        Utils.saveStringShareData(Utils.getSharePreference(mContext),

                AdhocConstants.PREFS_ABTEST_FLAGS, obj.toString());


    }

    private void getNewestFlagsFromServer(final Context context) {


        if (!Utils.isCanConnectionNetWork(mContext)) {

            return;

        }
        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
        JSONObject object = BuildParameters.getInstance(mContext).buildParametersForServer(keyValueCampaign);


        JsonObjectRequest request = new JsonObjectRequest(
                AdhocConstants.ADHOC_SERVER_GETFLAGS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null && response.has("error")) {
                    String errMesg = null;
                    try {
                        errMesg = response.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    T.e(new Exception(errMesg));
                    return;
                }

                mFlags = new ExperimentFlags(response);

                // 将mFlags 保存sharepreference;

                saveSharePrefFlags(response);

                // check if need update pic

                checkNeedUploadFile(context);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                T.w(error.getMessage());
                T.e(error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Language", "utf-8");

                return headers;
            }
        };

        request.setShouldCache(false);

        VolleyNet.getInstance(mContext).getVolley().add(request);

    }



    private void checkNeedUploadFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 没有安装tester
                PackageInfo packageInfo = null;

                try {
                    packageInfo = mContext.getPackageManager().getPackageInfo(
                            AdhocConstants.SCANNER_PACKAGE_NAME, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    T.i("不上传截图");
                }

                if (packageInfo == null) {
                    T.i("不上传截图 : 没有 tester ");
                    return;
                }
                // 查看test sao 标记
//                boolean sao = SharePrefHandler.getInstance(mContext).getTesterPreferenceSaoFlag();\

                int count = ProviderHandler.getInstance(mContext).searchScan();

                // tester 扫描加入实验成功
                if (count == 0) {
                    T.i("不上传截图:未扫描并加入实验");
                    return;

                }

                // flag 有改变，上传截屏
                boolean flag_changed = Utils.getBooleanValue(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED);

                if (!flag_changed) {

                    T.i("不上传截图 ： 开关字符串没有改变: " + flag_changed);
                    return;

                }

                uploadScreenShotFile(context);

                T.i("upload files ");


            }
        }).start();
    }

    private void uploadScreenShotFile(final Context context) {

        HashMap<String, File> files = new HashMap<String, File>();
        HashMap<String, String> paras = new HashMap<String, String>();

        ScreenShot.getInstance().shot(context);

        files.put("screenshotFile", ScreenShot.getInstance().getScreenShotFile(context));
//        files.put("screenshotFile", getTmpFile());

        AnalyticsConfigLoader mConfigLoader = new AnalyticsConfigLoader(context);

        paras.put("adhoc_app_track_id", mConfigLoader.getString(AdhocConstants.APP_KEY));

        paras.put("client_id", AdhocClientIDHandler.getInstance(context).getClientId());

        // send pic request
        MultipartRequest mr = new MultipartRequest(AdhocConstants.
                ADHOC_PIC_SERVER, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                T.e(new Exception(error.toString()));

            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Uploaded")){
                    T.i(response);
                    Utils.saveBooleanShareData(Utils.getSharePreference(context), AdhocConstants.FLAG_CHANGED, false);
//                    SharePrefHandler.getInstance(mContext).setTesterScanFalse();
                    ProviderHandler.getInstance(context).delScan();
                }
            }
        }, files, paras);

        VolleyNet.getInstance(context).getVolley().add(mr);
    }

    public ExperimentFlags getExperimentFlagsTimeOut(int timeoutMillis) {

        return this.getExperimentFlagsTimeOut(timeoutMillis, null);
    }

    /*
     * Get experiment flags.
     */
    public ExperimentFlags getExperimentFlagsTimeOut(int timeoutMillis, OnAdHocReceivedData listener) {

        // 检查网络如果网络未联通返回本地保存

        if (!Utils.isCanConnectionNetWork(mContext)) {

            mFlags = getLocalFlags();

            T.w("取本地flags 结果：" + mFlags);

            return mFlags;
        }

        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());

        // TODO: let the async task use callback to update mFlags.
        AdhocFlagClient mAdhocClient = new AdhocFlagClient(mContext);
        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_GETFLAGS, keyValueCampaign, listener, timeoutMillis);

        do {
            try {

                Thread.sleep(1000);

                timeoutMillis = timeoutMillis - 1000;

            } catch (InterruptedException e) {
                T.d("Failed to sleep.");
            }
        } while (timeoutMillis > 0 && mAdhocClient.getLastResponse() == null);

        if (!mAdhocClient.getLastResponse().equals("UNKNOWN")) {
            try {

                JSONObject object = new JSONObject(mAdhocClient.getLastResponse());

                mFlags = new ExperimentFlags(object);

                // 保存sharepreference;
                saveSharePrefFlags(object);

            } catch (JSONException e) {
                T.i(JSON_ERROR_STR);
            }
            mFlags = null;
        } else {// 从本地取flags

            String str = getlocalFlagsString();

            if (!str.equals("")) {
                try {

                    JSONObject jsObj = new JSONObject(str);

                    mFlags = new ExperimentFlags(jsObj);

                } catch (JSONException e) {

                    T.i(JSON_ERROR_STR);

                    mFlags = null;
                }
            }
        }
        return mFlags;
    }

     /*
     * 取服务器模块开关，访问网络一次
     */

    public void getExperimentFlags(final OnAdHocReceivedData listener) {

        // 检查网络如果网络未联通返回本地保存

        if (!Utils.isCanConnectionNetWork(mContext)) {

            mFlags = getLocalFlags();

            T.w("取本地flags 结果：" + mFlags);
            if (listener != null) {
                listener.onReceivedData(mFlags.getRawFlags());
            }

        }

        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());

        // TODO: let the async task use callback to update mFlags.
        final AdhocFlagClient mAdhocClient = new AdhocFlagClient(mContext);
        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_GETFLAGS, keyValueCampaign, listener, 0);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (mAdhocClient.getLastResponse() == null) {
                    try {
                        // 这里会阻塞ui线程，需要优化
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        T.d("Failed to sleep.");
                    }
                }
                if (!mAdhocClient.getLastResponse().equals("UNKNOWN")) {
                    try {
                        JSONObject object = new JSONObject(mAdhocClient.getLastResponse());

                        mFlags = new ExperimentFlags(object);

                        // 将mFlags 保存sharepreference;
                        saveSharePrefFlags(object);

                        if (listener != null) {

                            listener.onReceivedData(mFlags.getRawFlags());

                        }

                    } catch (JSONException e) {
                        T.d("Failed to get experiment flags.");
                    }
                }
            }
        }).start();

    }
}
