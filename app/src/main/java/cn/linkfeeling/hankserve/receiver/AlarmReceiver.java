package cn.linkfeeling.hankserve.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.linkfeeling.hankserve.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //当系统到我们设定的时间点的时候会发送广播，执行这里
        Log.i("sssssss", "闹钟响了");
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent inte = new Intent(context, MainActivity.class);
                inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(inte);
                // android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        },5 * 1000);


    }
}
