package com.example.testing.demo.select;

import android.graphics.drawable.Drawable;
import android.view.View;
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

import java.util.List;

/**
 * Created by win7 on 2017/4/21.
 * 已选择的数据显示 adapter
 */
public class SelectDataShowAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    //删除接口变量
    public SelectDataShowPop.onDeleteListener listener;
    //删除监听接口
    public SelectDataShowAdapter(List<String> data) {
        super(R.layout.select_data_show_item, data);
    }
    //设置删除监听
    public void setOnDeleteLisener(SelectDataShowPop.onDeleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        final String name;
        if (item.contains("|")) {
            name = item.substring(0, item.indexOf("|"));
        } else {
            name = item;
        }
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
                        helper.setVisible(R.id.img, false)
                                .setVisible(R.id.title, true)
                                .setText(R.id.title, name.substring(0, 1));
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        helper.setVisible(R.id.img, true)
                                .setVisible(R.id.title, false);

                    }
                });

        helper.setOnClickListener(R.id.btn_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(item, helper.getAdapterPosition());
                }
            }
        });

    }
}
