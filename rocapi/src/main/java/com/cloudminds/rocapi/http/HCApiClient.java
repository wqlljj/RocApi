package com.cloudminds.rocapi.http;

import android.util.Log;

import com.cloudminds.rocapi.bean.ActivationBean;
import com.cloudminds.rocapi.constant.Constants;
import com.cloudminds.rocapi.util.SharePreferenceUtils;

import java.util.List;


/**
 * Created by SX on 2017/4/10.
 */

public class HCApiClient {
    private static String TAG="HCApiClient";
    public static  void activation(String robotId, String rcuId, List<ActivationBean.Module> modules,final RespCallBack<String> callBack){
        HttpActivation activation = new HttpActivation(new ActivationBean(rcuId,robotId,modules,0), Constants.ROCURL+"/roc/V1/rcu/activation",callBack);
        activation.execute();
    }

    public static  void offline(String robotId, String rcuId, String userCode,String userPwd,String lang,final RespCallBack<String> callBack){
        String url = Constants.ROCURL+"/roc/V1/rcu/logout";
//        String url = "http://10.11.35.178:8012/roc/V1/rcu/logout";
        ReqOffline reqLogout = new ReqOffline(url, callBack);
        reqLogout.execute(robotId,rcuId,userCode,userPwd,lang);
    }

    public static void init(){
        Constants.ROCIP= getRocHost();
        Constants.ROCURL="https://"+ Constants.ROCIP;
    }
    public static String getRocHost(){
        return SharePreferenceUtils.getPrefString("ROCIP",Constants.ROCIP);
//        return (sharedPreferences.getString("ROCIP",Constants.ROCIP));
    }
    public static void setRocHost(String http){
        Log.e(TAG, "setServiceHost: "+http );
        Constants.ROCIP =http;
        SharePreferenceUtils.setPrefString("ROCIP",Constants.ROCIP);
//        sharedPreferences.edit().putString("ROCIP",Constants.ROCIP).apply();
        Constants.ROCURL="https://"+Constants.ROCIP;
    }
}
