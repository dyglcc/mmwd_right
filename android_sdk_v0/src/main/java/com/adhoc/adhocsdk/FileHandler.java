package com.adhoc.adhocsdk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

/**
 * This class provides a basic function to fetch/write a file in Android.
 */
public class FileHandler {

    protected final Context mContext;

    protected FileHandler(Context context) {
        mContext = context.getApplicationContext();
    }

    /*
     * Read content from file. Returns null if it fails to load.
     */
    public String readFile(String fileName, int maxLen) {
        String content = null;
        try {
            FileInputStream inputStream = mContext.openFileInput(fileName);
            byte[] buffer = new byte[maxLen];
            int readLen = inputStream.read(buffer, 0, maxLen);
            if (readLen <= 0 || inputStream.available() > 0) {
                T.i("Either file (" + fileName + ") is too long or it is corrupted.");
                inputStream.close();
                mContext.deleteFile(fileName);
            } else {
                content = new String(buffer, 0, readLen);
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            T.i("Fails to read file: " + fileName);
            mContext.deleteFile(fileName);
        }
        return content;
    }


    /*
     * Writes a string into a file. If failed, return false.
     */
    public boolean writeFile(String fileName, String data) {
        try {
            FileOutputStream outStream =
                    mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream.write(data.getBytes());
            outStream.close();
            return true;
        } catch (FileNotFoundException e) {
            T.i( "File Not Found Error. Failed to create one.");
            return false;
        } catch (IOException e) {
            T.i("Failed to write to file.");
            return false;
        }
    }

}
