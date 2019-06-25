package com.cloudminds.rocapi.http;

/**
 * Created by cloud on 2019/5/14.
 */

public interface RespCallBack<S>{
    void onResponse(S data);
    void onFailure(String msg);
}
