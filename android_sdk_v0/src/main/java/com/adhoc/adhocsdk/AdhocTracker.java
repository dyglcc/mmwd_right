package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import java.util.HashMap;

public class AdhocTracker {
//    private static Context mContext;
//    private static AdhocTracker mAdhocTracker = null;
//    private static ExperimentFlags mFlags = null;
//    private AdhocClient mAdhocClient = null;
//    private RequestQueue volley;
//    private static final String JSON_ERROR_STR = "Failed to get experiment flags.";


//    private AdhocTracker(Context context) {
//        mContext = context;
//        volley = Volley.newRequestQueue(mContext);
////        mAdhocClient = AdhocClient.getClient(mContext);
//
//    }

//    public void uploadScreenShotFile() {
//
//        HashMap<String, File> files = new HashMap<String, File>();
//        HashMap<String, String> paras = new HashMap<String, String>();
//
//        ScreenShot.getInstance(mContext).shot();
//
//        files.put("screenshotFile", ScreenShot.getInstance(mContext).getScreenShotFile());
////        files.put("screenshotFile", getTmpFile());
//
//        AnalyticsConfigLoader mConfigLoader = new AnalyticsConfigLoader(mContext);
//
//        paras.put("adhoc_app_track_id", mConfigLoader.getString(AdhocConstants.APP_KEY));
//
//        paras.put("client_id", AdhocClientIDHandler.getInstance(mContext).getClientId());
//
//        // send pic request
//        MultipartRequest mr = new MultipartRequest(AdhocConstants.
//                ADHOC_PIC_SERVER, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                T.e(new Exception(error.toString()));
//
//            }
//        }, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                boolean success = true;
//                if (success) {
//                    T.i("flag change set false");
//                    Utils.saveBooleanShareData(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED, false);
////                    SharePrefHandler.getInstance(mContext).setTesterScanFalse();
//                    ProviderHandler.getInstance(mContext).delScan();
//                }
//            }
//        }, files, paras);
//
//        volley.add(r);
//    }

//    // 获取 screen shot pic
//    public Bitmap takePic() {
//        ScreenShot.getInstance(mContext).shot();
//        return ScreenShot.getInstance(mContext).getScreenShotBimap(mContext);
//    }

//    /*
//     * 去本地flags，并访问服务器
//     */
//    public ExperimentFlags getExperimentFlags() {
//
//        // 检查网络如果网络未联通返回本地保存
//
//        mFlags = getLocalFlags();
//
//        String result = "null";
//
//        if (mFlags != null && mFlags.getRawFlags() != null) {
//
//            result = mFlags.getRawFlags().toString();
//        }
//
//        T.i("取本地flags 结果：" + result);
//
//        getNewestFlagsFromServer();
//
//
//        return mFlags;
//
//}

    public static ExperimentFlags getExperimentFlags(Context context) {
        return GetExperimentFlag.getInstance(context).getExperimentFlags(context);
    }

