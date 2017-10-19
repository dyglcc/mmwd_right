package com.adhoc.adhocsdk;


import android.content.Context;

import com.adhoc.utils.ReflectionUtil;
import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by dongyuangui on 15-5-27.
 */
public class StatFragment implements PageStat {
    private Context context;
    private static StatFragment ourInstance = new StatFragment();
    private static final int DETER = 3000;
//    private ArrayList<Object> dupTrueList = new ArrayList<Object>();

    public static StatFragment getInstance() {
        return ourInstance;
    }

    public boolean resumeForeground;

    private StatFragment() {
    }

    public static ArrayList<Object> list = new ArrayList<Object>();

    final class FragmentStatBean {

        private String name;
        private long t1;
        private int id;

    }

    private FragmentStatBean from;
    private FragmentStatBean to;


    public synchronized void dump() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DETER);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                T.i("list size:" + list.size());
                // 去掉父类的fragment 避免统计的重复
                reMoveParents();
                // display
                // delete it for debug

                T.i("list size after delete:" + list.size());
//                for (int i =  - 1; i >= 0; i--) {
                for (int i = 0; i < list.size(); i++) {
                    Object fragment = list.get(i);
                    // null able
                    if (fragment == null) {
                        T.w("fragment sudden null ! position: " + i);
                        continue;
                    }
                    boolean visiable = ifVisiable(fragment);
//                    boolean fatherVisiable = fatherIsVisiable(fragment);
//                    T.i("father Visiable lllllllast result :" + fatherVisiable);
                    if (visiable && fatherIsVisiable(fragment)) {

                        to = new FragmentStatBean();
                        to.id = fragment.hashCode();
                        to.name = fragment.getClass().getSimpleName();
                        to.t1 = System.currentTimeMillis() - DETER;
                        T.i(fragment.getClass().getSimpleName() + " visiable : hituser & fathervisiable all  true");
                        sendRequest(context);
                        break; // 有可能有两个fragment true；
                    }
                }
            }
        }).start();

    }

    private boolean ifVisiable(Object fragment) {
        Method visiableMethod = ReflectionUtil.getMethodByClassName(fragment.getClass(), "isVisible", "Fragment");
        Method hituserMethod = ReflectionUtil.getMethodByClassName(fragment.getClass(), "getUserVisibleHint", "Fragment");
//        boolean fatherVisiable = fatherIsVisiable(fragment);
        boolean visiable = false;
        boolean hituser = false;
        try {
            visiable = (Boolean) visiableMethod.invoke(fragment);
            hituser = (Boolean) hituserMethod.invoke(fragment);
        } catch (IllegalAccessException e) {
            T.e(e);
        } catch (InvocationTargetException e) {
            T.e(e);
        }
        return visiable && hituser;
    }

    private boolean fatherIsVisiable(Object fragment) {


        Object parent = null;
        while ((parent = getParentObj(fragment)) != null) {
            boolean result = ifVisiable(parent);
            T.i("father : isvisiable" + result  + " " + fragment.getClass().getName());
            if (result == false) {
                return false;
            }else{
                return fatherIsVisiable(parent);
            }
        }
        return true;
    }

//    private boolean isDup(Object obj){
//
//        for(Object item : dupTrueList){
//            if(item.hashCode() == obj.hashCode()){
//                return true;
//            }
//        }
//        return false;
//
//    }

    // 去掉重复的true
