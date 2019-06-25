package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2019/1/8.
 */

public class ReportBaseBean<D> {
    //reportConfig reportAttribute
    private String type = "reportAttribute";
    private D data;

    public ReportBaseBean(String type, D data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReportBaseBean{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
