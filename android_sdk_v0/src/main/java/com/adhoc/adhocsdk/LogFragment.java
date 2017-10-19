package com.adhoc.adhocsdk;

/**
 * Created by dongyuangui on 15-5-18.
 */
public class LogFragment {
    // 操作时间
    private long t1;
    // fragment 名称
    private String name;
    // 操作
    private String Oper;
    // id
    private String id;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long duration;

    public boolean isChanged2Hiden() {
        return changed2Hiden;
    }

    public void setChanged2Hiden(boolean changed2Hiden) {
        this.changed2Hiden = changed2Hiden;
    }

    // id
    private boolean changed2Hiden;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOper() {
        return Oper;
    }

    public void setOper(String oper) {
        Oper = oper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getT1() {
        return t1;
    }

    public void setT1(long t1) {
        this.t1 = t1;
    }

}
