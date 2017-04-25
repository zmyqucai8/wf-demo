package com.example.testing.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testing.demo.http.HttpCallBack;
import com.example.testing.demo.http.OkHttpUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yyyyyyy on 2017/4/19.
 * 子部门下的人员详细列表
 */
public class DetailsActivity extends Activity {

    /**
     * 部门跳转标记
     */
    String tag;
    private ListView listview;
    private TextView tv_title;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        dialog = ProgressDialog.show(this, "", "加载中...");
        listview = (ListView) findViewById(R.id.listview);
        tv_title = (TextView) findViewById(R.id.title);
        tag = getIntent().getStringExtra("TAG");
        tv_title.setText(tag);
        new Thread(new Runnable() {
            @Override
            public void run() {
                okHttpGetXML();
            }
        }).start();

    }

    /**
     * 请求xml数据, 并解析
     */
    private void okHttpGetXML() {
        String url = "http://192.168.0.12:8900/weboa/common/winfreeinfo.nsf/getpersonxml";
        OkGo.get(url)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        List<DetailsBean> xmlList = getXmlList(s);
                        setData(xmlList);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }
                });

    }

    /**
     * 在主线程中国设置list数据
     *
     * @param xmlList
     */
    private void setData(final List<DetailsBean> xmlList) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listview.setAdapter(new DetaisAdapter(xmlList, DetailsActivity.this));
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(DetailsActivity.this, xmlList.get(position).N, Toast.LENGTH_SHORT).show();
                    }
                });

                if (xmlList.size() < 1) {
                    TextView view = new TextView(DetailsActivity.this);
                    view.setPadding(100, 100, 100, 100);
                    view.setGravity(Gravity.CENTER);
                    view.setText("没有数据");
                    listview.addFooterView(view);
                }
                dialog.dismiss();
            }
        });
    }


    /**
     * 解析数据
     *
     * @param xmlStr
     * @return
     */
    private List<DetailsBean> getXmlList(String xmlStr) {
        List<DetailsBean> list = new ArrayList<>();
        DetailsBean bean = null;
        String xmlString = new String(xmlStr);
        Log.e("数据=", xmlString);
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        XmlPullParser xmlPullParser = null;
        try {
            xmlPullParser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            xmlPullParser.setInput(new StringReader(xmlString));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        int eventType = 0;
        try {
            eventType = xmlPullParser.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        boolean isAdd = false;//是否可以添加的标记
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String nodeName = xmlPullParser.getName();
                    if ("NAMES".equals(nodeName)) {
                        Log.e("开始=", nodeName);
                    } else {
                        try {
                            Log.e("开始标签=", xmlPullParser.getName());
                            if ("DEPT".equals(nodeName)) {
                                String name = xmlPullParser.getAttributeValue(0);
                                Log.e("DEPT=name=", name);
                                if (tag.equals(name)) {
                                    isAdd = true;
                                }
                                break;
                            }

                            if ("P".equals(nodeName)) {
                                bean = new DetailsBean();
                                break;
                            }
                            if ("N".equals(nodeName) && bean != null) {
                                bean.N = xmlPullParser.nextText();
                                break;
                            }
                            if ("C".equals(nodeName) && bean != null) {
                                bean.C = xmlPullParser.nextText();
                                break;
                            }

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    Log.e("结束=", xmlPullParser.getName());
                    if ("P".equals(xmlPullParser.getName()) && bean != null && isAdd) {
                        list.add(bean);
                    }
                    if ("DEPT".equals(xmlPullParser.getName())) {
                        isAdd = false;
                    }
                    break;
            }
            try {
                eventType = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
