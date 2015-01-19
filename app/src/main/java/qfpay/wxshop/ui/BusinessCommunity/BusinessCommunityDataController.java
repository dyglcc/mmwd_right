package qfpay.wxshop.ui.BusinessCommunity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.event.LogoutEvent;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

@EBean(scope = Scope.Singleton) public class BusinessCommunityDataController {
    private String last_fid="";//我的动态页上次返回的最后一个帖子的id
    private String topic_last_fid="";//某一小组动态上次返回的最后一个帖子的id
	private List<MyDynamicItemBean0>   data       = new ArrayList<MyDynamicItemBean0>();
    private List<MyDynamicItemBean0>   notesListOfOneTopic = new ArrayList<MyDynamicItemBean0>();
    private List<MyTopicBean> myTopicBeanList = new ArrayList<MyTopicBean>();
	@Bean   RetrofitWrapper                  netWrapper;
	private BusinessCommunityService                 netService;
	private SoftReference<BusinessCommunityCallback> callback;
    private boolean 						 hasNext    = false;

	@AfterInject void init() {
		netService = netWrapper.getNetService(BusinessCommunityService.class , WDConfig.SOCIAL_URL);
		EventBus.getDefault().register(BusinessCommunityDataController.this);
	}
    public void onEvent(LogoutEvent event) {
    }

    /**
     * 重新加载当前页数据
     */
    public void reloadData(){
        getMyDynamicNotesListFromServer(last_fid);
    }

