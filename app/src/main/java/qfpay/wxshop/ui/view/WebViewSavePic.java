package qfpay.wxshop.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.Toaster;

/**
 * Created by dongyuangui on 15-1-22.
 */
public class WebViewSavePic extends WebView {
    Context context;


    private void init(){

        Toaster.l(context,"long inti");
        setFocusable(true);
        setLongClickable(true);
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                WebView.HitTestResult result = getHitTestResult();
                if (result != null) {
                    int type = result.getType();
                    if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                        imgurl = result.getExtra();
                        showAlert();
                    }
                }

                Toaster.l(context,"long press");
                return false;
            }
        });
    }
    public WebViewSavePic(Context context) {
        super(context);
        this.context = context;
        init();
    }


    public WebViewSavePic(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public WebViewSavePic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private String imgurl = "";

    /**
     * 功能：长按图片保存到手机
     */

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    private void showAlert() {
        String[] items = new String[]{"保存到手机"};
        QMMAlert.showAlert(context, "图片",
                items, null, new QMMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        if (!ConstValue.haveSdcard()) {
                            Toast.makeText(
                                    context,
                                    context.getResources().getString(
                                            R.string.no_found_SD),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        switch (whichButton) {

                            case 0:
                                new SaveImage().execute();
                                break;
                            default:
                                break;
                        }
                    }

                });
    }

    /**
     * 功能：用线程保存图片
     *
     * @author wangyp
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/miaomiaowd");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/miaomiaowd/" + new Date().getTime() + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toaster.l(context,result);
        }
    }
}
