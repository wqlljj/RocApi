package com.cloudminds.rocapi.bean;

import java.util.List;

/**
 * Created by cloud on 2018/12/7.
 */

public class ReportAlarmBean {


    /**
     * type :  reportAlarm
     * data : [{"oid":"retryTimeout","alarmSeq":"yyMMddhhmmss001","level":1,"timestamp":1231245,"module":"UWB_NAVI","status":1,"content":"retry timeout due to unknown issues"},{"oid":"retryTimeout","alarmSeq":"yyMMddhhmmss001","level":1,"timestamp":1231245,"module":"UWB_NAVI","status":1,"content":"retry timeout due to unknown issues"}]
     */

    private String type ="reportAlarm";
    private List<DataBean> data;

    public ReportAlarmBean() {
    }

    public ReportAlarmBean(List<DataBean> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * level : 1
         * timestamp : 1231245
         * module : UWB_NAVI
         * status : 1
         * content : retry timeout due to unknown issues
         */
        private String oid;
        private int level;
        private long timestamp;
        private String module;
        private int status;
        private String content;

        public DataBean() {
        }

        public DataBean(String oid, int level, long timestamp, String module, int status, String content) {
            this.oid = oid;
            this.level = level;
            this.timestamp = timestamp;
            this.module = module;
            this.status = status;
            this.content = content;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "oid='" + oid + '\'' +
                    ", level=" + level +
                    ", timestamp=" + timestamp +
                    ", module='" + module + '\'' +
                    ", status=" + status +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReportAlarmBean{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
