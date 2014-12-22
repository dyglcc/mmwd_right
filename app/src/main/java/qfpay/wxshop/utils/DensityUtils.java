package qfpay.wxshop.utils;

import qfpay.wxshop.utils.MobAgentTools;
import android.content.Context;  

public class DensityUtils {  
  

	/**
	 * dip 2 px  
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * PX 2 DIP
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}  
