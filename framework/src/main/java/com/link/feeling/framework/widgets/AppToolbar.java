package com.link.feeling.framework.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.link.feeling.framework.R;
import com.link.feeling.framework.R2;
import com.link.feeling.framework.utils.data.DisplayUtils;
import com.link.feeling.framework.utils.ui.ViewUtils;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created on 2019/1/14  19:50
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class AppToolbar extends RelativeLayout {

    @BindView(R2.id.btn_back)
    TextView mBtnBack;
    @BindView(R2.id.btn_menu)
    TextView mBtnMenu;
    @Nullable
    TextView mTextTitle;
    @BindDimen(R2.dimen.framework_normal_padding)
    int mNormalPadding;

    @Nullable
    private View mTitleView;
    private OnClickListener mTitleOnclickListener;
    private Consumer<View> mTitleOnDoubleTapListener;
    private GestureDetector mGestureDetector;
    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

    };

    public AppToolbar(Context context) {
        this(context, null);
    }

    public AppToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.FrameworkToolbarStyle);
    }

    public AppToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        inflate(context, R.layout.framework_toolbar, this);
        ButterKnife.bind(this);
        if (findViewById(R.id.text_title) instanceof TextView) {
            mTextTitle = findViewById(R.id.text_title);
        }

        ViewUtils.expandViewTouchDelegate(mBtnMenu, mNormalPadding);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppToolbar, defStyle, 0);
        showBack(typedArray.getBoolean(R.styleable.AppToolbar_atb_show_back, true));
        if (typedArray.hasValue(R.styleable.AppToolbar_atb_backSrc)) {
            setBackSrc(typedArray.getDrawable(R.styleable.AppToolbar_atb_backSrc));
        }
        showTitle(typedArray.getBoolean(R.styleable.AppToolbar_atb_showTitle, true));
        setTitleText(typedArray.getText(R.styleable.AppToolbar_atb_title));
        if (typedArray.hasValue(R.styleable.AppToolbar_atb_title_left_drawable)) {
            setTitleLeftDrawable(typedArray.getDrawable(R.styleable.AppToolbar_atb_title_left_drawable));
        }
        showMenu(typedArray.getBoolean(R.styleable.AppToolbar_atb_showMenu, false));
        setMenuText(typedArray.getText(R.styleable.AppToolbar_atb_menuTitle));
        if (typedArray.hasValue(R.styleable.AppToolbar_atb_menuSrc)) {
            setMenuDrawable(typedArray.getDrawable(R.styleable.AppToolbar_atb_menuSrc));
        }
        if (typedArray.hasValue(R.styleable.AppToolbar_atb_titleColor)) {
            if (mTextTitle != null) {
                mTextTitle.setTextColor(typedArray.getColor(R.styleable.AppToolbar_atb_titleColor, Color.BLACK));
            }
        }
        typedArray.recycle();

        mBtnBack.setOnClickListener(v -> {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        });

        setPadding(
                mNormalPadding,
                DisplayUtils.getStatusBarHeight((Activity) getContext()),
                mNormalPadding,
                0
        );

        if (mTextTitle != null) {
            mTextTitle.setPadding(
                    0,
                    mNormalPadding,
                    0,
                    mNormalPadding
            );
        }
    }

    /**
     * 是否显示返回按钮
     *
     * @param show 是否显示
     */
    public void showBack(boolean show) {
        mBtnBack.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 设置返回按钮 Drawable
     *
     * @param drawable drawable
     */
    public void setBackSrc(@Nullable Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(
                    0,
                    0,
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight()
            );
        }
        mBtnBack.setCompoundDrawables(
                null,
                null,
                drawable,
                null
        );
    }

    /**
     * 显示标题
     *
     * @param show 是否显示标题
     */
    public void showTitle(boolean show) {
        if (mTextTitle == null) {
            return;
        }
        mTextTitle.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 设置标题
     *
     * @param titleRes 标题
     */
    public void setTitleText(@StringRes int titleRes) {
        setTitleText(getResources().getString(titleRes));
    }

    /**
     * 设置标题
     *
     * @param character 标题
     */
    public void setTitleText(CharSequence character) {
        if (mTextTitle == null) {
            return;
        }
        if (!TextUtils.isEmpty(character)) {
            mTextTitle.setText(character);
        } else {
            mTextTitle.setText("");
        }
    }

    /**
     * 设置标题文字的字体
     *
     * @param typeface 字体
     */
    public void setTitleTypeface(Typeface typeface) {
        if (mTextTitle == null) {
            return;
        }
        mTextTitle.setTypeface(typeface);
    }

    /**
     * 设置默认标题的点击事件
     *
     * @param onClickTitle 点击事件
     */
    public void setOnClickTitle(@NonNull OnClickListener onClickTitle) {
        mTitleOnclickListener = onClickTitle;
        if (mTextTitle != null) {
            mTextTitle.setOnClickListener(onClickTitle);
        }
        if (mTitleView != null) {
            mTitleView.setOnClickListener(onClickTitle);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setTitleOnDoubleTapListener(Consumer<View> titleOnDoubleTapListener) {
        mTitleOnDoubleTapListener = titleOnDoubleTapListener;
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);
        }
        if (mTextTitle != null) {
            mTextTitle.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));
        }
    }

    /**
     * 设置标题左边 Drawable
     *
     * @param drawable drawable
     */
    public void setTitleLeftDrawable(Drawable drawable) {
        if (mTextTitle == null) {
            return;
        }
        if (drawable == null) {
            return;
        }
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight()
        );
        mTextTitle.setCompoundDrawables(
                drawable,
                null,
                null,
                null
        );
    }

    /**
     * 显示右边菜单
     *
     * @param show 是否显示
     */
    public void showMenu(boolean show) {
        mBtnMenu.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * 设置右边菜单栏文字
     *
     * @param charSequence 菜单文字
     */
    public void setMenuText(CharSequence charSequence) {
        if (charSequence != null) {
            mBtnMenu.setText(charSequence);
        } else {
            mBtnMenu.setText("");
        }
    }

    /**
     * 设置菜单 drawable
     *
     * @param drawable drawable
     */
    public void setMenuDrawable(@Nullable Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        mBtnMenu.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 设置菜单 drawable
     *
     * @param drawable drawable
     */
    public void setMenuDrawable(@DrawableRes int drawable) {
        setMenuDrawable(
                ResourcesCompat.getDrawable(getResources(), drawable, getContext().getTheme())
        );
    }

    /**
     * 设置菜单点击时间
     *
     * @param onClickMenu 点击事件
     */
    public void setOnClickMenu(@Nullable OnClickListener onClickMenu) {
        mBtnMenu.setOnClickListener(onClickMenu);
    }

    /**
     * 设置标题文字的视图
     *
     * @param childView 视图
     */
    public void setTitleView(View childView) {
        if (mTextTitle != null && mTextTitle.getParent() instanceof ViewGroup) {
            ((ViewGroup) mTextTitle.getParent()).removeView(
                    mTextTitle
            );
        }

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(childView, params);
        mTitleView = childView;
        if (mTitleOnclickListener != null) {
            mTitleView.setOnClickListener(mTitleOnclickListener);
        }
    }

}

