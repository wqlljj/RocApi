package com.cloudminds.roc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cloud on 2019/1/8.
 */

public class ReportConfigDataBean {

    /**
     * tenantCode : cloudminds
     * userCode : 用户名
     * userPwd : 用户密码
     * pushToken : 推送的token
     * serviceCode : pepper
     * robotType : pepper
     * vpnProfile : xxxxxxxxxxx
     * initAppConfig : xxxxxxxxxxx
     * hariAddress : {"\u201cip\u201d":"\u201d0.0.0.0\u201d","\u201cport\u201d":1234}
     * iceAddress : {"endpoints":"AiasIceGrid/Query","locator":"AiasIceGrid/Locator:default -h 10.11.34.173 -p 4066"}
     * pushAddress : {"privateIp":"192.168.1.1","privatePort":"1883","publicIp":"211.211.211.211","publicPort":"1883"}
     */

    private String tenantCode;
    private String userCode;
    private String userPwd;
    private String pushToken;
    private String serviceCode;
    private String robotType;
    private String vpnProfile;
    private String initAppConfig;
    private HariAddressBean hariAddress;
    private IceAddressBean iceAddress;
    private PushAddressBean pushAddress;
    private DefaultConfigBean defaultConfig;

    public String getTenantCode() {
        return tenantCode;
    }

    public DefaultConfigBean getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(DefaultConfigBean defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getRobotType() {
        return robotType;
    }

    public void setRobotType(String robotType) {
        this.robotType = robotType;
    }

    public String getVpnProfile() {
        return vpnProfile;
    }

    public void setVpnProfile(String vpnProfile) {
        this.vpnProfile = vpnProfile;
    }

    public String getInitAppConfig() {
        return initAppConfig;
    }

    public void setInitAppConfig(String initAppConfig) {
        this.initAppConfig = initAppConfig;
    }

    public HariAddressBean getHariAddress() {
        return hariAddress;
    }

    public void setHariAddress(HariAddressBean hariAddress) {
        this.hariAddress = hariAddress;
    }

    public IceAddressBean getIceAddress() {
        return iceAddress;
    }

    public void setIceAddress(IceAddressBean iceAddress) {
        this.iceAddress = iceAddress;
    }

    public PushAddressBean getPushAddress() {
        return pushAddress;
    }

    public void setPushAddress(PushAddressBean pushAddress) {
        this.pushAddress = pushAddress;
    }

    public static class HariAddressBean {
        @SerializedName("“ip”")
        private String _$Ip149; // FIXME check this code
        @SerializedName("“port”")
        private int _$Port181; // FIXME check this code

        public String get_$Ip149() {
            return _$Ip149;
        }

        public void set_$Ip149(String _$Ip149) {
            this._$Ip149 = _$Ip149;
        }

        public int get_$Port181() {
            return _$Port181;
        }

        public void set_$Port181(int _$Port181) {
            this._$Port181 = _$Port181;
        }

        @Override
        public String toString() {
            return "HariAddressBean{" +
                    "_$Ip149='" + _$Ip149 + '\'' +
                    ", _$Port181=" + _$Port181 +
                    '}';
        }
    }

    public static class IceAddressBean {
        /**
         * endpoints : AiasIceGrid/Query
         * locator : AiasIceGrid/Locator:default -h 10.11.34.173 -p 4066
         */

        private String endpoints;
        private String locator;

        public String getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(String endpoints) {
            this.endpoints = endpoints;
        }

        public String getLocator() {
            return locator;
        }

        public void setLocator(String locator) {
            this.locator = locator;
        }

        @Override
        public String toString() {
            return "IceAddressBean{" +
                    "endpoints='" + endpoints + '\'' +
                    ", locator='" + locator + '\'' +
                    '}';
        }
    }

    public static class PushAddressBean {
        /**
         * privateIp : 192.168.1.1
         * privatePort : 1883
         * publicIp : 211.211.211.211
         * publicPort : 1883
         */

        private String privateIp;
        private String privatePort;
        private String publicIp;
        private String publicPort;

        public String getPrivateIp() {
            return privateIp;
        }

        public void setPrivateIp(String privateIp) {
            this.privateIp = privateIp;
        }

        public String getPrivatePort() {
            return privatePort;
        }

        public void setPrivatePort(String privatePort) {
            this.privatePort = privatePort;
        }

        public String getPublicIp() {
            return publicIp;
        }

        public void setPublicIp(String publicIp) {
            this.publicIp = publicIp;
        }

        public String getPublicPort() {
            return publicPort;
        }

        public void setPublicPort(String publicPort) {
            this.publicPort = publicPort;
        }

        @Override
        public String toString() {
            return "PushAddressBean{" +
                    "privateIp='" + privateIp + '\'' +
                    ", privatePort='" + privatePort + '\'' +
                    ", publicIp='" + publicIp + '\'' +
                    ", publicPort='" + publicPort + '\'' +
                    '}';
        }
    }
    public static class DefaultConfigBean {

        /**
         * pad : {"baseUrl":"","signature":""}
         */

        private PadBean pad;

        public PadBean getPad() {
            return pad;
        }

        public void setPad(PadBean pad) {
            this.pad = pad;
        }

        public static class PadBean {
            /**
             * baseUrl :
             * signature :
             */

            private String baseurl;
            private String signature;

            public String getBaseurl() {
                return baseurl;
            }

            public void setBaseurl(String baseurl) {
                this.baseurl = baseurl;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            @Override
            public String toString() {
                return "PadBean{" +
                        "baseurl='" + baseurl + '\'' +
                        ", signature='" + signature + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DefaultConfigBean{" +
                    "pad=" + pad +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "tenantCode='" + tenantCode + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", pushToken='" + pushToken + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", robotType='" + robotType + '\'' +
                ", vpnProfile='" + vpnProfile + '\'' +
                ", initAppConfig='" + initAppConfig + '\'' +
                ", hariAddress=" + hariAddress +
                ", iceAddress=" + iceAddress +
                ", pushAddress=" + pushAddress +
                ", defaultConfig=" + defaultConfig +
                '}';
    }
}
