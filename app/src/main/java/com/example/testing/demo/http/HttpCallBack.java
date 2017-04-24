package com.example.testing.demo.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by win7 on 2017/4/20.
 */

public interface  HttpCallBack  {

    void onSucceed(Call call, String str)throws IOException;

    void  onError(Call call, IOException e);
}
