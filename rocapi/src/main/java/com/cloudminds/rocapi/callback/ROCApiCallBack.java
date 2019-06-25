package com.cloudminds.rocapi.callback;

/**
 * Created by cloud on 2019/1/8.
 */

public interface ROCApiCallBack {
    void initFinish();
    void onConnectSuccess();
    void onConnectFail(String msg);
    void onConnectLost();
    void onClose();
    void message(String msg);
    void parseFail(String message,Exception e);
}
