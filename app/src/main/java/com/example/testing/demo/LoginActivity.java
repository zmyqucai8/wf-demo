package com.example.testing.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.testing.demo.http.OkHttpUtils;
import com.example.testing.demo.http.UploadActivity;
import com.example.testing.demo.select.SelectActivity;
import com.example.testing.demo.upload.AmUtlis;


import java.io.IOException;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Yyyyyyy on 2017/4/19.
 */
public class LoginActivity extends Activity {
    EditText ed_name;
    EditText ed_pwd;
    RadioButton radio1;
    RadioButton radio2;
    RadioGroup group;
    TextView tv_restut;
    //test submit
    boolean select = false;//默认ture 多选

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btn_login = (Button) findViewById(R.id.login);
        ed_pwd = (EditText) findViewById(R.id.pwd);
        tv_restut = (TextView) findViewById(R.id.tv_restut);
        ed_name = (EditText) findViewById(R.id.name);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio1);
        group = (RadioGroup) findViewById(R.id.group);

        ed_name.setText("admin");
        ed_pwd.setText("a");

        ed_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    AmUtlis.showToast("搜索");

                }
                return false;
            }
        });

        //点击登录
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginHttp(0);
            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                select = rb.getId() == R.id.radio1;
            }
        });
    }


    /**
     * 测试上传图片
     *
     * @param v
     */
    public void upload(View v) {

        loginHttp(1);

    }


    /**
     * 登录请求
     * //0=登录 1=上传测试 2=选择列表
     */
    private void loginHttp(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://192.168.0.12:8900/names.nsf?Login";
                RequestBody formBody = new FormBody.Builder()
                        .add("UserName", ed_name.getText().toString().trim())
                        .add("Password", ed_pwd.getText().toString().trim())
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = OkHttpUtils.getInstance().newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                if (type == 0) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) {
                                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                    }
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else if (type == 1) {
                                    startActivity(new Intent(LoginActivity.this, UploadActivity.class));
                                } else if (type == 2) {
                                    Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                                    intent.putExtra("TAG", select);
                                    startActivityForResult(intent, 1);
                                }
                            }
                        });
                    }

                });


            }
        }).start();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> restut = data.getStringArrayListExtra("restut");
            StringBuilder b = new StringBuilder();
            b.append("选择的数据如下\n");
            for (int x = 0; x < restut.size(); x++) {
                String s = restut.get(x);
                String s1 = AmUtlis.containsStr("|", s);
                if (!s.equals(s1)) {
                    restut.remove(x);
                    restut.add(x, s1);
                    b.append(s1 + "\n");
                } else {
                    b.append(s + "\n");
                }

            }

            tv_restut.setText(b.toString());
        } else {
            //
        }
    }

    /**
     * 选择抄送对象
     *
     * @param view
     */
    public void select(View view) {
        loginHttp(2);
    }


}
