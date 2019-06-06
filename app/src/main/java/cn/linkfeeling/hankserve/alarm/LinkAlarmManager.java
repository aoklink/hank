package cn.linkfeeling.hankserve.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;


import cn.linkfeeling.hankserve.receiver.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * @author create by zhangyong
 * @time 2019/5/29
 */
public class LinkAlarmManager {
    private LinkAlarmManager() {

    }

    private static final LinkAlarmManager linkAlarmManager = new LinkAlarmManager();

    public static LinkAlarmManager getInstance() {
        return linkAlarmManager;
    }

    /**
     * 开启提醒
     */
    public void startRemind(Context context) {
        //得到日历实例，主要是为了下面的获取时间
        Calendar mCalendar = Calendar.getInstance();
        //  mCalendar.setTimeInMillis(System.currentTimeMillis());

        //获取当前毫秒值
     //   long systemTime = System.currentTimeMillis();

        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));


        //设置在几点提醒  设置的为0点
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        //设置在几分提醒  设置的为0分
        mCalendar.set(Calendar.MINUTE, 59);
        //下面这两个看字面意思也知道
        mCalendar.set(Calendar.SECOND, 59);
//        mCalendar.set(Calendar.MILLISECOND, 0);

//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
//        Toast.makeText(context, simpleDateFormat.format(mCalendar.getTimeInMillis()), Toast.LENGTH_SHORT).show();
      //  Log.i("wwwwwwwwwwww",simpleDateFormat.format(mCalendar.getTimeInMillis()));

        long selectTime = mCalendar.getTimeInMillis();



        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
//        if (systemTime > selectTime) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }

        //AlarmReceiver.class为广播接受者
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //得到AlarmManager实例
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //**********注意！！下面的两个根据实际需求任选其一即可*********



        /**
         * 单次提醒
         * mCalendar.getTimeInMillis() 上面设置的13点25分的时间点毫秒值
         */
          // am.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);

        am.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);



        /**
         * 重复提醒
         * 第一个参数是警报类型；下面有介绍
         * 第二个参数网上说法不一，很多都是说的是延迟多少毫秒执行这个闹钟，但是我用的刷了MIUI的三星手机的实际效果是与单次提醒的参数一样，即设置的13点25分的时间点毫秒值
         * 第三个参数是重复周期，也就是下次提醒的间隔 毫秒值 我这里是一天后提醒
         */
   //     am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);

    }
}
