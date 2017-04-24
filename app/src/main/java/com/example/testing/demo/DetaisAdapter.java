package com.example.testing.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.List;

/**
 * Created by Yyyyyyy on 2017/4/19.
 */
public class DetaisAdapter extends BaseAdapter {


    private List<DetailsBean> list;
    private Context context;

    public DetaisAdapter(List<DetailsBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final MyViewHolder holder;
        if (view == null) {
            holder = new MyViewHolder();
            view = View.inflate(context, R.layout.details_item, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.email = (TextView) view.findViewById(R.id.email);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        final String n = list.get(position).N;
        holder.name.setText(n);
        holder.email.setText(list.get(position).C);

//
        String url = "http://192.168.0.12:8900/hrinfophoto/l/" + n + ".jpg";

        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade()
                .transform(new CircleTransform(context))
                .into(new GlideDrawableImageViewTarget(holder.img) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        holder.img.setVisibility(View.GONE);
                        holder.title.setVisibility(View.VISIBLE);
                        holder.title.setText(n.substring(0, 1));
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                    }
                });


        return view;

    }

    private class MyViewHolder {
        TextView name;
        TextView email;
        TextView title;
        ImageView img;
    }
}
