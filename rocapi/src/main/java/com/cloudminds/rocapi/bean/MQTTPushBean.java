package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2018/12/6.
 */

public class MQTTPushBean {

    /**
     * id : push
     * seq : 2678
     * sid : 07178150-bbf6-11e8-a991-0100c737e981
     * from : rcuId|robotId
     * to : roc
     * timestamp : 1537459318297
     * tenantCode : cloudminds
     * message : {"type":"reportConfigStatus","data":{"id":"123","status":1}}
     */

    private String id;
    private String seq;
    private String sid;
    private String from;
    private String to;
    private long timestamp;
    private String tenantCode;
    private Object message;

    public MQTTPushBean() {
    }

    public MQTTPushBean(String id, String seq, String sid, String from, String to, long timestamp, String tenantCode, Object message) {
        this.id = id;
        this.seq = seq;
        this.sid = sid;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.tenantCode = tenantCode;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
//
//    public static class MessageBean {
//        /**
//         * type : reportConfigStatus
//         * data : {"id":"123","status":1}
//         */
//        //reportAppVersion reportConfigStatus reportAttribute
//
//        private String type;
//        private DataBean data;
//
//        public MessageBean() {
//        }
//
//        public MessageBean(String type, DataBean data) {
//            this.type = type;
//            this.data = data;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public DataBean getData() {
//            return data;
//        }
//
//        public void setData(DataBean data) {
//            this.data = data;
//        }
//
//        public static class DataBean {
//            public DataBean() {
//            }
//            /**
//             * id : 123
//             * status : 1
//             */
//            private String id;
//            private int status;
//
//            public DataBean(String id, int status) {
//                this.id = id;
//                this.status = status;
//            }
//
//            public String getId() {
//                return id;
//            }
//
//            public void setId(String id) {
//                this.id = id;
//            }
//
//            public int getStatus() {
//                return status;
//            }
//
//            public void setStatus(int status) {
//                this.status = status;
//            }
//
//            /**
//             * pkgName : com.cloudminds.pepper.hari
//             * versionName : 9
//             * versionCode : 9
//             */
//
//            private String pkgName;
//            private String versionName;
//            private int versionCode;
//
//            public DataBean(String pkgName, String versionName, int versionCode) {
//                this.pkgName = pkgName;
//                this.versionName = versionName;
//                this.versionCode = versionCode;
//            }
//
//            public String getPkgName() {
//                return pkgName;
//            }
//
//            public void setPkgName(String pkgName) {
//                this.pkgName = pkgName;
//            }
//
//            public String getVersionName() {
//                return versionName;
//            }
//
//            public void setVersionName(String versionName) {
//                this.versionName = versionName;
//            }
//
//            public int getVersionCode() {
//                return versionCode;
//            }
//
//            public void setVersionCode(int versionCode) {
//                this.versionCode = versionCode;
//            }
//        }
//    }
}
