package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.utils.T;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class PageActivityStat implements PageStat {

    private static PageActivityStat ourInstance = null;

    public static boolean run = false;

    private ArrayList<ActivityStatBean> list = new ArrayList<ActivityStatBean>();
    private ActivityStatBean from = null;
    private ActivityStatBean to = null;

    public static PageActivityStat getInstance() {
        run = true;
        if (ourInstance == null) {
            ourInstance = new PageActivityStat();
        }
        return ourInstance;
    }

    private PageActivityStat() {
    }

    @Override
    public void sendRequest(Context context) {

        if (context == null) {
            throw new InvalidParameterException("error! need Activity context");
        }
        if (list.size() > 1) {
            from = list.get(list.size() - 2);
        }
        to = list.get(list.size() - 1);
        if (from != null) {

            if (from.id != to.id) {

                long duration = to.t1 - from.t1;
                // 小于300毫秒不记录统计
                if (duration < 300) {
                    return;
                }
                T.i("from " + from.name + "id:" + from.id);
                AdhocTracker.incrementStat(context, "staytime-" + from.name,
                        to.t1 - from.t1);
                // request staytime
                AdhocTracker.incrementStat(context, "event-" + from.name +
                        "-" + to.name, 1);
            }

        } else {
            T.i("from  null");
            AdhocTracker.incrementStat(context, "event-null-" + to.name, 1);

        }
        T.i("to " + to.name + "id:" + to.id);

    }

    public void OnResume(Context context) {

        ActivityStatBean resume = new ActivityStatBean();
        resume.name = context.getClass().getSimpleName();
        resume.t1 = System.currentTimeMillis();
        resume.id = context.hashCode();

        if (list == null) {
            list = new ArrayList<ActivityStatBean>();
        }
        list.add(resume);

        sendRequest(context);

    }

    public void OnPause(Context context) {


    }

    final class ActivityStatBean {

        private String name;
        private long t1;
        private int id;

    }

    public void onDestory(Context context) {
        // 退回桌面发送最后界面停留时间
        sendRequest2Menu(context);
        list.clear();
        list = null;
//        ourInstance = null;
        from = null;
        to = null;
        run = false;
    }

    private void sendRequest2Menu(Context context) {
        if (list != null) {
            from = list.get(list.size() - 1);
        }
        if(from != null){
            AdhocTracker.incrementStat(context, "staytime-" + from.name,
                    System.currentTimeMillis() - from.t1);
        }
    }

}
