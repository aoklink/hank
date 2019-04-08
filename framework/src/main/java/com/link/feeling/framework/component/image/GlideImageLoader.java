package com.link.feeling.framework.component.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.link.feeling.framework.R;

/**
 * Created on 2019/1/21  14:17
 * chenpan pan.chen@linkfeeling.cn
 */
public final class GlideImageLoader implements LinkImageLoader {

    @Override
    public void load(String imgUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .error(R.drawable.round_placeholder)
                .placeholder(R.drawable.round_placeholder)
                .into(imageView);
    }

    @Override
    public void load(int imgUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .error(R.drawable.round_placeholder)
                .placeholder(R.drawable.round_placeholder)
                .into(imageView);
    }

    @Override
    public void load(String imgUrl, ImageView imageView, int placeholder) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .error(placeholder)
                .placeholder(placeholder)
                .into(imageView);
    }

    @Override
    public void load(String imgUrl, ImageView imageView, Transformation<Bitmap>... transformations) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .bitmapTransform(transformations)
                .error(R.drawable.round_placeholder)
                .placeholder(R.drawable.round_placeholder)
                .into(imageView);
    }
}
