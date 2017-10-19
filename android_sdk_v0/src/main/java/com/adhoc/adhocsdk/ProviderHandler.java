package com.adhoc.adhocsdk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.adhoc.utils.T;
import com.adhoc.utils.Toaster;

/**
 * Created by dongyuangui on 15-5-6.
 */
public class ProviderHandler {
    private static ProviderHandler instance;
    private Context context;

    public static ProviderHandler getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderHandler(context);
        }
        return instance;
    }

    private ProviderHandler(Context context) {
        this.context = context;
    }

    public int searchScan() {
        int id = 0;
        String uriStr = "content://com.example.scannertest.provider/scan";
        Cursor cursor = context.getContentResolver().query(Uri.parse(uriStr), null, null, null, null);
        if(cursor == null){
            return 0;
        }
        while (cursor.moveToNext()) {
            id++;
        }
        T.i(id + " seach result");
        return id;
    }

    public void delScan() {

        String uriStr = "content://com.example.scannertest.provider/scan";

        int count = context.getContentResolver().delete(
                Uri.parse(uriStr), null, null);
        if (count > 0) {
            T.i("tester scan is success");
        }

    }

}
