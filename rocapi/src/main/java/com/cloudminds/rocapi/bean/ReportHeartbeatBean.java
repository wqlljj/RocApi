package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2018/12/10.
 */

public class ReportHeartbeatBean {

    /**
     * type : onlinePing
     * data : {}
     */

    private String type ="onlinePing";
    private DataBean data;

    public ReportHeartbeatBean() {
        data=new DataBean();
    }

    public ReportHeartbeatBean(DataBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
    }
}
