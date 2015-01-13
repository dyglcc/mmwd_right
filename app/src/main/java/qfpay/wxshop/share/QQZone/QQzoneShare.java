package qfpay.wxshop.share.QQZone;

import com.tencent.connect.share.QzoneShare;

/**
 * Created by dongyuangui on 15-1-13.
 */
public class QQzoneShare {

    private void shareToQzone () {
        //分享类型
        params.putString(QzoneShare.SHARE_TO_QQ_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
        mTencent.shareToQzone(activity, params, new BaseUiListener());
    }
}
