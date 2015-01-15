package qfpay.wxshop.data.beans;

import java.io.Serializable;

/**
 * 我的动态 正常帖子中的 回复数据bean
 * Created by zhangzhichao on 2014/12/29.
 */
public class MyDynamicItemReplyBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String t_id;//帖子id
    private String content;//评论内容
    private String u_avatar;//评论用户头像链接
    private String create_time;//评论创建时间
    private String update_time;//评论更新时间
    private String floor;//楼层
    private String u_name;//评论用户昵称
    private String u_id;//评论用户id
    private String id;//评论id

    @Override
    public String toString() {
        return "MyDynamicItemReplyBean{" +
                "t_id='" + t_id + '\'' +
                ", content='" + content + '\'' +
                ", u_avatar='" + u_avatar + '\'' +
                ", create_time='" + create_time + '\'' +
                ", floor='" + floor + '\'' +
                ", u_name='" + u_name + '\'' +
                ", u_id='" + u_id + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getU_avatar() {
        return u_avatar;
    }

    public void setU_avatar(String u_avatar) {
        this.u_avatar = u_avatar;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
