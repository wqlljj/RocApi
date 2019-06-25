package com.cloudminds.rocapi.http;

import android.util.Log;

import com.cloudminds.rocapi.bean.ResponseBean;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;

/**
 * Created by cloud on 2019/5/21.
 */

public class ReqOffline extends BaseReq<String,Void,String> {

    public ReqOffline(String url, RespCallBack<String> callBack) {
        super(url, "POST",callBack);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "doInBackground: url = "+url);
//        url+="?robotId="+params[0];
//        url+="&rcuId="+params[1];
//        url+="&userCode="+params[2];
//        url+="&userPwd="+params[3];
//        url+="&lang="+params[4];
        StringBuffer result = new StringBuffer();
        HttpURLConnection connection = null;
        try {
            connection = initReq();
            connection.connect();
            DataOutputStream os = new DataOutputStream( connection.getOutputStream());
//            String content = new Gson().toJson(activationBean);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("robotId",params[0]);
            jsonObject.put("rcuId",params[1]);
            jsonObject.put("userCode",params[2]);
            jsonObject.put("userPwd",params[3]);
            jsonObject.put("lang",params[4]);
            Log.i(TAG, "doInBackground: param = "+jsonObject.toString());
            os.writeBytes(jsonObject.toString());
            os.flush();
            os.close();
            getStringFromResp(result, connection);
        } catch (Exception e) {
            e.printStackTrace();
            isError=true;
            result.append(e.getMessage());
        } finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(TAG, "onPostExecute: "+result);
        // UI操作
        if(!isError) {
            ResponseBean responseBean = new Gson().fromJson(result, ResponseBean.class);
            if(responseBean.getCode()==0) {
                callBack.onResponse(result);
            }else{
                callBack.onFailure(responseBean.getCode()+":"+responseBean.getMessages());
            }
        }else{
            callBack.onFailure(result);
        }
    }
}