    public static void setCustomStatParameter(Context context, HashMap<String, String> map) {
        BuildParameters.getInstance(context).makeCustomPara(map);
    }

//    // 从服务器取模块开关
//
//    private void getNewestFlagsFromServer() {
//
//
//        if (!Utils.isCanConnectionNetWork(mContext)) {
//
//            return;
//
//        }
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//        JSONObject object = BuildParameters.getInstance(mContext).buildParametersForServer(keyValueCampaign);
//
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                AdhocConstants.ADHOC_SERVER_GETFLAGS, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                if (response != null && response.has("error")) {
//                    String errMesg = null;
//                    try {
//                        errMesg = response.getString("error");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    T.e(new Exception(errMesg));
//                    return;
//                }
//
//                mFlags = new ExperimentFlags(response);
//
//                // 将mFlags 保存sharepreference;
//
//                saveSharePrefFlags(response);
//
//                // check if need update pic
//
//                checkNeedUploadFile();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
////                T.w(error.getMessage());
//                T.e(error);
//            }
//
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//
//                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Language", "utf-8");
//
//                return headers;
//            }
//        };
//
//        request.setShouldCache(false);
//
//        volley.add(request);
//
//    }


//    private void checkNeedUploadFile() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 没有安装tester
//                PackageInfo packageInfo = null;
//
//                try {
//                    packageInfo = mContext.getPackageManager().getPackageInfo(
//                            AdhocConstants.SCANNER_PACKAGE_NAME, 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    T.i("不上传截图");
//                }
//
//                if (packageInfo == null) {
//                    T.i("不上传截图 : 没有 tester ");
//                    return;
//                }
//                // 查看test sao 标记
////                boolean sao = SharePrefHandler.getInstance(mContext).getTesterPreferenceSaoFlag();\
//
//                int count = ProviderHandler.getInstance(mContext).searchScan();
//
//                // tester 扫描加入实验成功
//                if (count == 0) {
//                    T.i("不上传截图:未扫描并加入实验");
//                    return;
//
//                }
//
//                // flag 有改变，上传截屏
//                boolean flag_changed = Utils.getBooleanValue(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED);
//
//                if (!flag_changed) {
//
//                    T.i("不上传截图 ： 开关字符串没有改变: " + flag_changed);
//                    return;
//
//                }
//
//                uploadScreenShotFile();
//
//                T.i("upload files ");
//
//
//            }
//        }).start();
//    }

    public static void getExperimentFlags(Context context, OnAdHocReceivedData listener) {
        GetExperimentFlag.getInstance(context).getExperimentFlags(listener);
    }

//    /*
//     * 取服务器模块开关，访问网络一次
//     */
//
//    public void getExperimentFlags(final OnAdHocReceivedData listener) {
//
//        // 检查网络如果网络未联通返回本地保存
//
//        if (!Utils.isCanConnectionNetWork(mContext)) {
//
//            mFlags = getLocalFlags();
//
//            T.w("取本地flags 结果：" + mFlags);
//            if (listener != null) {
//                listener.onReceivedData(mFlags.getRawFlags());
//            }
//
//        }
//
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//
//        // TODO: let the async task use callback to update mFlags.
//        final AdhocFlagClient mAdhocClient = new AdhocFlagClient(mContext);
//        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_GETFLAGS, keyValueCampaign, listener, 0);
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (mAdhocClient.getLastResponse() == null) {
//                    try {
//                        // 这里会阻塞ui线程，需要优化
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        T.d("Failed to sleep.");
//                    }
//                }
//                if (!mAdhocClient.getLastResponse().equals("UNKNOWN")) {
//                    try {
//                        JSONObject object = new JSONObject(mAdhocClient.getLastResponse());
//
//                        mFlags = new ExperimentFlags(object);
//
//                        // 将mFlags 保存sharepreference;
//                        saveSharePrefFlags(object);
//
//                        if (listener != null) {
//
//                            listener.onReceivedData(mFlags.getRawFlags());
//
//                        }
//
//                    } catch (JSONException e) {
//                        T.d("Failed to get experiment flags.");
//                    }
//                }
//            }
//        }).start();
//
//    }

//    // 从本地去模块开关
//    private ExperimentFlags getLocalFlags() {
//        ExperimentFlags flags = new ExperimentFlags(new JSONObject());
//        // init flag state
//        flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_NULL.toString());
//
//        String str = getlocalFlagsString();
//
//        if (!str.equals("")) {
//            try {
//                JSONObject jsObj = new JSONObject(str);
//
//                flags = new ExperimentFlags(jsObj);
//
//                flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());
//                return flags;
//            } catch (JSONException e) {
//
//                T.i(JSON_ERROR_STR);
//
//            }
//        }
//        return flags;
//    }

    public static ExperimentFlags getExperimentFlagsTimeOut(Context context, int timeoutMillis) {
        return GetExperimentFlag.getInstance(context).getExperimentFlagsTimeOut(timeoutMillis);
    }

    public static ExperimentFlags getExperimentFlagsTimeOut(Context context, int timeoutMillis, OnAdHocReceivedData listener) {
        return GetExperimentFlag.getInstance(context).getExperimentFlagsTimeOut(timeoutMillis, listener);
    }

//    public ExperimentFlags getExperimentFlagsTimeOut(int timeoutMillis) {
//
//        return this.getExperimentFlagsTimeOut(timeoutMillis, null);
//    }

    /*
     * Get experiment flags.
     */
//    public ExperimentFlags getExperimentFlagsTimeOut(int timeoutMillis, OnAdHocReceivedData listener) {
//
//        if (mAdhocClient == null) return null;
//
//        // 检查网络如果网络未联通返回本地保存
//
//        if (!Utils.isCanConnectionNetWork(mContext)) {
//
//            mFlags = getLocalFlags();
//
//            T.w("取本地flags 结果：" + mFlags);
//
//            return mFlags;
//        }
//
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.GET_EXPERIMENT_FLAGS));
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//
//        // TODO: let the async task use callback to update mFlags.
//        AdhocFlagClient mAdhocClient = new AdhocFlagClient(mContext);
//        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_GETFLAGS, keyValueCampaign, listener, timeoutMillis);
//
//        do {
//            try {
//
//                Thread.sleep(1000);
//
//                timeoutMillis = timeoutMillis - 1000;
//
//            } catch (InterruptedException e) {
//                T.d("Failed to sleep.");
//            }
//        } while (timeoutMillis > 0 && mAdhocClient.getLastResponse() == null);
//
//        if (!mAdhocClient.getLastResponse().equals("UNKNOWN")) {
//            try {
//
//                JSONObject object = new JSONObject(mAdhocClient.getLastResponse());
//
//                mFlags = new ExperimentFlags(object);
//
//                // 保存sharepreference;
//                saveSharePrefFlags(object);
//
//            } catch (JSONException e) {
//                T.i(JSON_ERROR_STR);
//            }
//            mFlags = null;
//        } else {// 从本地取flags
//
//            String str = getlocalFlagsString();
//
//            if (!str.equals("")) {
//                try {
//
//                    JSONObject jsObj = new JSONObject(str);
//
//                    mFlags = new ExperimentFlags(jsObj);
//
//                } catch (JSONException e) {
//
//                    T.i(JSON_ERROR_STR);
//
//                    mFlags = null;
//                }
//            }
//        }
//        return mFlags;
//    }

//    private String getlocalFlagsString() {
//        return Utils.getStringShareData(Utils.getSharePreference(mContext),
//                AdhocConstants.PREFS_ABTEST_FLAGS);
//    }

//    private void saveSharePrefFlags(JSONObject obj) {
//        // check changed
//        String str = Utils.getStringShareData(Utils.getSharePreference(mContext), AdhocConstants.PREFS_ABTEST_FLAGS);
//
//        // changed
//        if (!str.equals(obj.toString())) {
//            T.i("flag changed save change flag true");
//            Utils.saveBooleanShareData(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED, true);
//        }
//
//        Utils.saveStringShareData(Utils.getSharePreference(mContext),
//
//                AdhocConstants.PREFS_ABTEST_FLAGS, obj.toString());
//
//
//    }

