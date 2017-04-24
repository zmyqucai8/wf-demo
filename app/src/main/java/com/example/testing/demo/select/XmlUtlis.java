package com.example.testing.demo.select;

import android.text.TextUtils;
import android.util.Log;


import com.example.testing.demo.ChildListBean;
import com.example.testing.demo.GroupListBean;
import com.example.testing.demo.treeview.Element;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by win7 on 2017/4/21.
 * <p>
 * 自定义XML解析类
 */

public class XmlUtlis {

    /**
     * 顶级父类list
     */
    public static ArrayList<Element> elements;

    /**
     * 解析xml数据
     * xmlStr= 公司和部门12级列表
     *
     * @param xmlStr
     * @return List<GroupListBean> = 二级列表的格式化数据
     */
    public static List<GroupListBean> getXmlGroupList(String xmlStr) {
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
        final List<GroupListBean> groupList = new ArrayList<>();
        List<ChildListBean> childList = new ArrayList<>();
        GroupListBean groupListBean = null;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (!text.contains("\\")) {
                groupListBean = new GroupListBean();
                groupListBean.setName(text);
                if (i == list.size() - 1) {
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
                    String nextS = list.get(i + 1);
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
        return groupList;
    }


    /**
     * 解析部门数据
     *
     * @param xmlStr
     * @return
     */
    public static List<String> getXmlList(String xmlStr) {
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
            Log.e("解析错误1", "");
        }
        return list;
    }


    /**
     * 解析部门数据， 并且格式化数据返回
     *
     * @param str
     */
    public static ArrayList<Element> getXmlSectionList(String str) {
        List<String> list = getXmlList(str);
        elements = new ArrayList<Element>();
        ArrayList<Element> elementsData = new ArrayList<Element>();
        FormatCompanyBean formatCompanyBean = null;
        List<FormatCompanyBean> listBean = new ArrayList<>();
        List<FormatCompanyBean> newListBean = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i);//全部数据
            int num = 0;
            String f = "\\";
            char[] chars = name.toCharArray();
            Map<Integer, Integer> hashMap = new HashMap<>();
            for (int x = 0; x < chars.length; x++) {
                if (f.equals(String.valueOf(chars[x]))) {
                    hashMap.put(num, x);
                    num++;
                }
            }
//            Log.e("斜杠出现的次数=", num + "次");
            formatCompanyBean = new FormatCompanyBean();
            formatCompanyBean.level = num;//层级
            formatCompanyBean.allName = name;//全称
            formatCompanyBean.name = name;//获取简称  :防止一级目录没有简称的问题
            for (int y = 0; y < hashMap.size(); y++) {
                if (num == 0) {
                    formatCompanyBean.name = name;//获取简称
                    formatCompanyBean.topAllName = name;//获取上级部门全称
                } else {
                    formatCompanyBean.name = name.substring(hashMap.get(y) + 1, name.length());//获取简称
                    formatCompanyBean.topAllName = name.replace("\\" + formatCompanyBean.name, "");//获取上级部门全称
                    if (num == 1) {//判断斜杠次数 如果1直接获取前面的name
                        formatCompanyBean.topName = name.substring(0, name.indexOf("\\"));//获取上级部门简称
                    } else {
                        if (y != 0)
                            formatCompanyBean.topName = name.substring(hashMap.get(y - 1) + 1, hashMap.get(y));//获取上级部门简称
                    }
                }
            }
            formatCompanyBean.mId = i;
            listBean.add(formatCompanyBean);
        }
        //再次循环 添加上级部门的简称 和全称 以及id
        for (int x = 0; x < listBean.size(); x++) {
            FormatCompanyBean mFormatCompanyBean = listBean.get(x);//我的数据
            String mTopName = mFormatCompanyBean.topAllName;//我的上级部门简称
            if (!TextUtils.isEmpty(mTopName)) {
                for (int y = 0; y < listBean.size(); y++) {
                    if (mTopName.equals(listBean.get(y).allName)) {
                        mFormatCompanyBean.pId = listBean.get(y).mId;
                        if (!newListBean.contains(mFormatCompanyBean)) {
                            newListBean.add(mFormatCompanyBean);
                        }
                    }
                }
            } else {
                mFormatCompanyBean.pId = Element.NO_PARENT;
                if (!newListBean.contains(mFormatCompanyBean)) {
                    newListBean.add(mFormatCompanyBean);
                }
            }
        }
        for (int c = 0; c < newListBean.size(); c++) {
            //处理是否有子类
            FormatCompanyBean bean = newListBean.get(c);
            boolean is = false;
            for (int d = 0; d < newListBean.size(); d++) {
                if (bean.mId == newListBean.get(d).pId) {
                    is = true;
                    break;
                }
            }
            Element e1 = new Element(bean.name, bean.allName, bean.level, bean.mId, bean.pId, is, false);
            if (bean.pId == Element.NO_PARENT) {
                elements.add(e1);
            }
            elementsData.add(e1);
        }
        for (Element b : elements) {
            Log.e("TAG", b.toString());
        }
        Log.e("*********", "****************************************");
        //对最终数据 进行排序 , 有子类的放前面
//        elementsData = px(elementsData);
        for (Element b : elementsData) {
            Log.e("所有数据", b.toString());
        }
        return elementsData;
    }

    //对数据进行排序
    public static ArrayList<Element> px(ArrayList<Element> elements) {
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element lhs, Element rhs) {
                if (rhs.isHasChildren()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return elements;
    }
}
