package qfpay.wxshop.utils;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public final class QMMAlert {

    public interface OnAlertSelectId {
        void onClick(int whichButton);
    }

    private QMMAlert() {

    }

	/*
     * menu Text dialog
	 */

    public static Dialog showAlertTextView(final Context context,
                                           final String title, final String content, final String leftStr,
                                           String rightStr, OnClickListener clickRightListener) {
        final Dialog dlg = new Dialog(context, R.style.MyDialog);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.alert_dialog_show_text, null);
        final TextView textview = (TextView) layout
                .findViewById(R.id.tv_content);
        final TextView titleView = (TextView) layout
                .findViewById(R.id.tv_title);

        textview.setText(content);
        titleView.setText(title);
        Button btnLeft = (Button) layout.findViewById(R.id.btn_left);
        btnLeft.setText(leftStr);
        Button btnRight = (Button) layout.findViewById(R.id.btn_right);
        btnRight.setText(rightStr);
        if (rightStr == null || rightStr.equals("")) {
            btnRight.setVisibility(View.GONE);
        }
        if (leftStr == null || leftStr.equals("")) {
            btnLeft.setVisibility(View.GONE);
        }
        btnLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();

            }
        });

        btnRight.setOnClickListener(clickRightListener);

        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

	/*
	 * menu list dialog
	 */

    public static Dialog showAlertCenterMenu(final Context context,
                                             final String title, final String[] items, String exit,
                                             final OnAlertSelectId alertDo) {
        String cancel = "取消";
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheetContent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.alert_dialog_menu_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, title, items, exit,
                cancel);
        list.setAdapter(adapter);
        list.setDividerHeight(0);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!(title == null || title.equals("")) && position - 1 >= 0) {
                    alertDo.onClick(position - 1);
                    dlg.dismiss();
                    list.requestFocus();
                } else {
                    alertDo.onClick(position);
                    dlg.dismiss();
                    list.requestFocus();
                }

            }
        });
        // set a large value put it in bottom
        // Window w = dlg.getWindow();
        // WindowManager.LayoutParams lp = w.getAttributes();
        // lp.x = 0;
        // final int cMakeBottom = -1000;
        // lp.y = cMakeBottom;
        // lp.gravity = Gravity.BOTTOM;
        // dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

	/*
	 * menu list dialog
	 */

    public static Dialog showAlertWithListView(final Context context,
                                               final String title, int resImg, final String[] items, int selPos,
                                               final OnAlertSelectId alertDo,View ancorView) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheetRadio_list_view);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.alert_dialog_listmenu_layout_, null);

        TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
        ImageView ivIcon = (ImageView) layout.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setBackgroundResource(resImg);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        View view  = layout.findViewById(R.id.layout_title);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        RadioAdapter adapter = new RadioAdapter(context, items, selPos);
        list.setAdapter(adapter);
        list.setDividerHeight(0);
        dlg.setCancelable(true);
        dlg.setTitle(title);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                alertDo.onClick(position);
                dlg.dismiss();
                list.requestFocus();
            }
        });
        // set a large value put it in bottom
        // Window w = dlg.getWindow();
        // WindowManager.LayoutParams lp = w.getAttributes();
        // lp.x = 0;
        // final int cMakeBottom = -1000;
        // lp.y = cMakeBottom;
        // lp.gravity = Gravity.BOTTOM;
        // dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);

        // set position

        Window dialogWindow = dlg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        // view 的宽高
        int[] xin = new int[2];
        ancorView.getLocationInWindow(xin);

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)(context)).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;     // 屏幕宽度（像素）
        int screenHeight = metric.heightPixels;   // 屏幕高度（像素）

        lp.x = xin[0]-18; // 新位置X坐标
        lp.y = xin[1]-getStatusBarHeight(context); // 新位置Y坐标
        lp.width = screenWidth - xin[0]; // 宽度
        if(items.length > 6){
            lp.height = (int)(0.6* screenHeight);
        }
