package com.link.feeling.framework.utils.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.link.feeling.framework.KeysConstants;
import com.link.feeling.framework.base.BaseApplication;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

/**
 * Created on 2019/1/9  11:06
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class DisplayUtils {

    private DisplayUtils() {
        throw new UnsupportedOperationException("工具类不能调用构造函数");
    }

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    static {
        sContext = BaseApplication.getAppContext();
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                sContext.getResources().getDisplayMetrics());
    }

    public static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                sContext.getResources().getDisplayMetrics());
    }

    @SuppressWarnings("WeakerAccess")
    public static int px2dp(float px) {
        return Math.round(px / sContext.getResources().getDisplayMetrics().density);
    }

    public static int getScreenHeight() {
        return sContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return sContext.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 设置状态栏透明
     *
     * @param activity activity
     */
    @SuppressWarnings("WeakerAccess")
    public static void translucentStatusBar(@NonNull Activity activity) {

        if (activity.getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                activity.getWindow()
                        .addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                final int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
                activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility
                        | SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 是否状态栏透明
     *
     * @param activity activity
     * @return true 则设置了状态栏透明
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isTranslucentStatusBar(@NonNull Activity activity) {

        if (activity.getWindow() != null) {
            final int systemUIVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if ((systemUIVisibility & SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
                return true;
            }

            final int flags = activity.getWindow().getAttributes().flags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
        }

        return false;

    }

    /**
     * 状态栏深色字体
     *
     * @param activity activity
     */
    public static void statusBarDarkFont(@NonNull Activity activity) {
        if (activity.getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
                if ((systemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }
    }

    /**
     * 状态栏白色字体
     *
     * @param activity activity
     */
    public static void statusBarLightFont(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if ((systemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) > 0) {
                systemUiVisibility = systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            }

        }

    }

    /**
     * 全屏
     *
     * @param activity actvity
     */
    public static void fullscreen(@NonNull Activity activity) {

        if (activity.getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | SYSTEM_UI_FLAG_FULLSCREEN
                                | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            } else {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

    }

    /**
     * 补偿因为状态栏透明的 padding
     * {@link #translucentStatusBar(Activity)}
     *
     * @param activity activity
     * @param view     view
     */
    public static void compensateTranslucentStatusBar(@NonNull Activity activity, @NonNull View view) {
        compensateTranslucentStatusBar(activity, view, true);
    }

    /**
     * 补偿因为状态栏透明的 padding
     * {@link #translucentStatusBar(Activity)}
     *
     * @param activity activity
     * @param view     view
     * @param margin   pass margin
     */
    @SuppressWarnings("WeakerAccess")
    public static void compensateTranslucentStatusBar(@NonNull Activity activity, @NonNull View view, boolean margin) {

        if (!isTranslucentStatusBar(activity)) {
            return;
        }

        int statusBarHeight = getStatusBarHeight(activity);

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams && margin) {
            final ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + statusBarHeight;
            view.setLayoutParams(marginLayoutParams);
            return;
        }
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + statusBarHeight,
                view.getPaddingRight(), view.getPaddingBottom());

    }

    @SuppressWarnings("WeakerAccess")
    public static int getStatusBarHeight(@NonNull Activity context) {
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        if (context.getWindow() != null) {
            Rect rect = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            if (rect.top > 0) {
                return rect.top;
            }
        }
        return 0;

    }

    @Nullable
    public static Drawable getDrawable(@DrawableRes int drawableId) {
        final Drawable drawable = ResourcesCompat.getDrawable(sContext
                .getResources(), drawableId, sContext.getTheme());
        if (drawable == null) {
            return null;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    public static String getString(@StringRes int stringRes) {
        return sContext.getString(stringRes);
    }

    public static String getString(@StringRes int stringRes, Object... formatArgs) {
        return sContext.getString(stringRes, formatArgs);
    }

    @ColorInt
    public static int getColor(@ColorRes int colorRes) {
        return ResourcesCompat.getColor(sContext.getResources(), colorRes, null);
    }

    public static int getDimen(@DimenRes int dimenRes) {
        return sContext.getResources().getDimensionPixelOffset(dimenRes);
    }


    public static Typeface getNumberFont() {
        return Typeface.createFromAsset(sContext.getAssets(), KeysConstants.NUMBER_FONT);
    }

}
