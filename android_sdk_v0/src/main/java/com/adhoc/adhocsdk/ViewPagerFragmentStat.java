package com.adhoc.adhocsdk;

import android.content.Context;

import com.adhoc.utils.ReflectionUtil;
import com.adhoc.utils.T;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by dongyuangui on 15-5-20.
 */
public class ViewPagerFragmentStat  implements PageStat{
    public  static boolean run ;
    private static ViewPagerFragmentStat instance;
//    private static final boolean VISIABLE = true;
//    private static final boolean INVISIABLE = false;
    private PagerFragmentStatBean from = null;
    private HashMap<Integer, PagerFragmentStatBean> tmp = null;

    private PagerFragmentStatBean to = null;
    private Context context;

    private ViewPagerFragmentStat() {
        run = true;
    }

    public static ViewPagerFragmentStat getInstance() {
        if (instance == null) {
            instance = new ViewPagerFragmentStat();
        }
        return instance;
    }
    @Override
    public void sendRequest(Context context) {
        if (from != null) {
            if(from.id != to.id){
                T.i("from " + from.name + "id:" + from.id);
                AdhocTracker.incrementStat(context,"staytime-" + from.name,
                        to.t1-from.t1);
                // request staytime
                AdhocTracker.incrementStat(context,"event-" + from.name +
                        "-" + to.name, 1);
            }

        } else {

            AdhocTracker.incrementStat(context,"event-null-" + to.name, 1);

        }
        T.i("to " + to.name + "id:" + to.id);

        // from change

        from = to;
    }

    //    }
    class PagerFragmentStatBean {
        private String name;
        private long t1;
//        private boolean visiable;
        private int id;

    }


    public void setUserVisibleHint(Object fragment, boolean isVisibleToUser) {
        if(fragment == null){
            throw new InvalidParameterException("fragment need not null");
        }
        Class clazz = fragment.getClass();
        long t1 = System.currentTimeMillis();
        Object context = null;

        try {
            Field field = ReflectionUtil.getFieldByClassName(clazz,"mActivity","Fragment");
            context= ReflectionUtil.getFieldValue(field,fragment);
            if(context == null){
                T.i("context para is null : " + isVisibleToUser);
            }
        } catch (NoSuchFieldException e) {
            T.e(e);
        } catch (IllegalAccessException e) {
            T.e(e);
        }

        T.i("reflect time : " + (System.currentTimeMillis() - t1) );
        if (fragment == null) return;
        if (tmp == null) {
            tmp = new HashMap<Integer, PagerFragmentStatBean>();
        }

        PagerFragmentStatBean psb = new PagerFragmentStatBean();
//        psb.visiable = isVisibleToUser;
        psb.t1 = System.currentTimeMillis();
        psb.name = fragment.getClass().getSimpleName();
        psb.id = fragment.hashCode();

        tmp.put(psb.id,psb);

        if (isVisibleToUser) {
            to = psb;
            sendRequest((Context) context);
        }

    }

    public void destory() {
        from = null;
        to = null;
        tmp.clear();
        tmp = null;
        context = null;
        run = false;
    }
}