    /*
     * Tracks user on starting an activity.
     */
//    public void Start(Activity activity) {
//        if (!BuildParameters.getInstance(mContext).needTrackActivity()) return;
//        if (mAdhocClient == null) return;
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.ACTIVITY_START));
//        keyValueCampaign.put(KeyFields.ACTIVITY_NAME, activity.getClass().getSimpleName());
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_URL, keyValueCampaign, null, 0);
//    }

    /*
     * Tracks user on stopping an activity.
     */
//    public void Stop(Activity activity) {
//        if (!BuildParameters.getInstance(mContext).needTrackActivity()) return;
//        if (mAdhocClient == null) {
//            mAdhocClient = AdhocClient.getClient(mContext);
//        }
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.ACTIVITY_STOP));
//        keyValueCampaign.put(KeyFields.ACTIVITY_NAME, activity.getClass().getSimpleName());
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_URL, keyValueCampaign, null, 0);
//    }

    /*
     * Increment experiment stats.
     */
//    private void incrementStatObj(String key, Object inc) {
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.REPORT_STAT));
//        keyValueCampaign.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
//        keyValueCampaign.put(KeyFields.STAT_KEY, key);
//        keyValueCampaign.put(KeyFields.STAT_VALUE, inc);
//
//        JSONObject object = BuildParameters.getInstance(mContext).buildParametersForServer(keyValueCampaign);
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                AdhocConstants.ADHOC_SERVER_URL, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                // 没有返回
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                T.w(error.getMessage());
//                T.e(error);
//            }
//
//        });
//        request.setShouldCache(false);
//        volley.add(request);
//    }

    /*
     * Increment experiment stats.
     */
    public static void incrementStat(Context context, String key, double inc) {

        IncrementStat.getInstance().incrementStatObj(context, key, inc);

//        this.incrementStatObj(key, inc);

    }


    /*
     * Increment experiment stats.
     */
    public static void incrementStat(Context context, String key, int inc) {

        IncrementStat.getInstance().incrementStatObj(context, key, inc);

//        this.incrementStatObj(key, inc);
    }

    /*
     * Increment experiment stats.
     */
    public static void incrementStat(Context context, String key, long inc) {

        IncrementStat.getInstance().incrementStatObj(context, key, inc);
    }

    /*
     * Increment experiment stats.
     */
    public static void incrementStat(Context context, String key, float inc) {

        IncrementStat.getInstance().incrementStatObj(context, key, inc);
    }


