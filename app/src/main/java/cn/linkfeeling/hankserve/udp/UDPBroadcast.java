package cn.linkfeeling.hankserve.udp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.link.feeling.framework.executor.ThreadPoolManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.utils.CommonUtil;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @author create by zhangyong
 * @time 2019/4/11
 */
public class UDPBroadcast {

    public static void udpBroadcast(Context context) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 广播的实现 :由客户端发出广播，服务器端接收
                final String host = "255.255.255.255";// 广播地址
                final int port = 4399;// 广播的目的端口

                final InetAddress[] adds = new InetAddress[1];
                final DatagramSocket[] ds = new DatagramSocket[1];
                final DatagramPacket[] dp = new DatagramPacket[1];

                Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        String message = CommonUtil.getIPAddress(context.getApplicationContext());// 用于发送的字符串
                        if ("".equals(message)) {
                            return;
                        }
                        try {
                            adds[0] = InetAddress.getByName(host);
                            ds[0] = new DatagramSocket();
                            dp[0] = new DatagramPacket(message.trim().getBytes(), message.trim().length(), adds[0], port);
                            ds[0].send(dp[0]);
                            Log.i("eeeeeeeeee", message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


//
//                String host = "192.168.50.255";// 广播地址
//                int port = 4399;// 广播的目的端口
//                String message = CommonUtil.getIPAddress(context.getApplicationContext());// 用于发送的字符串
//                Log.i("eeeeeeeeee111", message);
//                try {
//                    InetAddress adds = InetAddress.getByName(host);
//                    DatagramSocket ds = new DatagramSocket();
//                    DatagramPacket dp = new DatagramPacket(message.trim().getBytes(),
//                            message.trim().length(), adds, port);
//                    while (true) {
//                        ds.send(dp);
//                        Log.i("eeeeeeeeee", message);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


            }
        });
    }

}
