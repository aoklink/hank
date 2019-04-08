package com.link.feeling.framework.component.image;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.load.Transformation;

/**
 * Created on 2019/1/21  14:17
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public interface LinkImageLoader {

    /**
     * 加载图片 url
     *
     * @param imgUrl    图片地址
     * @param imageView view
     */
    void load(String imgUrl, ImageView imageView);


    /**
     * 加载图片 url
     *
     * @param imgUrl    图片地址
     * @param imageView view
     */
    void load(@DrawableRes int imgUrl, ImageView imageView);


    /**
     * 加载图片 url，使用指定的占位图
     *
     * @param imgUrl      图片地址
     * @param imageView   view
     * @param placeholder 占位图
     */
    void load(String imgUrl, ImageView imageView, @DrawableRes int placeholder);

    /**
     * 加载图片 url，使用指定的占位图，指定
     *
     * @param imgUrl          图片地址
     * @param imageView       view
     * @param transformations 占位图
     */
    void load(String imgUrl, ImageView imageView, Transformation<Bitmap>... transformations);

    GlideImageLoader INSTANCE = new GlideImageLoader();

}
