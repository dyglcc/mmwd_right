package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;

import com.adhoc.utils.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ScreenShot {

//    private Context context;

    private ScreenShot() {

    }

    private static ScreenShot instance;

    public static ScreenShot getInstance() {
        if (instance == null) {
            instance = new ScreenShot();
        }
        return instance;

    }

    public File getScreenShotFile(Context context){
        return new File(context.getCacheDir() +"/"+
                AdhocConstants.ADHOC_FILE_PATH + "/" +"android_"+context.getClass().getSimpleName()+
                AdhocConstants.ADHOC_SCREEN_SHOT_FILE_SUFFIX);
    }
    public void shot(Context context) {

        File file = getScreenShotFile(context);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 清理
        if (!file.exists()) {
            System.err.print("文件不存在");
            try {
                // 删除原来文件
                file.delete();
                // 创建新文件
                file.createNewFile();
            } catch (IOException e) {
                T.e(e);
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            if (null != fos) {
                Bitmap map = takeScreenShot(context);
                if(map != null){
                    map.compress(Bitmap.CompressFormat.JPEG, AdhocConstants.QUALITY_SCREEN_SHOT, fos);
                }
            }
        } catch (FileNotFoundException e) {
            T.e(e);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap takeScreenShot(Context activity) {

        if (!(activity instanceof Activity)) {
            T.w("截屏出错：只截屏activity");
            return null;
        }
        View view = ((Activity) activity).getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        ((Activity) activity).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = ((Activity) activity).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity) activity).getWindowManager().getDefaultDisplay()
                .getHeight();
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width,
                height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap getScreenShotBimap(Context context) {
        Bitmap bimap = null;
        File file = getScreenShotFile(context);
        try {
            bimap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bimap;
    }

}
