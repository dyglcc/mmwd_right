package com.adhoc.adhocsdk;

import android.content.Context;
import android.content.pm.PackageManager;

import com.adhoc.utils.T;
import com.adhoc.utils.Toaster;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by dongyuangui on 15-4-16.
 */
public class AdhocClientIDHandler {
    private static AdhocClientIDHandler ourInstance = null;
    private String mClientId;

    public static AdhocClientIDHandler getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AdhocClientIDHandler(context);
        }
        return ourInstance;
    }

    /*
     * Generates a new client ID. Returns null if anything fails.
	 */
    private String generateNewClientId() {
        // If the client ID is not generated, generate it and store it in file.
        String clientId = UUID.randomUUID().toString().toLowerCase(Locale.ENGLISH);
        try {
            // 保存sharePre
            saveSharePre(clientId);
            // 保存sdcard
            saveSaveSDcard(clientId);
            // 保存内存
            mClientId = clientId;
            T.i("生成新的clientid " + clientId);
        } catch (Exception e) {
            T.e(e);
        }
        return clientId;
    }

    private void saveSaveSDcard(String clientId) {
        boolean bool = SDcardHandler.getInstance(mContext).writeFile(clientId);
        if (bool) {
            T.i("写ClientID成功");
        }
    }

    private void saveSharePre(String clientId) {
        SharePrefHandler.getInstance(mContext).saveString(AdhocConstants.SHARE_PREF_CLIENT_ID, clientId);
    }

    private void checkPermission(){
        PackageManager pm = mContext.getPackageManager();
        int hasPerm = pm.checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                mContext.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            // do stuff
            Toaster.toast(mContext,"请注册SDCARD读写权限");
        }
    }
    /*
     * This function is used to get Client ID.
	 */
    public String getClientId() {

        // 从内存中取数据
        if (mClientId != null && !mClientId.equals("")) {
            T.i("内存中取到clientid:" + mClientId);
            return mClientId;
        }

        String clientId = null;
        try {
            clientId = getFromSDCARD();
        } catch (Exception e) {
            T.e(e);
        }
        if (clientId != null && !clientId.equals("")) {
            mClientId = clientId;
            T.i("SDCARD取到clientid:" + clientId);
            return mClientId;
        }

        // 从SharePre文件取ClientID
        clientId = getFromSharePre();

        T.i("Share_pref取到clientid:" + clientId);

        if (clientId != null && !clientId.equals("")) {
            mClientId = clientId;
            return mClientId;
        }

        // 生成新的clientId
        return generateNewClientId();
    }

    private String getFromSharePre() {
        String id = SharePrefHandler.getInstance(mContext).getString(AdhocConstants.SHARE_PREF_CLIENT_ID);
        // 尝试备份SDCARD
        String clientID = SDcardHandler.getInstance(mContext).readFile();
        if (clientID == null && id != null && !id.equals("")) {
            SDcardHandler.getInstance(mContext).writeFile(id);
        }
        return id;
    }

    private String getFromSDCARD() {
        checkPermission();
        return SDcardHandler.getInstance(mContext).readFile();
    }

    private Context mContext;

    private AdhocClientIDHandler(Context context) {
        this.mContext = context;
    }
}
