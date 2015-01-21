package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 我的动态 帖子中 点赞数据
 * Created by zhangzhichao on 2014/12/29.
 */
public class MyDynamicItemLinkDataBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    @Override
    public String toString() {
        return "MyDynamicItemLinkDataBean{" +
                "like_count='" + like_count + '\'' +
                ", is_liked='" + is_liked + '\'' +
                ", liked_user=" + liked_user +
                '}';
    }

    private String like_count;//点赞数

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    private String is_liked;//是否点过赞 0：未赞  1：赞过
    private List<String> liked_user;//点赞欢迎您过户id  格式为 [1023,2921,19239,299320]

    public List<String> getLiked_user() {
        return liked_user;
    }

    public void setLiked_user(List<String> liked_user) {
        this.liked_user = liked_user;
    }
}

