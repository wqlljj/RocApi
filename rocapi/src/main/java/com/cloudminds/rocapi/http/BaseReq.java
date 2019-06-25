package com.cloudminds.rocapi.http;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudminds.rocapi.bean.CustomX509TrustManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
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
 * Created by cloud on 2019/5/21.
 */

public abstract class BaseReq<D,T,K> extends AsyncTask<D,T,K> {
    public String TAG;
    public String url;
    public String method = "GET";
    boolean isError=false;
    RespCallBack callBack;
    {
        TAG = this.getClass().getSimpleName();
    }

    public  BaseReq(String url, String method,RespCallBack callBack) {
        this.url = url;
        this.method = method;
        this.callBack = callBack;
    }

    /**
     2      * 初始化http请求参数
     3      */
    public  HttpURLConnection initReq()
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        Log.i(TAG, "initHttps: "+url);
        boolean isHttps = false;
        if(url.startsWith("https")) {
            URL _url = new URL(url);
            HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
            setReqHeader(http);
            TrustManager[] tm = {new CustomX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
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
            return http;
        }else{
            URL _url = new URL(url);
            HttpURLConnection http = (HttpURLConnection) _url.openConnection();
            setReqHeader(http);
            http.setDoOutput(true);
            http.setDoInput(true);
            return http;
        }
    }

    /**
     * setReqHeader 设置请求头
     * @param http
     * @throws ProtocolException
     */
    public void setReqHeader(HttpURLConnection http) throws ProtocolException {
        // 连接超时
        http.setConnectTimeout(5000);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(5000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/json");
        http.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
    }

    public void getStringFromResp(StringBuffer result, HttpURLConnection connection) throws IOException {
//        if(true){
//            return;
//        }
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
            result.append("错误码："+connection.getResponseCode());
            try {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    result.append(temp);
                }
                Log.i(TAG, "doInBackground: error " + connection.getResponseCode() + "\n" + result.toString());
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
