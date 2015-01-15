package qfpay.wxshop.data.netImpl;

import java.io.Serializable;
import java.util.List;

import qfpay.wxshop.data.beans.BusinessCommunityMyNotificationBean;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface BusinessCommunityService {

    /**
     * 获取我的动态帖子列表
     * @param last_fid  参数为上次返回的最后一个帖子
     * @return
     */
	@GET("/my_forum")
    MyDynamicNotesListDataWrapper getMyDynamicNotesList(@Query("last_fid") String last_fid);

    /**
     * 第一次获取我的动态帖子列表，没有参数
     * @return
     */
    @GET("/my_forum")
    MyDynamicNotesListDataWrapper getMyDynamicNotesListFirstTime();

    /**
     * 获取某一小组的帖子列表
     * @param g_id
     * @param last_fid
     * @return
     */
    @GET("/group")
    MyDynamicNotesListDataWrapper getOneTopicNotesList(@Query("g_id") String g_id ,@Query("last_fid") String last_fid);

    /**
     * 第一次获取某一组内的帖子列表
     * @param g_id
     * @return
     */
    @GET("/group")
    MyDynamicNotesListDataWrapper getOneTopicNotesListFirstTime(@Query("g_id") String g_id);

    /**
     * 点赞或取消点赞
     * @return
     */
    @GET("/like/b_topic")
    CommonJsonBean setPraiseState(@Query("id") String id,@Query("flag") String flag);


    /**
     * 发帖
     * @param g_id
     * @param content
     * @param file
     * @return
     */
    @FormUrlEncoded
    @POST("/post")
    CommonJsonBean publishOneNote(@Field("g_id") String g_id,@Field("content") String content
            ,@Field("img") String img);

    /**
     * 发帖 不带图片url
     * @param g_id
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("/post")
    CommonJsonBean publishOneNoteNoImg(@Field("g_id") String g_id,@Field("content") String content);
    /**
     * 发表评论
     * @param t_id
     * @param content
     * @return
     */
    @FormUrlEncoded@POST("/reply")
    CommonJsonBean publishReply(@Field("t_id") String t_id,@Field("content") String content);

    /**
     * 获取我加入的小组列表
     */
    @GET("/my_group")
    TopicsListDataWrapper getMyTopicList(@Query("u_id") String u_id);
    /**
     * 获取所有小组列表
     */
    @GET("/all_group")
    TopicsListDataWrapper getALlTopicList();

    /**
     * 获取有关我的消息和是否有新帖
     * @return
     */
    @GET("/remind")
    BusinessCommmunityMyNotificationDataWrapper getAboutMyNotification();


    public static class BusinessCommmunityMyNotificationDataWrapper extends CommonJsonBean{
        private static final long serialVersionUID = 1L;
        public BusinessCommmunityMyNotificationListWrapper data;
    }

    public static class BusinessCommmunityMyNotificationListWrapper implements Serializable{
        private static final long serialVersionUID = 1L;
        public String tag;//有无关于我的消息的标志 0 没有 1 有
        public String has_new;//是否有新帖 1 有，0 没有
        public String count;//如果有关于我的消息，count为消息数量
        public List<BusinessCommunityMyNotificationBean> items;

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if(items!=null){

                for(BusinessCommunityMyNotificationBean businessCommunityMyNotificationBean:items){
                    stringBuilder.append(businessCommunityMyNotificationBean.toString());
                }
            }
            return "BusinessCommmunityMyNotificationListWrapper{" +
                    "tag='" + tag + '\'' +
                    ", has_new='" + has_new + '\'' +
                    ", count='" + count + '\'' +
                    stringBuilder.toString()+
                    '}';
        }
    }

	public static class MyDynamicNotesListDataWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		public MyDynamicNotesListWrapper data;
	}
	
	public static class MyDynamicNotesListWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		public List<MyDynamicItemBean0> items;
	}
    public static class TopicsListDataWrapper extends CommonJsonBean {
        private static final long serialVersionUID = 1L;
        public TopicsListWrapper data;
    }

    public static class TopicsListWrapper implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<MyTopicBean> items;
    }

}
