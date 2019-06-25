package com.cloudminds.rocapi.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloud on 2019/5/14.
 */

public class ActivationBean {
    String userCode;
    String userPwd;
    String rcuId;
    String robotId;
    String robotType="rcu_pad";
    List<Module> modules=new ArrayList<>();
    private int type;

    public ActivationBean(String userCode, String userPwd, String rcuId, String robotId, List<Module> modules) {
        this.userCode = userCode;
        this.userPwd = userPwd;
        this.rcuId = rcuId;
        this.robotId = robotId;
        this.modules = modules;
    }

    public ActivationBean(String rcuId, String robotId, List<Module> modules, int type) {
        this.rcuId = rcuId;
        this.robotId = robotId;
        this.modules = modules;
        this.type = type;
    }

    public String getRcuId() {
        return rcuId;
    }

    public void setRcuId(String rcuId) {
        this.rcuId = rcuId;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getRobotType() {
        return robotType;
    }

    public void setRobotType(String robotType) {
        this.robotType = robotType;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }


    public static class Module{

        /**
         *  moduleId : xxxxx
         *  moduleType  :  camera
         */

        private String moduleId;
        private String moduleType;

        public Module(String moduleId) {
            this.moduleId = moduleId;
            this.moduleType = "camera";
        }

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public String getModuleType() {
            return moduleType;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        @Override
        public String toString() {
            return "moudle{" +
                    "moduleId='" + moduleId + '\'' +
                    ", moduleType='" + moduleType + '\'' +
                    '}';
        }
    }
}
