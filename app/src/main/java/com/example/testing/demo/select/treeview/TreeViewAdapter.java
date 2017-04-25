package com.example.testing.demo.select.treeview;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.testing.demo.R;

import java.util.ArrayList;

/**
 * TreeViewAdapter
 *
 * @author zmy
 */
public class TreeViewAdapter extends BaseAdapter {
    /**
     * 元素数据源
     */
    private ArrayList<Element> elementsData;
    /**
     * 树中元素
     */
    private ArrayList<Element> elements;
    /**
     * LayoutInflater
     */
    private LayoutInflater inflater;
    /**
     * item的行首缩进基数
     */
    private int indentionBase;

    public TreeViewAdapter(ArrayList<Element> elements, ArrayList<Element> elementsData, LayoutInflater inflater) {
        this.elements = elements;
        this.elementsData = elementsData;
        this.inflater = inflater;
        indentionBase = 50;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public ArrayList<Element> getElementsData() {
        return elementsData;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.treeview_item, null);
            holder.disclosureImg = (ImageView) convertView.findViewById(R.id.disclosureImg);
            holder.contentText = (TextView) convertView.findViewById(R.id.contentText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Element element = elements.get(position);
        int level = element.getLevel();
        holder.disclosureImg.setPadding(
                indentionBase * (level + 1),
                holder.disclosureImg.getPaddingTop(),
                holder.disclosureImg.getPaddingRight(),
                holder.disclosureImg.getPaddingBottom());
        holder.contentText.setText(element.getContentText());
        if (element.isHasChildren() && !element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.close);
            //这里要主动设置一下icon可见，因为convertView有可能是重用了"设置了不可见"的view，下同。
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (element.isHasChildren() && element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.open);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (!element.isHasChildren()) {
            holder.disclosureImg.setImageResource(R.drawable.close);
            holder.disclosureImg.setVisibility(View.INVISIBLE);
        }

        holder.disclosureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpanded(position);
            }
        });

        return convertView;
    }


    /**
     * 设置展开与关闭
     */
    public void setExpanded(int position){
//        Toast.makeText(APP.context,"展开",0).show();
        //展开按钮
        //点击的item代表的元素
        Element element = (Element) getItem(position);
        //树中的元素
        ArrayList<Element> elements = getElements();
        //元素的数据源
        ArrayList<Element> elementsData = getElementsData();

        //点击没有子项的item直接返回
        if (!element.isHasChildren()) {
            return;
        }

        if (element.isExpanded()) {
            element.setExpanded(false);
            //删除节点内部对应子节点数据，包括子节点的子节点...
            ArrayList<Element> elementsToDel = new ArrayList<Element>();
            for (int i = position + 1; i < elements.size(); i++) {
                if (element.getLevel() >= elements.get(i).getLevel())
                    break;
                elementsToDel.add(elements.get(i));
            }
            elements.removeAll(elementsToDel);
            notifyDataSetChanged();
        } else {
            element.setExpanded(true);
            //从数据源中提取子节点数据添加进树，注意这里只是添加了下一级子节点，为了简化逻辑
            int i = 1;//注意这里的计数器放在for外面才能保证计数有效
            for (Element e : elementsData) {
                if (e.getParendId() == element.getId()) {
                    e.setExpanded(false);
                    elements.add(position + i, e);
                    i++;
                }
            }
            notifyDataSetChanged();
        }
    }
    /**
     * 优化Holder
     *
     * @author carrey
     */
    static class ViewHolder {
        ImageView disclosureImg;
        TextView contentText;
    }
}