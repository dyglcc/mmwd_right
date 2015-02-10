package qfpay.wxshop.data.netImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.OfficialGoodsResponseWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.main.fragment.OfficalListFragment;
import qfpay.wxshop.utils.T;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
/**
 * 一键代发删除
 *
 * */
public class OfficialGoodGetNetImpl extends AbstractNet {

    public OfficialGoodGetNetImpl(Context act) {
        super(act);
        setNoNeedShowDialog();
    }

    @Override
    protected Map<String, Object> setRequestParameter(Bundle parameter2) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        try {

            int page = parameter2.getInt("page");
            int offset = page * OfficalListFragment.page_size;
            int category = parameter2.getInt("category");

            // 买家秀 1

            String url = WDConfig.getInstance()
                    .getHuoyuanWholeDatalist(ConstValue.HTTP_GET)
                    + "?offset="
                    + offset
                    + "&length="
                    + OfficalListFragment.page_size;
            if (category != 0) {
                url = url + "&cid=" + category;
            }
            map.put(ConstValue.REQUEST_URL, url);
            map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_GET);
            /** 参数正确 */
            map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);

        } catch (Exception e) {
            T.e(e);
            /** 请求参数错误 */
            map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
        }
        return map;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Bundle jsonParse(String jsonStr) {
        ArrayList<HashMap<String, Object>> list = null;
        HashMap<String, Object> map = null;
        if (jsonStr != null && jsonStr.length() > 0) {
            try {
                Gson gosn = new Gson();
                OfficialGoodsResponseWrapper fromJson = gosn.fromJson(jsonStr,
                        OfficialGoodsResponseWrapper.class);
                if (!fromJson.getRespcd().equals("0000")) {
                    String errorMsg = fromJson.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
                }else{
                    qfpay.wxshop.data.beans.OfficialGoodsResponseWrapper.MsgsWrapper data = fromJson.getData();
                    list = new ArrayList<HashMap<String, Object>>();
                    map = new HashMap<String, Object>();
                    // 2014-04-24 14:52:31
                    // 处理日期
                    map.put("orderList", data);
                    list.add(map);

                    Long key = System.currentTimeMillis();
                    CacheData.getInstance().setData(key + "", list);
                    /** 界面上展示的时候直接根据key取存储类的数据 */
                    bundle.putString(ConstValue.CACHE_KEY, key + "");
                }

                bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_SUCCESS);
            } catch (Exception e) {
                T.e(e);
                bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
            }
        } else {
            bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
            T.i("jsonStr is null or jsonStr.length is 0");
        }
        return bundle;
    }
}
