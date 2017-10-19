package qfpay.wxshop.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongyuangui on 15-5-20.
 */
public class ViewPagerFragmentStat {
    private static ViewPagerFragmentStat instance;
    private static final boolean VISIABLE = true;
    private static final boolean INVISIABLE = false;
    private PagerFragmentStatBean from = null;
    private HashMap<Integer, PagerFragmentStatBean> tmp = null;

    private PagerFragmentStatBean to = null;

    private ViewPagerFragmentStat() {

    }

    public static ViewPagerFragmentStat getInstance() {
        if (instance == null) {
            instance = new ViewPagerFragmentStat();
        }
        return instance;
    }

    //    }
    class PagerFragmentStatBean {
        private String name;
        private long t1;
        private boolean visiable;
        private int id;

    }

    private void startStat() {
        // 得到from
        if (from != null) {
            T.i("wwd", "from " + from.name + "id:" + from.id);
        } else {
            T.i("wwd", "from null");
        }
        T.i("wwd", "to " + to.name + "id:" + to.id);

        requestStat();

        from = to;
    }

    private void requestStat() {




    }

//    private void getPsbFrom() {
//        for (Map.Entry<Integer, PagerFragmentStatBean> entry : map.entrySet()) {
//            PagerFragmentStatBean pb = entry.getValue();
//            if (pb.visiable) {
//                PagerFragmentStatBean pbnew = tmp.get(pb.id);
//                if (pbnew != null && !pbnew.visiable) {
//                    from = pb;
//                }
//            }
//        }
//    }

    public void setUserVisibleHint(Object fragment, boolean isVisibleToUser) {

        if (fragment == null) return;
        if (tmp == null) {
            tmp = new HashMap<Integer, PagerFragmentStatBean>();
        }

//        PagerFragmentStatBean psbOld = map.get(fragment.hashCode());
//
//        // 新加入
//        // 以前加入1.原来显示，现在隐藏、显示
//        //
//        // 2原来隐藏 现在隐藏和显示
//
//        if (psbOld == null) {
//
//            // 新加入但是是false 相当于没有出现过
//            if (isVisibleToUser == false) {
//                return;
//            } else {
//                PagerFragmentStatBean psb = new PagerFragmentStatBean();
//                psb.visiable = VISIABLE;
//                psb.t1 = System.currentTimeMillis();
//                psb.name = fragment.getClass().getSimpleName();
//                psb.id = fragment.hashCode();
//            }
//            // 显示 1.
////
//        } else {
//            boolean visiableOld = psbOld.visiable;
//            if (visiableOld) {
//
//                if ( !isVisibleToUser) {
////                    统计时长，并且记录从哪里来
//                    from = psbOld;
//                }
//
//            }else { // invisiable 2 visiable
//                if(isVisibleToUser){
//
//                }
//
//            }
//        }
        PagerFragmentStatBean psb = new PagerFragmentStatBean();
        psb.visiable = isVisibleToUser;
        psb.t1 = System.currentTimeMillis();
        psb.name = fragment.getClass().getSimpleName();
        psb.id = fragment.hashCode();

        if (isVisibleToUser) {
            to = psb;
            startStat();
        }

    }


    public void destory() {
        from = null;
        to = null;
        tmp.clear();
        tmp = null;
    }
}
