package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.utils.ReflectionUtil;
import com.adhoc.utils.T;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MLog {

    private HashMap<String, LogFragment> logsOld = new HashMap<String, LogFragment>();
    //    private HashMap<String, LogFragment> logsNew = new HashMap<String, LogFragment>();
    private static MLog instance = null;
    private Process process;
    private Context mContext;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ArrayList<String> lines = new ArrayList<String>();

//    private long time1 = System.currentTimeMillis();

    public void createLogCollector(Context context) {
        this.mContext = context;
        if (isRun()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                long lastTime = 0L;
                List<String> commandList = new ArrayList<String>();
                commandList.add("logcat");
                commandList.add("-b");
                commandList.add("main");
                commandList.add("-v");
                commandList.add("time");
                commandList.add("FragmentManager:V *:S");
                try {
                    process = Runtime.getRuntime().exec(
                            commandList.toArray(new String[commandList.size()]));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String str;
                    // process.waitFor();
                    while ((str = reader.readLine()) != null) {

                        // read new start
                        // read new end
                        if (!run) {
                            break;
                        }

                        if(str.indexOf(" Operations:")!=-1){

                            if((System.currentTimeMillis() - lastTime) > 800){
                                lastTime = System.currentTimeMillis();
                                StatFragment.getInstance().dump();
                            }else{
                                T.i("800 毫秒内有重复的Operations");
                            }

                        }

                    }
                } catch (Exception e) {
                    T.e(e);
                }
            }
        }).start();

    }

    private long getTimefromLog(String str) {
        long t = 0;
        String stringTime = null;
        if (str.length() >= 14) {
            stringTime = Calendar.getInstance().get(Calendar.YEAR) + "-" + str.substring(0, 14);
        }
        if (stringTime != null && stringTime.length() == 19) {
            try {
                Date date = simpleDateFormat.parse(stringTime);
                t = date.getTime();
            } catch (ParseException e) {
                T.e(e);
            }
        }
        return t;
    }



    public static MLog getInstance() {
        if (instance == null) {
            instance = new MLog();
        }
        return instance;
    }

    private MLog() {
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    private boolean run = false;


    public void destory() {

        logsOld.clear();

    }

    public void enableDebug() {

        if (isRun()) {
            return;
        }

//        android.support.v4.app.FragmentManager.enableDebugLogging(true);
//        FragmentManager.enableDebugLogging(true);
        // 为什么不用fragmentManager直接enableDebug()
//        Field field = ReflectionUtil.getField(activity.getClass(), "mFragments");
        // 另外一种方式 class.forname
        try {
            Class fragmentManager = Class.forName("android.support.v4.app.FragmentManagerImpl");

            if (fragmentManager != null) {

                setBooleanTrueValue(fragmentManager, true);

            }
        } catch (ClassNotFoundException e) {
            T.e(e);
        }

        try {
            Class fragmentManagerSdk = Class.forName("android.app.FragmentManagerImpl");
            setBooleanTrueValue(fragmentManagerSdk, true);

        } catch (ClassNotFoundException e) {
            T.e(e);
        }
        run = true;

    }

    private void setBooleanTrueValue(Class clazz, boolean value) {
        Field fieldDebug = null;
        try {
            fieldDebug = ReflectionUtil.getField(clazz, "DEBUG");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ReflectionUtil.setStaticValue(fieldDebug, new Boolean(value));
    }

    public void disAbleDebug() throws NoSuchFieldException, IllegalAccessException {

//        android.support.v4.app.FragmentManager.enableDebugLogging(true);
//        FragmentManager.enableDebugLogging(true);
        // 为什么不用fragmentManager直接enableDebug()
//        Field field = ReflectionUtil.getFieldByClassName(activity.getClass(), "mFragments", "FragmentActivity");
//        Object value = ReflectionUtil.getFieldValue(field, activity);
//        if (value != null) {
//
//            Field fieldDebug = ReflectionUtil.getField(value.getClass(), "DEBUG");
//            ReflectionUtil.setFieldValue(fieldDebug, value, new Boolean(false));
//        }
//
//        Field fieldSuper = ReflectionUtil.getField(Activity.class, "mFragments");
//        Object valueSuper = ReflectionUtil.getFieldValue(fieldSuper, activity);
//        if (valueSuper != null) {
//
//            Field fieldDebugsuper = ReflectionUtil.getField(valueSuper.getClass(), "DEBUG");
//            ReflectionUtil.setFieldValue(fieldDebugsuper, valueSuper, new Boolean(false));
//        }
        try {
            Class fragmentManager = Class.forName("android.support.v4.app.FragmentManagerImpl");

            if (fragmentManager != null) {

                setBooleanTrueValue(fragmentManager, false);

            }
        } catch (ClassNotFoundException e) {
            T.e(e);
        }

        try {
            Class fragmentManagerSdk = Class.forName("android.app.FragmentManagerImpl");
            setBooleanTrueValue(fragmentManagerSdk, false);

        } catch (ClassNotFoundException e) {
            T.e(e);
        }

    }

}