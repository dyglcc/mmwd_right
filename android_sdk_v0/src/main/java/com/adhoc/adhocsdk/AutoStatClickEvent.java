package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adhoc.utils.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class AutoStatClickEvent {
    private static AutoStatClickEvent ourInstance = new AutoStatClickEvent();

    public static AutoStatClickEvent getInstance() {
        return ourInstance;
    }

    private AutoStatClickEvent() {
    }


    // 得到点击的Views
    private List<View> getClickViews(Activity activity, float x1, float y1) {
        List<View> clickViews = new ArrayList<View>();
        int x = (int) x1;
        int y = (int) y1;
        List<View> children = getAllChildViews(activity.getWindow().getDecorView());
        for (View viewchild : children) {

            String viewName = viewchild.getClass().getCanonicalName();

            // 去掉v4,7等系统控件
            if (viewName.indexOf("internal.widget") != -1) {
                continue;
            }
            // 去掉
            if (viewName.indexOf("android.support.") != -1) {
                continue;
            }
            boolean isTextView = viewchild instanceof TextView;

            // 没有id 并且还是非TextView
            if (viewchild.getId() == -1 && !isTextView) {
                continue;
            }
            int[] localChild = new int[2];
            viewchild.getLocationOnScreen(localChild);
            if (x >= localChild[0] && x <= localChild[0] + viewchild.getWidth() && y <= localChild[1] + viewchild.getHeight() && y >= localChild[1]) {
                clickViews.add(viewchild);
            }
        }
        return clickViews;

    }


    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    private String showAllViewInfo(List<View> views) {

        StringBuilder sb = new StringBuilder();

        for (View v : views) {

            String viewText = "";
            if (v instanceof TextView) {

                viewText = ((TextView) v).getText().toString();
            }
            sb.append(v.getClass().getCanonicalName() + ":" + viewText + "\n");

        }
        return sb.toString();


    }

    private String getShowViewsStrings(List<View> views) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < views.size(); i++) {

            View view = views.get(i);

            sb.append(view.getClass().getCanonicalName() + " @id :" + view.getId() + "\n");
        }
        return sb.toString();
    }

    public void autoTracking(final Context mContext,final MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    boolean isActivity = mContext instanceof Activity;

                    if (!isActivity) {

                        return;

                    }

                    List<View> clickeViews = getClickViews(((Activity) mContext), event.getRawX(), event.getRawY());

                    for (View view : clickeViews) {
                        if (view instanceof TextView) {
                            String text = ((TextView) view).getText().toString();
                            if (text.length() > 13) {
                                text = text.substring(0, 13) + "...";
                            }
                            String key = view.getId() + "_" + text + "_autoclick";

                            AdhocTracker.incrementStat(mContext,key, 1);

                            T.i("自动统计:key_" + (key));
                        }
                    }

                    T.i("点击view 列表:" + getShowViewsStrings(clickeViews));
                }
            }).start();

        }
    }

}
