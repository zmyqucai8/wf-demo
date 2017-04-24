package com.example.testing.demo.http;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.testing.demo.App;
import com.example.testing.demo.LoginActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by win7 on 2017/4/20.
 */

public class OkHttpUtils {

    //    _%NOTAUTHORIZE%_ 没有权限
//    _%INCORRECTPASSWORD%_ 密码错误
//    _%EXPIRED%_会话超时
    public static String NOTAUTHORIZE = "_%NOTAUTHORIZE%_";
    public static String INCORRECTPASSWORD = "_%INCORRECTPASSWORD%_";
    public static String EXPIRED = "_%EXPIRED%_";
    /**
     * 文本类型的type
     */
    public static String TEXT_TYPE = "text";

    private static OkHttpClient mOkHttpClient;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 单例模式获取OkHttpClient
     *
     * @return
     */
    public synchronized static OkHttpClient getInstance() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.cookieJar(new CookiesManager());
            builder.addInterceptor(new LogInterceptor());
            return mOkHttpClient = builder.build();
        } else {
            return mOkHttpClient;
        }


    }


    /**
     * 封装的普通get请求
     *
     * @param url
     * @param callBack
     */
    public static void get(String url, final HttpCallBack callBack) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call mcall = getInstance().newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onError(call, e);
                    }
                });

            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                String body =  response.body().string();
//                boolean is = false;
//                Log.e("返回的数据类型=", response.body().contentType().toString());
//                if (response.body().contentType().toString().contains(TEXT_TYPE)) {
//                    //如果是文本
//                    body = response.body().string();
//                    //    _%NOTAUTHORIZE%_ 没有权限
////    _%INCORRECTPASSWORD%_ 密码错误
////    _%EXPIRED%_会话超时
//                    if (body.contains(NOTAUTHORIZE)) {
//                        is = true;
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(App.getContext(), "没有权限查看", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else if (body.contains(INCORRECTPASSWORD)) {
//                        is = true;
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(App.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else if (body.contains(EXPIRED)) {
//                        is = true;
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(App.getContext(), "会话超时，请重新登录", Toast.LENGTH_SHORT).show();
//                                App.getContext().startActivity(new Intent(App.getContext(), LoginActivity.class));
//
//                            }
//                        });
//                    }
//
//                    if (is)
//                        return;
//
//                }

                final String finalBody = body;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callBack.onSucceed(call, finalBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


}
