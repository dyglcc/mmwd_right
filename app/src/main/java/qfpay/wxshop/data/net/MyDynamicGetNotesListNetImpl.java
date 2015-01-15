package qfpay.wxshop.data.net;

import android.content.Context;
import android.os.Bundle;

import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.utils.T;

/**
 * 获取我的动态 帖子列表数据
 * Created by zhangzhichao on 2014/12/29.
 */
public class MyDynamicGetNotesListNetImpl extends AbstractNet{
    public MyDynamicGetNotesListNetImpl(Context context){
        super(context);
        setNoNeedShowDialog();
    }
    @Override
    protected Map<String, Object> setRequestParameter(Bundle parameter2) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        try {
            if (!parameter2.getString("last_fid").equals("")) {
                map.put("last_fid", parameter2.getString("last_fid"));
            }

            map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
                    .getMyDynamicNotesListUrl());
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

    @Override
    protected Bundle jsonParse(String jsonStr) {
        System.out.println("我的动态返回数据----->"+jsonStr);
        return null;
    }
}
