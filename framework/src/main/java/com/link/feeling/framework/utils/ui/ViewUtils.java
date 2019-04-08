package com.link.feeling.framework.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.link.feeling.framework.R;
import com.link.feeling.framework.base.BaseApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2019/1/8  16:02
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class ViewUtils {

    private ViewUtils() {
        throw new UnsupportedOperationException("工具类不能调用构造函数");
    }

    public static void setVisible(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setGone(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setInvisible(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setEnable(View view, boolean isEnable) {
        if (view == null) {
            return;
        }
        if (view.isEnabled() != isEnable) {
            view.setEnabled(isEnable);
        }
    }

    public static void setActivated(View view, boolean isActivated) {
        if (view == null) {
            return;
        }
        if (view.isActivated() != isActivated) {
            view.setActivated(isActivated);
        }
    }

    public static boolean isVisible(View view) {
        if (view == null) {
            return false;
        } else {
            return view.getVisibility() == View.VISIBLE;
        }
    }

    public static void setVisible(View v, boolean isVisible) {
        if (v == null) {
            return;
        }
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        if (v.getVisibility() != visibility) {
            v.setVisibility(visibility);
        }
    }

    public static void setMarginLeft(View view, int leftMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = leftMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginRight(View view, int rightMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.rightMargin = rightMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginTop(View view, int topMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.topMargin = topMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginBottom(View view, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.bottomMargin = bottomMargin;
        view.setLayoutParams(params);
    }

    public static void setBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    public static void setDrawableLeft(TextView textView, int drawableResId) {
        if (drawableResId == 0) {
            textView.setCompoundDrawables(null, null, null, null);

            return;
        }

        Drawable drawable = textView.getContext().getResources().getDrawable(drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    public static void setDrawableTop(TextView textView, int drawableResId) {
        Drawable drawable = textView.getContext().getResources().getDrawable(drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(null, drawable, null, null);
        }
    }

    public static void setDrawableRight(TextView textView, int drawableResId) {
        if (drawableResId == 0) {
            textView.setCompoundDrawables(null, null, null, null);

            return;
        }

        Drawable drawable = textView.getContext().getResources().getDrawable(drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(null, null, drawable, null);
        }
    }

    public static void setDrawableBottom(TextView textView, int drawableResId) {
        if (drawableResId == 0){
            textView.setCompoundDrawables(null, null, null, null);
            return;
        }
        Drawable drawable = textView.getContext().getResources().getDrawable(drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(null, null, null, drawable);
        }

    }

    public static String getString(int sid) {
        return BaseApplication.getAppContext().getString(sid);
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        return BaseApplication.getAppContext().getString(resId, formatArgs);
    }

    public static Drawable getDrawable(@DrawableRes int res) {
        Drawable drawable = ResourcesCompat.getDrawable(
                BaseApplication.getAppContext().getResources(),
                res,
                BaseApplication.getAppContext().getTheme()
        );
        if (drawable != null) {
            drawable.setBounds(
                    0,
                    0,
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight()
            );
        }
        return drawable;
    }

    private static long LAST_CLICK_TIME;

    public static boolean isQuickClick() {
        return isQuickClick(500);
    }

    public static boolean isQuickClick(long maxDurantion) {
        long time = System.currentTimeMillis();
        if (Math.abs(time - LAST_CLICK_TIME) < maxDurantion) {
            return true;
        }
        LAST_CLICK_TIME = time;
        return false;
    }

    /**
     * 真实屏幕宽高，除去状态栏和虚拟键盘
     *
     * @return
     */
    public static Point getRealSize() {
        final WindowManager windowManager =
                (WindowManager) BaseApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        // 不含虚拟按键
        // display.getSize(outPoint);
        if (Build.VERSION.SDK_INT >= 17) {
            // 包含虚拟按键
            display.getRealSize(outPoint);
        } else {
            try {
                final Method method = display.getClass().getMethod("getRealSize", Point.class);
                method.invoke(display, outPoint);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return outPoint;
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @return
     */
    public static int getBottomStatusHeight() {
        int totalHeight = getDpi(BaseApplication.getAppContext());

        int contentHeight = getScreenHeight(BaseApplication.getAppContext());

        return totalHeight - contentHeight;
    }

    public static int getStatusBarSize(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static Bitmap loadBitmapFromView(View view, int with, int height) {
        if (view == null) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(with, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        // 这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // 生成bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        // 把view中的内容绘制在画布上
        view.draw(canvas);

        return bitmap;
    }

    public static void ViewTransparentToSolid(Context context, View view) {
        Animation myAnimation = AnimationUtils.loadAnimation(context, R.anim.transparent_to_solid);
        view.startAnimation(myAnimation);
        view.setVisibility(View.VISIBLE);
    }

    public static void ViewTransparentFromSolid(Context context, View view) {
        Animation myAnimation = AnimationUtils.loadAnimation(context, R.anim.transparent_from_solid);
        view.startAnimation(myAnimation);
        view.setVisibility(View.GONE);
    }

    public static int getColor(@ColorRes int id) {
        return BaseApplication.getAppContext().getResources().getColor(id);
    }

    public static boolean isSpecialString(String s) {
        String str = "^[a-zA-Z0-9_\u0391-\uFFE5]+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static Activity getActivityFromView(View view) {
        try {
            Context context = view.getContext();
            if (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                return (Activity) ((ContextWrapper) context).getBaseContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static View getContentView(Activity ac) {
        ViewGroup view = (ViewGroup) ac.getWindow().getDecorView();
        FrameLayout content = (FrameLayout) view.findViewById(android.R.id.content);
        return content.getChildAt(0);
    }

    public static View getItemView(ViewGroup parent, @LayoutRes int ids) {

        return LayoutInflater.from(parent.getContext()).inflate(ids, parent, false);
    }

    /**
     * 扩大视图点击范围
     *
     * @param delegateView 视图
     * @param size         size
     */
    public static void expandViewTouchDelegate(View delegateView, int size) {
        expandViewTouchDelegate(delegateView, size, size, size, size);
    }

    /**
     * 扩大视图点击范围
     *
     * @param delegateView 视图
     * @param top          top
     * @param bottom       bottom
     * @param left         left
     * @param right        right
     */
    public static void expandViewTouchDelegate(View delegateView, int top, int bottom, int left, int right) {
        if (!(delegateView.getParent() instanceof View)) {
            return;
        }
        View parentView = (View) delegateView.getParent();
        Rect rect = new Rect();
        delegateView.getHitRect(rect);
        rect.left -= left;
        rect.top -= top;
        rect.right += right;
        rect.bottom += bottom;
        TouchDelegate touchDelegate = new TouchDelegate(rect, delegateView);
        parentView.setTouchDelegate(touchDelegate);
    }

    /**
     * 金钱格式化
     *
     * @param str
     * @return
     */
    public static String addComma(String str) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        return decimalFormat.format(Double.parseDouble(str));

    }

    private static final Rect TEXT_RECT = new Rect();


    /**
     * 获取当前文字占用行
     *
     * @param textView textview
     * @return 行
     */
    public static boolean getMultiLine(TextView textView, int width) {
        if (textView == null || TextUtils.isEmpty(textView.getText())) {
            return false;
        }
        if (textView.getMeasuredWidth() <= 0) {
            textView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }
        float textWidth = textView.getPaint().measureText(textView.getText().toString());
        return textWidth > (textView.getMeasuredWidth() - textView.getPaddingLeft() - textView.getPaddingRight());
    }
}
