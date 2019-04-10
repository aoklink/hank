package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.link.feeling.framework.executor.ThreadPoolManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import cn.linkfeeling.hankserve.utils.CommonUtil;
import cn.linkfeeling.link_socketserve.NettyServer;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;

public class MainActivity extends AppCompatActivity {

    private TextView tv_ipTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        tv_ipTip = findViewById(R.id.tv_ipTip);

        initWakeLock();

        udpBroadcast();


        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                NettyServer.getInstance().bind(new SocketCallBack() {


                    @Override
                    public void connectSuccess(String ip) {
                        tv_ipTip.append(ip + "连接成功！！");
                        tv_ipTip.append("\n\n");

                    }

                    @Override
                    public void disconnectSuccess(String ip) {
                        tv_ipTip.append(ip + "断开连接！！");
                        tv_ipTip.append("\n\n");
                    }

                    @Override
                    public void getSubjectData() {

                    }
                });
            }
        });


    }

    /**
     * 保持cpu唤醒
     */
    private void initWakeLock() {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // 创建唤醒锁
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "hank:link-feeling");

        // 获得唤醒锁
        wakeLock.acquire();

    }

    private void udpBroadcast() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 广播的实现 :由客户端发出广播，服务器端接收
                String host = "255.255.255.255";// 广播地址
                int port = 4399;// 广播的目的端口
                String message = CommonUtil.getIPAddress(MainActivity.this);// 用于发送的字符串
                Log.i("eeeeeeeeee111",message);
                try {
                    InetAddress adds = InetAddress.getByName(host);
                    DatagramSocket ds = new DatagramSocket();
                    DatagramPacket dp = new DatagramPacket(message.getBytes(),
                            message.length(), adds, port);
                    while (true) {
                        Log.i("eeeeeeeeee",message);
                        ds.send(dp);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
