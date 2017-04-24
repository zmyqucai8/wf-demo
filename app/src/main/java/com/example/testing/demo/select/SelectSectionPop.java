package com.example.testing.demo.select;

import android.app.Activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.testing.demo.R;
import com.example.testing.demo.treeview.Element;
import com.example.testing.demo.treeview.TreeViewAdapter;
import com.example.testing.demo.upload.AmUtlis;

import java.util.ArrayList;


/**
 * @Created by zmy.
 * @Date 2017/3/2 0002.
 * 公司及部门选择的pop ， 是一个二级展开列表
 */
public class SelectSectionPop {
    //选中监听接口
    public interface onSelectListener {
        void onSelect(String selectStr);
    }

    //接口变量
    public onSelectListener mListener;

    //当前实例对象
    public static SelectSectionPop mSelectSectionPop;
    //pop
    private PopupWindow mPop;
    //act
    private Activity mAct;
    //只能显示一个的约束 默认可以显示
    private boolean isShow = true;
    //所有数据list
    private ArrayList<Element> mElementsData;
    //顶级父类list
    private ArrayList<Element> mElements;


    /**
     * 获取实例
     *
     * @return
     */
    public static synchronized SelectSectionPop getInstance() {
        if (mSelectSectionPop == null) {
            return mSelectSectionPop = new SelectSectionPop();
        } else {
            return mSelectSectionPop;
        }
    }


    /**
     * @param activity
     */
    public void showPop(Activity activity, ArrayList<Element> elements, ArrayList<Element> elementsData, onSelectListener listener) {
        mListener = listener;
        mElementsData = elementsData;
        mElements = elements;
        mAct = activity;
        if (isShow) {
            if (mPop == null) {//为null才去加载
                initPop();
            }
            AmUtlis.showDarkScreen(activity);
            mPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            isShow = false;
        }
    }

    /**
     * 初始化pop
     */
    private void initPop() {
        View popView = View.inflate(mAct, R.layout.section_pop, null);
        ListView treeview = (ListView) popView.findViewById(R.id.treeview);
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TreeViewAdapter treeViewAdapter = new TreeViewAdapter(mElements, mElementsData, inflater);
//        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
        treeview.setAdapter(treeViewAdapter);
        treeViewAdapter.setExpanded(0);
        treeview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Element item = (Element) treeViewAdapter.getItem(position);
                if (mListener != null) {
                    mListener.onSelect(item.getAllName());
                }
                mPop.dismiss();
            }
        });
        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPop.setContentView(popView);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setTouchable(true);
        mPop.setFocusable(true);
        mPop.setAnimationStyle(R.style.ScaleAnimation);
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AmUtlis.hideDarkScreen(mAct);
                isShow = true;
            }
        });
    }
}
