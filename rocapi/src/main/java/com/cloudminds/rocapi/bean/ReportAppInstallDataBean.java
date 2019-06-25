package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2019/5/22.
 */

public class ReportAppInstallDataBean {
    public ReportAppInstallDataBean() {
    }
    /**
     * pkgName : com.cloudminds.pepper.hari
     * versionName : 9
     * versionCode : 9
     */

    private String pkgName;
    private String versionName;
    private int versionCode;

    public ReportAppInstallDataBean(String pkgName, String versionName, int versionCode) {
        this.pkgName = pkgName;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "pkgName='" + pkgName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
