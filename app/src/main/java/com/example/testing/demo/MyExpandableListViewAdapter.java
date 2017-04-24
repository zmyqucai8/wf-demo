package com.example.testing.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yyyyyyy on 2017/4/19.
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {


    private  List<GroupListBean>list;
    private Context context;
    public MyExpandableListViewAdapter(List<GroupListBean> list,Context context){
        this.list=list;
        this.context=context;

    }


    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getChildListBean().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChildListBean().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        MyViewHolder holder;
        if(view == null){
            holder = new MyViewHolder();

            view =View.inflate(context,R.layout.group_item, null);
            holder.textView = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        }else {
            holder = (MyViewHolder) view.getTag();
        }
        holder.textView.setText(list.get(groupPosition).getName());
        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        MyViewHolder holder;
        if(view == null){
            holder = new MyViewHolder();
            //最好把false加上
            view =View.inflate(context,R.layout.child_item, null);
            holder.textView = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        }else {
            holder = (MyViewHolder) view.getTag();
        }
        holder.textView.setText(list.get(groupPosition).getChildListBean().get(childPosition).getName());
        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class MyViewHolder{
        TextView textView;
    }
}
