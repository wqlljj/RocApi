package com.cloudminds.rocapi.http;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudminds.rocapi.bean.ActivationBean;
import com.cloudminds.rocapi.bean.CustomX509TrustManager;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by cloud on 2019/5/14.
 */

public class HttpActivation extends AsyncTask<String,Void,String> {

    private static String TAG = "HttpActivation";
    ActivationBean activationBean;
    String url;
    RespCallBack<String> callBack;
    boolean isError=false;

    public HttpActivation(ActivationBean activationBean, String url, RespCallBack<String> callBack) {
        this.activationBean = activationBean;
        this.url = url;
        this.callBack = callBack;
    }

    // 方法1：onPreExecute（）
    // 作用：执行 线程任务前的操作
    // 注：根据需求复写
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute: ");
    }

    // 方法2：doInBackground（）
    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
    // 注：必须复写，从而自定义线程任务
    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "doInBackground: url = "+url);
        StringBuffer result = new StringBuffer();
        HttpURLConnection connection = null;
        try {
            connection = initHttps(url,"POST");
//            // 上传一张图片
//            FileInputStream file = new FileInputStream(Environment.getExternalStorageDirectory().getPath()
//                    + "/Pictures/Screenshots/Screenshot_2015-12-19-08-40-18.png");
//            OutputStream os = connection.getOutputStream();
//            int count = 0;
//            while((count=file.read()) != -1){
//                os.write(count);
//            }
//            os.flush();
//            os.close();

            // 获取返回数据
            DataOutputStream os = new DataOutputStream( connection.getOutputStream());
            String content = new Gson().toJson(activationBean);
            Log.i(TAG, "doInBackground: param "+content);
            os.writeBytes(content);
            os.flush();
            os.close();
            if(connection.getResponseCode() == 200){
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String temp = null;
                while ((temp = reader.readLine())!=null){
                    result.append(temp);
                }
                Log.i(TAG, "doInBackground: success "+result.toString());
                reader.close();
            }else{
                isError=true;
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String temp = null;
                while ((temp = reader.readLine())!=null){
                    result.append(temp);
                }
                Log.i(TAG, "doInBackground: error "+connection.getResponseCode()+"\n"+result.toString());
                reader.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
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

    // 方法4：onPostExecute（）
    // 作用：接收线程任务执行结果、将执行结果显示到UI组件
    // 注：必须复写，从而自定义UI操作
    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute: "+result);
         // UI操作
        if(!isError) {
            callBack.onResponse(result);
        }else{
            callBack.onFailure(result);
        }
    }

    // 方法5：onCancelled()
    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
        Log.i(TAG, "onCancelled: ");
    }


    /**
     2      * 初始化http请求参数
     3      */
      private static HttpsURLConnection initHttps(String url, String method)
              throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
          Log.i(TAG, "initHttps: "+url);
          TrustManager[] tm = {new CustomX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                 SSLSocketFactory ssf  = sslContext.getSocketFactory();
                 URL              _url = new URL(url);
                 HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
                 // 连接超时
                 http.setConnectTimeout(5000);
                 // 读取超时 --服务器响应比较慢，增大时间
                 http.setReadTimeout(5000);
                 http.setRequestMethod(method);
                 http.setRequestProperty("Content-Type", "application/json");
                 http.setRequestProperty("User-Agent",
                           "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
          HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
              @Override
              public boolean verify(String hostname, SSLSession session) {
                  return true;
              }
          };
                 http.setHostnameVerifier(DO_NOT_VERIFY);
                 http.setSSLSocketFactory(ssf);
                 http.setDoOutput(true);
                 http.setDoInput(true);
                 http.connect();
                 return http;
             }
    
}