//    /*
//     * Handle onReceive from BroadcastReceiver of com.android.vending.INSTALL_REFERRER.
//     */
//    public void onReceive(String campaign) {
//
//        T.d("onReceive campaign: " + campaign);
//
//        if (mAdhocClient == null) return;
//
//
//        HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//        keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.INSTALL_REFERRER));
//        keyValueCampaign.put(KeyFields.INSTALL_REFERRER, campaign);
//        mAdhocClient.sendToServer(AdhocConstants.ADHOC_SERVER_URL, keyValueCampaign, null, 0);
//    }

    // 得到点击的Views
//    private List<View> getClickViews(Activity activity, float x1, float y1) {
//        List<View> clickViews = new ArrayList<View>();
//        int x = (int) x1;
//        int y = (int) y1;
//        List<View> children = getAllChildViews(activity.getWindow().getDecorView());
//        for (View viewchild : children) {
//
//            String viewName = viewchild.getClass().getCanonicalName();
//
//            // 去掉v4,7等系统控件
//            if (viewName.indexOf("internal.widget") != -1) {
//                continue;
//            }
//            // 去掉
//            if (viewName.indexOf("android.support.") != -1) {
//                continue;
//            }
//            boolean isTextView = viewchild instanceof TextView;
//
//            // 没有id 并且还是非TextView
//            if (viewchild.getId() == -1 && !isTextView) {
//                continue;
//            }
//            int[] localChild = new int[2];
//            viewchild.getLocationOnScreen(localChild);
//            if (x >= localChild[0] && x <= localChild[0] + viewchild.getWidth() && y <= localChild[1] + viewchild.getHeight() && y >= localChild[1]) {
//                clickViews.add(viewchild);
//            }
//        }
//        return clickViews;
//
//    }
//
//
//    private List<View> getAllChildViews(View view) {
//        List<View> allchildren = new ArrayList<View>();
//        if (view instanceof ViewGroup) {
//            ViewGroup vp = (ViewGroup) view;
//            for (int i = 0; i < vp.getChildCount(); i++) {
//                View viewchild = vp.getChildAt(i);
//                allchildren.add(viewchild);
//                allchildren.addAll(getAllChildViews(viewchild));
//            }
//        }
//        return allchildren;
//    }
//
//    private String showAllViewInfo(List<View> views) {
//
//        StringBuilder sb = new StringBuilder();
//
//        for (View v : views) {
//
//            String viewText = "";
//            if (v instanceof TextView) {
//
//                viewText = ((TextView) v).getText().toString();
//            }
//            sb.append(v.getClass().getCanonicalName() + ":" + viewText + "\n");
//
//        }
//        return sb.toString();
//
//
//    }
//
//    private String getShowViewsStrings(List<View> views) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < views.size(); i++) {
//
//            View view = views.get(i);
//
//            sb.append(view.getClass().getCanonicalName() + " @id :" + view.getId() + "\n");
//        }
//        return sb.toString();
//    }

    public static void autoTracking(Context context, MotionEvent event) {

        AutoStatClickEvent.getInstance().autoTracking(context, event);

//        if (event.getAction() == MotionEvent.ACTION_UP) {
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    boolean isActivity = mContext instanceof Activity;
//
//                    if (!isActivity) {
//
//                        return;
//
//                    }
//
//                    List<View> clickeViews = getClickViews(((Activity) mContext), event.getRawX(), event.getRawY());
//
//                    for (View view : clickeViews) {
//                        if (view instanceof TextView) {
//                            String text = ((TextView) view).getText().toString();
//                            if (text.length() > 13) {
//                                text = text.substring(0, 13) + "...";
//                            }
//                            String key = view.getId() + "_" + text + "_autoclick";
//                            incrementStat(key, 1);
//                            T.i("自动统计:key_" + (key));
//                        }
//                    }
//
//                    T.i("点击view 列表:" + getShowViewsStrings(clickeViews));
//                }
//            }).start();
//
//        }
    }

    public static void initalize(Context context) {
        MLog.getInstance().createLogCollector(context);
        MLog.getInstance().enableDebug();
        CrashHandler.getInstance().run(context);
    }

    public static void onViewPagerFragmentStat(Object fragment, boolean isVisibleToUser) {

        ViewPagerFragmentStat.getInstance().setUserVisibleHint(fragment, isVisibleToUser);

    }

    public static void onFragmentCreate(Context context, Object obj) {
        try {
            StatFragment.getInstance().add(context, obj);
            MLog.getInstance().createLogCollector(context);
            MLog.getInstance().enableDebug();

        } catch (IllegalAccessException e) {
            T.e(e);
        } catch (Exception e) {
            T.e(e);
        }
    }

    public static void onFragmentDestory(Context context, Object fragment) {
        StatFragment.getInstance().onFragmentDestory(fragment);
    }

    public static void onResume(Activity context) {

        T.i("onResume : " + context.getClass().getName());
        PageActivityStat.getInstance().OnResume(context);
        if(StatFragment.getInstance().resumeForeground){
            Log.d("FragmentManager"," Operations:");
            T.i("resume Opertions");
        }

    }

    public static void onPause(final Activity context) {

        T.i("onPause : " + context.getClass().getName());
        PageActivityStat.getInstance().OnPause(context);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    T.e(e);
                }
                boolean isForeground = Utils.isAppOnForeground(context);
                if (!isForeground) {

                    StatFragment.getInstance().onBack2Menu(context);
                    T.i("back 2 menu");
                    StatFragment.getInstance().resumeForeground = true;
                }else{
                    StatFragment.getInstance().resumeForeground = false;
                }

                if (PageActivityStat.run) {

                    // 回到主屏
                    if (!isForeground) {
                        //
                        PageActivityStat.getInstance().onDestory(context);
                    }
                }

            }
        }).start();


    }

    public static void reportCrashEnable(boolean enable) {

        CrashHandler.getInstance().setEnable(enable);

    }
}
