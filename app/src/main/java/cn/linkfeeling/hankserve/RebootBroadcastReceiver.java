package cn.linkfeeling.hankserve;
 
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

 
/**
 * 自定义 广播接收者
 * 继承 android.content.BroadcastReceiver
 */
public class RebootBroadcastReceiver extends BroadcastReceiver {
 
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
 
    /**
     * 接收广播消息后都会进入 onReceive 方法，然后要做的就是对相应的消息做出相应的处理
     *
     * @param context 表示广播接收器所运行的上下文
     * @param intent  表示广播接收器收到的Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Wmx logs::", intent.getAction());
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();


        //开机后一般会停留在锁屏页面且短时间内没有进行解锁操作屏幕会进入休眠状态，此时就需要先唤醒屏幕和解锁屏幕
        //屏幕唤醒
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
//                | PowerManager.SCREEN_DIM_WAKE_LOCK, "StartupReceiver");//最后的参数是LogCat里用的Tag
//        wl.acquire();
//
//        //屏幕解锁
//        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("StartupReceiver");//参数是LogCat里用的Tag
//        kl.disableKeyguard();
 
        /**
         * 如果 系统 启动的消息，则启动 APP 主页活动
         */
        if (ACTION_BOOT.equals(intent.getAction())) {
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        }
    }
}