//        lp.alpha = 0.9f; // 透明度
        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);


        dlg.show();
        return dlg;
    }


    private static int getStatusBarHeight(Context con){
        Rect rect = new Rect();
        ((Activity)con).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    public static Dialog showAlert(final Context context, final String title,
                                   final String[] items, String exit, final OnAlertSelectId alertDo) {
        return showAlert(context, title, items, exit, alertDo, null);
    }

    /**
     * @param context Context.
     * @param title   The title of this AlertDialog can be null .
     * @param items   button name list.
     * @param alertDo methods call Id:Button + cancel_Button.
     * @param exit    Name can be null.It will be Red Color
     * @return A AlertDialog
     */
    public static Dialog showAlert(final Context context, final String title,
                                   final String[] items, String exit, final OnAlertSelectId alertDo,
                                   OnCancelListener cancelListener) {
        String cancel = context.getResources().getString(R.string.cancel);
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.alert_dialog_menu_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, title, items, exit,
                cancel);
        list.setAdapter(adapter);
        list.setDividerHeight(0);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!(title == null || title.equals("")) && position - 1 >= 0) {
                    alertDo.onClick(position - 1);
                    dlg.dismiss();
                    list.requestFocus();
                } else {
                    alertDo.onClick(position);
                    dlg.dismiss();
                    list.requestFocus();
                }

            }
        });
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

}

class AlertAdapter extends BaseAdapter {
    // private static final String TAG = "AlertAdapter";
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_EXIT = 2;
    public static final int TYPE_CANCEL = 3;
    private List<String> items;
    private int[] types;
    // private boolean isSpecial = false;
    private boolean isTitle = false;
    // private boolean isExit = false;
    private Context context;

    public AlertAdapter(Context context, String title, String[] items,
                        String exit, String cancel) {
        if (items == null || items.length == 0) {
            this.items = new ArrayList<String>();
        } else {
            this.items = stringsToList(items);
        }
        this.types = new int[this.items.size() + 3];
        this.context = context;
        if (title != null && !title.equals("")) {
            types[0] = TYPE_TITLE;
            this.isTitle = true;
            this.items.add(0, title);
        }

        if (exit != null && !exit.equals("")) {
            // this.isExit = true;
            types[this.items.size()] = TYPE_EXIT;
            this.items.add(exit);
        }

        if (cancel != null && !cancel.equals("")) {
            // this.isSpecial = true;
            types[this.items.size()] = TYPE_CANCEL;
            this.items.add(cancel);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0 && isTitle) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String textString = (String) getItem(position);
        ViewHolder holder;
        int type = types[position];
        if (convertView == null
                || ((ViewHolder) convertView.getTag()).type != type) {
            holder = new ViewHolder();
            if (type == TYPE_CANCEL) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_cancel, null);
            } else if (type == TYPE_BUTTON) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout, null);
            } else if (type == TYPE_TITLE) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_title, null);
            } else if (type == TYPE_EXIT) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_special, null);
            }

            // holder.view = (LinearLayout)
            // convertView.findViewById(R.id.popup_layout);
            holder.text = (TextView) convertView.findViewById(R.id.popup_text);
            holder.type = type;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(textString);
        return convertView;
    }

    static class ViewHolder {
        // LinearLayout view;
        TextView text;
        int type;
    }

    public static List<String> stringsToList(String[] paramArrayOfString) {
        if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
            return null;
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < paramArrayOfString.length; ++i)
            localArrayList.add(paramArrayOfString[i]);
        return localArrayList;
    }

}

class RadioAdapter extends BaseAdapter {
    private String[] items;
    private boolean isTitle = false;
    private Context context;

    private int selected = 0;

    public RadioAdapter(Context context, String[] items,
                        int selected) {
        this.selected = selected;
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0 && isTitle) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String textString = (String) getItem(position);
        ViewHolderRadio holder;
        if (convertView == null) {
            holder = new ViewHolderRadio();
            convertView = View.inflate(context,
                    R.layout.alert_dialog_radio_list_layout, null);
            holder.text = (TextView) convertView.findViewById(R.id.tv_text);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_radio);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderRadio) convertView.getTag();
        }
        holder.selected = false;
        if (position == selected) {
            holder.selected = true;
        }
        if (holder.selected) {
            holder.iv.setImageResource(R.drawable.icon_pushdown_check);
        } else {
            holder.iv.setImageDrawable(null);
        }
        holder.text.setText(textString);
        return convertView;
    }

    final class ViewHolderRadio {
        TextView text;
        ImageView iv;
        boolean selected;
    }

    public static List<String> stringsToList(String[] paramArrayOfString) {
        if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
            return null;
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < paramArrayOfString.length; ++i)
            localArrayList.add(paramArrayOfString[i]);
        return localArrayList;
    }

}
