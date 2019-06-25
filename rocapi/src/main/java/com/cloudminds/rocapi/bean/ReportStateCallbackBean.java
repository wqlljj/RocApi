package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2019/2/28.
 */

public class ReportStateCallbackBean {

    public ReportStateCallbackBean() {
        type = "pushStateCallback";
        data=new DataBean();
    }

    /**
     * type : pushStateCallback
     * data : {}
     */

    private String type;
    private DataBean data;

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