    /**
     * 请求服务器获取我的动态页帖子列表
     * @param last_fid  上次返回的最后一个帖子id
     */
    @Background
    void getMyDynamicNotesListFromServer(String last_fid){
        try {
            BusinessCommunityService.MyDynamicNotesListDataWrapper dataWrapper = null;
            if(last_fid.equals("")){//last_fid为空，表明是第一次请求
                dataWrapper =  netService.getMyDynamicNotesListFirstTime();
            }else{
                dataWrapper =  netService.getMyDynamicNotesList(last_fid);
            }
            if (dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
                if(!last_fid.equals("")){
                    data.addAll(dataWrapper.data.items);
                }else{
                    data.clear();
                    data.addAll(0,dataWrapper.data.items);
                }
                setLast_fid(data.get(data.size()-1).getId());
                if(dataWrapper.data.items.size()>0){
                    hasNext = true;
                }else{
                    hasNext = false;
                }
                if(callback!=null&&callback.get()!=null){
                    callback.get().onSuccess();
                }
            }else{
                if(callback!=null&&callback.get()!=null){
                    callback.get().onServerError(dataWrapper.getRespmsg());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(callback!=null&&callback.get()!=null){
                callback.get().onNetError();
            }
        }
    }

    /**
     * 获取某一小组内的帖子列表
     * @param g_id
     */
    @Background
    void getNotesListOfTopicFromServer(String g_id){
        try {
            BusinessCommunityService.MyDynamicNotesListDataWrapper dataWrapper = null;
            if(getTopic_last_fid().equals("")){//last_fid为空，表明是第一次请求
                dataWrapper =  netService.getOneTopicNotesListFirstTime(g_id);
            }else{
                dataWrapper =  netService.getOneTopicNotesList(g_id, topic_last_fid);
            }
            if (dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
                if(!getTopic_last_fid().equals("")){
                    notesListOfOneTopic.addAll(dataWrapper.data.items);
                }else{
                    notesListOfOneTopic.clear();
                    notesListOfOneTopic.addAll(0,dataWrapper.data.items);
                }
                setTopic_last_fid(notesListOfOneTopic.get(notesListOfOneTopic.size() - 1).getId());
                if(dataWrapper.data.items.size()>0){
                    hasNext = true;
                }else{
                    hasNext = false;
                }
                if(callback!=null&&callback.get()!=null){
                    callback.get().onSuccess();
                }
            }else{
                if(callback!=null&&callback.get()!=null){
                    callback.get().onServerError(dataWrapper.getRespmsg());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(callback!=null&&callback.get()!=null){
                callback.get().onNetError();
            }
        }
    }

    /**
     * 点赞和取消点赞
     * @param id
     * @param flag
     */
    @Background
    public void setPriaseState(String id,String flag){
        netService.setPraiseState(id,flag);
    }

    /**
     * 发表评论
     * @param t_id
     * @param content
     */
    @Background
    public void publishReply(String t_id,String content){
        try{
            if(callback!=null&&callback.get()!=null){
                callback.get().refresh();
            }
            RetrofitWrapper.CommonJsonBean commonJsonBean = netService.publishReply(t_id,content);
            if(commonJsonBean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                if(callback!=null&&callback.get()!=null){
                    callback.get().onSuccess();
                }
            }else{
                if(callback!=null&&callback.get()!=null) {
                    callback.get().onServerError(commonJsonBean.getRespmsg());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            if(callback!=null&&callback.get()!=null) {
                callback.get().onNetError();
            }
        }
    }

    /**
     * 获取我加入的话题列表
     * @param u_id
     */
    @Background
    public void getMyTopicList(String u_id){
        try{
            if(callback!=null&&callback.get()!=null){
                callback.get().refresh();
            }
             BusinessCommunityService.TopicsListDataWrapper topicsListDataWrapper = netService.getMyTopicList(u_id);

            if(topicsListDataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                myTopicBeanList.clear();
                myTopicBeanList = topicsListDataWrapper.data.items;
                if(callback!=null&&callback.get()!=null){
                    callback.get().onSuccess();
                }
            }else{
                if(callback!=null&&callback.get()!=null) {
                    callback.get().onServerError(topicsListDataWrapper.getRespmsg());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            if(callback!=null&&callback.get()!=null) {
                callback.get().onNetError();
            }
        }
    }

    /**
     * 获取所有的话题列表
     * @param
     */
    public BusinessCommunityService.TopicsListDataWrapper getAllTopicList(){
        try{
            BusinessCommunityService.TopicsListDataWrapper topicsListDataWrapper = netService.getALlTopicList();
            if(topicsListDataWrapper!=null){
                return topicsListDataWrapper;
            }
        }catch(Exception e){
            e.printStackTrace();
            return  null;
        }
            return null;
    }

    /**
     * 发帖
     * @param t_id
     * @param content
     * @param file
     */
    @Background
    public void publishOneNote(String t_id,String content,String file){
        try{
            if(callback!=null&&callback.get()!=null){
                callback.get().refresh();
            }
            RetrofitWrapper.CommonJsonBean commonJsonBean;
            if(file.equals("")){
                commonJsonBean =  netService.publishOneNoteNoImg(t_id, content);
            }else{
                commonJsonBean =  netService.publishOneNote(t_id, content, file);
            }
            if(commonJsonBean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                if(callback!=null&&callback.get()!=null){
                    callback.get().onSuccess();
                }
            }else{
                if(callback!=null&&callback.get()!=null) {
                    callback.get().onServerError(commonJsonBean.getRespmsg());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            if(callback!=null&&callback.get()!=null) {
                callback.get().onNetError();
            }
        }
    }

    /**
     * 获取关于我的消息和是否有新帖
     * @return
     */
    public BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper getAboutMyNotification(){
        try{
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper data = netService.getAboutMyNotification();
            return data;
        }catch(Exception e){
            e.printStackTrace();
            return  null;
        }

    }

    /**
     * 根据用户id得到店铺id
     * @param userId
     * @return
     */
    public BusinessCommunityService.ShopIdDataWrapper getShopIdByUserId(String userId){
        BusinessCommunityService.ShopIdDataWrapper response = netService.getShopIdByUserId(userId);
        return response;
    }

    /**
     * 得到当前已获取的列表数据
     * @return
     */
    public List<MyDynamicItemBean0> getCurrentList(){
        return data;
    }

    public void setData(List<MyDynamicItemBean0> data) {
        this.data = data;
    }

    /**
     * 得到小组列表数据
     * @return
     */
    public List<MyTopicBean> getMyTopicBeanList() {
        return myTopicBeanList;
    }

    public void setMyTopicBeanList(List<MyTopicBean> myTopicBeanList) {
        this.myTopicBeanList = myTopicBeanList;
    }

    /**
     * 在所有操作前设置callback
     */
    public BusinessCommunityDataController setCallback(BusinessCommunityCallback callback) {
        this.callback = new SoftReference<BusinessCommunityDataController.BusinessCommunityCallback>(callback);
        return this;
    }

    public void removeCallback() {
        if (callback != null) {
            callback.clear();
        }
    }

    public static interface BusinessCommunityCallback {
        void onSuccess();

        void onNetError();

        void onServerError(String msg);

        void refresh();
    }

    public String getLast_fid() {
        return last_fid;
    }

    public void setLast_fid(String last_fid) {
        this.last_fid = last_fid;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getTopic_last_fid() {
        return topic_last_fid;
    }

    public void setTopic_last_fid(String topic_last_fid) {
        this.topic_last_fid = topic_last_fid;
    }

    public List<MyDynamicItemBean0> getNotesListOfOneTopic() {
        return notesListOfOneTopic;
    }

    public void setNotesListOfOneTopic(List<MyDynamicItemBean0> notesListOfOneTopic) {
        this.notesListOfOneTopic = notesListOfOneTopic;
    }
}
