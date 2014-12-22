package qfpay.wxshop.data.netImpl;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by LiFZhe on 12/3/14.
 */
public interface CollectNetService {
    @GET("/collect")
    String collectData(@Query("OS") String os,
                     @Query("app") String app,
                     @Query("userid") String userid,
                     @Query("clicktype") String type,
                     @Query("clicktime") String time,
                     @Query("appversion") String version);
}
