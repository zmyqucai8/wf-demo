package com.example.testing.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.testing.demo.http.HttpCallBack;
import com.example.testing.demo.http.OkHttpUtils;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ExpandableListView listview;
    ProgressDialog dialog;
    LoadingUtlis utlis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ExpandableListView) findViewById(R.id.expandableListView);
        dialog = ProgressDialog.show(this, "", "加载中...");
        //子线程中去访问网络 以及操作数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                getXml();
            }
        }).start();
    }

    /**
     * http获取一二级列表
     */
    private void getXml() {
        String url = "http://192.168.0.12:8900/weboa/common/winfreeinfo.nsf/getdeptxml";
        OkHttpUtils.get(url, new HttpCallBack() {
            @Override
            public void onSucceed(Call call, final String s) throws IOException {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        List<String> xmlList = getXmlList(s);
                        setData(xmlList);
                    }
                }).start();
            }

            @Override
            public void onError(Call call, IOException e) {

            }
        });
    }


    /**
     * 解析xml数据
     *
     * @param xmlStr
     * @return
     */
    private List<String> getXmlList(String xmlStr) {
        List<String> list = new ArrayList<>();
        try {
            String xmlString = new String(xmlStr);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));

            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String nodeName = xmlPullParser.getName();
                        if ("NAMES".equals(nodeName)) {
                            Log.e("开始=", nodeName);
                        } else {
                            if ("DEPT".equals(nodeName)) {
                                String text = xmlPullParser.nextText();
                                list.add(text);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.e("结束=", xmlPullParser.getName());
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            Log.e("解析错误", "");
        }
        return list;
    }


    /**
     * 格式化数据并设置ui
     *
     * @param test
     */
    private void setData(List<String> test) {
        final List<GroupListBean> groupList = new ArrayList<>();
        List<ChildListBean> childList = new ArrayList<>();
        GroupListBean groupListBean = null;
        for (int i = 0; i < test.size(); i++) {
            String text = test.get(i);
            if (!text.contains("\\")) {
                groupListBean = new GroupListBean();
                groupListBean.setName(text);
                if (i == test.size() - 1) {
                    groupListBean.setChildListBean(childList);
                    groupList.add(groupListBean);
                    childList = new ArrayList<>();//重置,清空下child
                }
            } else {
                ChildListBean childListBean = new ChildListBean();
                childListBean.setName(text.substring(text.indexOf("\\") + 1, text.length()));
                childList.add(childListBean);
                groupListBean.setChildListBean(childList);
                try {
                    String nextS = test.get(i + 1);
                    String cuS = text.substring(0, text.indexOf("\\"));
                    if (!nextS.contains(cuS)) {
                        groupList.add(groupListBean);
                        childList = new ArrayList<>();//重置,清空下child
                    }
                } catch (Exception e) {
                    groupList.add(groupListBean);
                    childList = new ArrayList<>();//重置,清空下child
                }
            }
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //ui线程中设置listview
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listview.setAdapter(new MyExpandableListViewAdapter(groupList, MainActivity.this));
                listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra("TAG", groupList.get(groupPosition).getName() + "\\" + groupList.get(groupPosition).getChildListBean().get(childPosition).getName());
                        startActivity(intent);
                        return false;
                    }
                });
                dialog.dismiss();
//                utlis.hideLoading();
            }
        });
    }
}
