package com.adhoc.adhocsdk;

import android.content.Context;

/**
 * Created by dongyuangui on 15-5-26.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {


    private Thread.UncaughtExceptionHandler defaultHandler;
    // Thread.getDefaultUncaughtExceptionHandler(); 系统默认的处理所有子线程异常，可以通过覆盖捕获异常
    private static CrashHandler instance;
    private Context context;

    public void setEnable(boolean ENABLE) {
        this.ENABLE = ENABLE;
    }

    private boolean ENABLE = true;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void run(Context context) {

        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        if(ENABLE){
            AdhocTracker.incrementStat(context.getApplicationContext(), "event-crash", 1);
        }
        if(defaultHandler!=null){
            defaultHandler.uncaughtException(thread,throwable);
        }
//        handleException(throwable);
    }
//    private void handleException(final Throwable ex) {
//        if (ex == null) {
//            return;
//        }

    //        final StackTraceElement[] stack = ex.getStackTrace();
//        final String message = ex.getMessage();
    //使用Toast来显示异常信息
//        new Thread() {
//            @Override
//            public void s() {
//                Looper.prepare();
////                Toast.makeText(mContext, "程序出错啦:" + message, Toast.LENGTH_LONG).show();
////                可以只创建一个文件，以后全部往里面append然后发送，这样就会有重复的信息，个人不推荐
//                String fileName = "crash-" + System.currentTimeMillis()  + ".log";
//                File file = new File(Environment.getExternalStorageDirectory(), fileName);
//                try {
//                    FileOutputStream fos = new FileOutputStream(file,true);
//                    fos.write(message.getBytes());
//                    for (int i = 0; i < stack.length; i++) {
//                        fos.write(stack[i].toString().getBytes());
//                    }
//                    fos.flush();
//                    fos.close();
//                } catch (Exception e) {
//                }
//                Looper.loop();
//            }
//
//        }.start();
//        return false;
//    }
    public void onDestory() {
        context = null;
        ENABLE = false;
        instance = null;
    }
}
