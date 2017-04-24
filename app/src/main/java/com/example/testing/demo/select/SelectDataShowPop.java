package com.example.testing.demo.select;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.testing.demo.App;
import com.example.testing.demo.R;
import com.example.testing.demo.upload.AmUtlis;

import java.util.List;

/**
 * @Created by zmy.
 * @Date 2017/3/2 0002.
 * 已选择数据显示的popwindow
 */
public class SelectDataShowPop {
    //adapter
    SelectDataShowAdapter mAdapter;

    //删除监听接口
    public interface onDeleteListener {
        void onDelete(String selectStr, int postion);
    }

    //接口变量
    public onDeleteListener mListener;
    //当前pop
    private PopupWindow mPop;
    //activtity
    private SelectActivity mAct;
    //只能显示一个的约束 默认可以显示
    private boolean isShow = true;
    //数据list
    private List<String> beanList;


    /**
     * 显示pop
     *
     * @param activity
     * @param list
     * @param listener
     */
    public void showPop(Activity activity, List<String> list, onDeleteListener listener) {
        mListener = listener;
        beanList = list;
        mAct = (SelectActivity) activity;
        if (isShow) {//是否可以显示
            if (mPop == null) {//判断是否为null
                initPop();
            } else {
                mAdapter.notifyDataSetChanged();
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
        View popView = View.inflate(mAct, R.layout.select_show_pop, null);
        RecyclerView recyclerView = (RecyclerView) popView.findViewById(R.id.recyclerView);
        mAdapter = new SelectDataShowAdapter(beanList);//设置adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));
        mAdapter.isFirstOnly(true);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.setEmptyView(AmUtlis.getEmptyView(mAct, "暂无数据"));
        mAdapter.setOnDeleteLisener(new onDeleteListener() {
            @Override
            public void onDelete(String selectStr, int postion) {
                //删除监听 以及刷新
                if (mListener != null) {
                    mListener.onDelete(selectStr, postion);
                }
                if (beanList.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mPop.dismiss();
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
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
                mAct.setSelectInfo();//pop消失的时候刷新下选择按钮的数据
            }
        });
    }
}
