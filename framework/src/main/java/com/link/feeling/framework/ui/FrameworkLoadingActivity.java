package com.link.feeling.framework.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.link.feeling.framework.R;
import com.link.feeling.framework.utils.data.L;
import com.link.feeling.framework.utils.data.StringUtils;
import com.link.feeling.framework.utils.rx.Rx2Bus;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2019/1/14  16:49
 * chenpan pan.chen@linkfeeling.cn
 *
 * 通用加载框
 */
@SuppressWarnings("unused")
public final class FrameworkLoadingActivity extends AppCompatActivity {

    private SVProgressHUD mProgressHUD;
    // 最后一次加载框的状态 true/false
    private final static AtomicBoolean LAST_LOADING_STATUS = new AtomicBoolean(true);

    // 普通信息
    private static final String BUNDLE_MESSAGE = "bundle_message";
    // 状态
    private static final String BUNDLE_STATUS = "bundle_status";

    // 加载
    private static final int STATUS_LOADING = 0;
    // 正常
    private static final int STATUS_NORMAL = 1;
    // 错误
    private static final int STATUS_ERROR = -1;

    // 30s
    private final static long LOADING_TIMEOUT = 30 * 1000;
    private static final int WHAT_TIMEOUT = 123;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_TIMEOUT) {
                L.d("关闭加载框：超时");
                finish();
            }
        }
    };

    /**
     * 启动错误提示
     *
     * @param context      上下文
     * @param errorMessage 错误信息
     */
    public static void launchError(Context context, String errorMessage) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, FrameworkLoadingActivity.class);
        intent.putExtra(BUNDLE_STATUS, STATUS_ERROR);
        if (!StringUtils.isEmpty(errorMessage)) {
            intent.putExtra(BUNDLE_MESSAGE, errorMessage);
        }
        context.startActivity(intent);
    }

    /**
     * 启动普通提示
     *
     * @param context       上下文
     * @param normalMessage 信息
     */
    public static void launchNormal(Context context, String normalMessage) {
        Intent intent = new Intent();
        intent.setClass(context, FrameworkLoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BUNDLE_STATUS, STATUS_NORMAL);
        if (!StringUtils.isEmpty(normalMessage)) {
            intent.putExtra(BUNDLE_MESSAGE, normalMessage);
        }
        context.startActivity(intent);
    }

    /**
     * 启动加载提示
     *
     * @param context 上下文
     */
    public static void launchLoading(Context context) {
        LAST_LOADING_STATUS.set(true);
        L.d("启动加载框：发送事件");
        Intent intent = new Intent();
        intent.setClass(context, FrameworkLoadingActivity.class);
        intent.putExtra(BUNDLE_STATUS, STATUS_LOADING);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 关闭
     */
    public static void dismiss() {
        LAST_LOADING_STATUS.set(false);
        L.d("关闭加载框：发送事件");
        Rx2Bus.getInstance().post(new LoadingEvent());
    }

    // 状态
    private int mStatus = STATUS_LOADING;
    // 信息
    private String mMessage = "";

    private Disposable mDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.framework_activity_loading);
        L.d("关闭加载框：绑定事件");
        mDisposable = Rx2Bus.getInstance()
                .toObservable(LoadingEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadingEvent -> {
                    L.d("关闭加载框：接受事件");
                    if (mStatus == STATUS_LOADING) {
                        // 加载中
                        L.d("关闭加载框：接受事件关闭");
                        finish();
                    }
                });
        init(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mHandler.removeMessages(WHAT_TIMEOUT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int status = intent.getIntExtra(BUNDLE_STATUS, STATUS_LOADING);
        if (status == STATUS_LOADING) {
            // 防止 loading 把提示覆盖了
            return;
        }
        init(intent);
        show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
            mProgressHUD.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void init(Intent intent) {
        if (mProgressHUD == null) {
            mProgressHUD = new SVProgressHUD(this);
            mProgressHUD.setOnDismissListener(hud -> finish());
        }
        mStatus = intent.getIntExtra(BUNDLE_STATUS, STATUS_LOADING);
        mMessage = intent.getStringExtra(BUNDLE_MESSAGE);
        if (mMessage == null) {
            mMessage = "";
        }
        if (!LAST_LOADING_STATUS.get() && mStatus == STATUS_LOADING) {
            L.d("关闭加载框：最后一次为 false");
            finish();
        }
    }

    private void show() {
        switch (mStatus) {
            case STATUS_NORMAL:
                mProgressHUD.showInfoWithStatus(mMessage, SVProgressHUD.SVProgressHUDMaskType.Clear);
                mHandler.removeMessages(WHAT_TIMEOUT);
                break;
            case STATUS_ERROR:
                mProgressHUD.showErrorWithStatus(mMessage, SVProgressHUD.SVProgressHUDMaskType.Clear);
                mHandler.removeMessages(WHAT_TIMEOUT);
                break;
            default:
            case STATUS_LOADING:
                mProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Clear);
                if (!mHandler.hasMessages(WHAT_TIMEOUT)) {
                    // loading 超时
                    mHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT, LOADING_TIMEOUT);
                }
                break;
        }
        setCancelable();
    }

    private void setCancelable() {
        final View view = findViewById(R.id.sv_outmost_container);
        if (view == null) {
            return;
        }
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setOnTouchListener(null);
                    finish();
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
            }
            return false;
        });

    }

    private static class LoadingEvent {

    }
}