//    private void delDupTrue() {
//        for (Iterator<Object> iterator = list.iterator(); iterator.hasNext(); ) {
//
//            Object obj = iterator.next();
//
//            if (isDup(obj)){
//                T.i("remove dup visiable true " + obj.getClass().getName());
//                iterator.remove();
//            }
//            dupTrueList.clear();
//        }
//    }

    // 去掉指定object
    private Object getParentObj(Object fragment) {
        // 去掉父fragment
        try {
            Field field = ReflectionUtil.getFieldByClassName(fragment.getClass(), "mParentFragment", "Fragment");

            return ReflectionUtil.getFieldValue(field, fragment);

//            if (obj != null) {
//
////                T.i("remove list value : " + obj.getClass().getName());
//
//                list.remove(obj);
//
//            }
        } catch (NoSuchFieldException e) {
            T.e(e);
        } catch (IllegalAccessException e) {
            T.e(e);
        }
        return null;
    }

    private void reMoveParents() {

        ArrayList<Object> parents = getParents();

        for (Iterator<Object> iterator = list.iterator(); iterator.hasNext(); ) {

            Object obj = iterator.next();

            if (isParent(obj, parents)) {

                iterator.remove();

            }
        }
//        for (Object object : list) {
//
//            getParentObj(object);
//        }
    }

    private boolean isParent(Object obj, ArrayList<Object> parents) {
        for (Object item : parents) {

            if (item.hashCode() == obj.hashCode()) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Object> getParents() {
        ArrayList<Object> parents = new ArrayList<Object>();
        for (int i = 0; i < list.size(); i++) {
            Object obj = getParentObj(list.get(i));
            if (obj != null) {
                parents.add(obj);
            }
        }
        return parents;
    }

    public void add(Context context, Object fragment) throws IllegalAccessException {
        T.i("fragment onCreate" + fragment.getClass().getName());
        // 首先 onResume方法中加入
        // 检查是否存在不存在就加入
        this.context = context;
//        boolean exist = false;
//        for (int i = 0; i < list.size(); i++) {
//
//            Object obj = list.get(i);
//            T.i(obj.getClass().getName());
//            if (obj.hashCode() == fragment.hashCode()) {
//                exist = true;
//            }
//        }
//        if (!exist) {
//            T.i("add exist : "+ false);
//        try {
//            Field field = ReflectionUtil.getFieldByClassName(fragment.getClass(),"mUserVisibleHint","Fragment");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        list.add(fragment);
//        }
    }

    public void onFragmentDestory(Object fragment) {
        list.remove(fragment);
    }

    public void destory() {
        list.clear();
    }

    public void onBack2Menu(Context context) {
        if (from != null) {
            T.i("onBack2Menu : from " + from.name + "is null");
        }
        to = null;
        on2MenuSendStay(context);
        from = null;
        destory();
    }


    private String lastFromToRequestStr;
    private String lastStayTimeStr;
    private long lastFromToTime;
    private long lastStayTime;

    @Override
    public void sendRequest(Context context) {
//        T.i("send request method!");
        if (from != null) {

            T.i("send request to " + to.id + " to .name " + to.name);
            if (from.id != to.id) { // 避免从A到A

                T.i(from.id + " 比较 " + to.id);
                long duration = to.t1 - from.t1;
                // 小于300毫秒不记录统计
                if (duration < 200) {
                    T.i("小于 300 毫秒 忽略页面跳转");
                }
//                if (lastFromToRequestStr.equals(from.name + (to.t1 - from.t1))
//                        && (System.currentTimeMillis() - lastFromToTime) < 100) {
//                }
                T.i("from " + from.name + "id:" + from.id);
                AdhocTracker.incrementStat(context, "staytime-" + from.name,
                        to.t1 - from.t1);

                // request staytime
                AdhocTracker.incrementStat(context, "event-" + from.name +
                        "-" + to.name, 1);
                from = to;
                T.i("set from is " + to.name);
            } else {
                T.i("id 相同");
            }

        } else {
            T.i("from  null");
            AdhocTracker.incrementStat(context, "event-null-" + to.name, 1);
            from = to;
            T.i("set from is " + to.name);
        }
        T.i("to " + to.name + "id:" + to.id);
    }

    private void on2MenuSendStay(Context context) {
        if (from != null) {
            T.i("2 munu send request");
            AdhocTracker.incrementStat(context, "staytime-" + from.name,
                    System.currentTimeMillis() - from.t1);
        }
    }
}

