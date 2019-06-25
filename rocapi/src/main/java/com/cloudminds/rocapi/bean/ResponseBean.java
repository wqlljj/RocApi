package com.cloudminds.rocapi.bean;

/**
 * Created by cloud on 2019/5/22.
 */

public class ResponseBean {
    int code;
    String messages;

    public ResponseBean() {
    }

    public ResponseBean(int code, String messages) {
        this.code = code;
        this.messages = messages;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code=" + code +
                ", messages='" + messages + '\'' +
                '}';
    }
}
