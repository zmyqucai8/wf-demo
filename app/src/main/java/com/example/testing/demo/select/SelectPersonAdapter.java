package com.example.testing.demo.select;

import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.testing.demo.App;
import com.example.testing.demo.CircleTransform;
import com.example.testing.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2017/4/21.
 * 选择列表的adapter
 */

public class SelectPersonAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    //数据对象list
    public List<String> list;
    //选中监听变量
    private OnItemCheckLitener mOnItemCheckLitener;
    //true= 单选， false =多选
    private boolean choiceModel = false;

    //设置选择模式 true= 单选， false =多选
    public void setChoiceModel(boolean model) {
        this.choiceModel = model;
    }

    /**
     * 设置item点击监听
     *
     * @param mOnItemCheckLitener item点击监听接口
     */
    public void setOnItemCheckLitener(OnItemCheckLitener mOnItemCheckLitener) {
        this.mOnItemCheckLitener = mOnItemCheckLitener;
    }

    //点击监听接口
    public interface OnItemCheckLitener {
        void onItemCheck(View view, int position, boolean isCheck);
    }

    //缓存已选中状态集合对象
    SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    /**
     * @param data        数据源
     * @param choiceModel 选择模式 true=单选
     */
    public SelectPersonAdapter(List<String> data, boolean choiceModel) {
        super(R.layout.select_item, data);
        this.list = data;
        this.choiceModel = choiceModel;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        final String name;
        if (item.contains("|")) {//如果包含|则取|前面的名字
            name = item.substring(0, item.indexOf("|"));
        } else {
            name = item;
        }
        helper.setVisible(R.id.title, true)
                .setText(R.id.title, name.substring(0, 1));//设置默认显示首字，防止出现快速滑动产生的问题
        helper.setText(R.id.name, name);
        String url = "http://192.168.0.12:8900/hrinfophoto/l/" + name + ".jpg";
        Glide.with(App.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .transform(new CircleTransform(App.getContext()))
                .into(new GlideDrawableImageViewTarget((ImageView) helper.getView(R.id.img)) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        //图片加载失败
                        helper.setVisible(R.id.img, false)
                                .setVisible(R.id.title, true)
                                .setText(R.id.title, name.substring(0, 1));
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        //图片加载成功
                        helper.setVisible(R.id.img, true)
                                .setVisible(R.id.title, false)
                                .setText(R.id.title, name.substring(0, 1));
                    }
                });

        //设置checkbox
        CheckBox checkBox = helper.getView(R.id.check);
        //状态
        checkBox.setChecked(isItemChecked(helper.getAdapterPosition()));
        //点击
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck(helper, v);
            }
        });
        //item的点击
        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck(helper, v);
            }
        });

    }

    /**
     * 设置点击选中
     *
     * @param helper
     * @param v
     */
    private void setCheck(BaseViewHolder helper, View v) {
        int adapterPosition = helper.getAdapterPosition();
        if (choiceModel) {
            //处理单选
            clearSelectedState();//清除已选中的item状态
            setItemChecked(adapterPosition, true);//设置当前item选中状态
        } else {//处理多选
            if (isItemChecked(adapterPosition)) {
                setItemChecked(adapterPosition, false);
            } else {
                setItemChecked(adapterPosition, true);
            }
        }
        //刷新数据
        notifyItemChanged(adapterPosition);
        //回调接口
        if (mOnItemCheckLitener != null) {
            mOnItemCheckLitener.onItemCheck(v, adapterPosition, isItemChecked(adapterPosition));
        }
    }

    /**
     * 设置是否选中
     *
     * @param position
     * @param isChecked
     */
    private void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    //根据位置判断条目是否选中
    public boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    //获得选中条目的结果
    public ArrayList<String> getSelectedItem() {
        ArrayList<String> selectList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(list.get(i));
            }
        }
        return selectList;
    }

    /**
     * 获取选中的items
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedPositions.size());
        for (int i = 0; i < mSelectedPositions.size(); i++) {
            items.add(mSelectedPositions.keyAt(i));
        }
        return items;
    }

    /**
     * 切换选中或取消选中
     */
    public void switchSelectedState(int position) {
        if (isItemChecked(position)) {
            setItemChecked(position, false);
        } else {
            setItemChecked(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * 清除所有选中item的标记
     */
    public void clearSelectedState() {
        List<Integer> selection = getSelectedItems();
        mSelectedPositions.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

}
