package cn.linkfeeling.hankserve.nsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author create by zhangyong
 * @time 2019/4/8
 */
public class NsdManagers {


    public static void createNsdInfo(Context context) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
//设置名字（若出现重名的系统自动加上NsdChat (1)）
        serviceInfo.setServiceName("NsdChat");
//设置连接类型使用基于tcp的http协议进行传输
        serviceInfo.setServiceType("_http._tcp.");
//为避免端口冲突，可使用以下代码获取一个可用的端口号
        // Initialize a server socket on the next available port.
        ServerSocket mServerSocket = null;
        try {
            mServerSocket = new ServerSocket(0);
            // Store the chosen port.
            int mLocalPort = mServerSocket.getLocalPort();
//设置端口号
            serviceInfo.setPort(mLocalPort);


            NsdManager mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
            mNsdManager.registerService(
                    serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
                        @Override
                        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            Log.i("qqqqqqqqqqqqqq", "onRegistrationFailed");
                            createNsdInfo(context);
                        }

                        @Override
                        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            Log.i("qqqqqqqqqqqqqq", "onUnregistrationFailed");
                        }

                        @Override
                        public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                            Log.i("qqqqqqqqqqqqqq", "onServiceRegistered");
                        }

                        @Override
                        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                            Log.i("qqqqqqqqqqqqqq", "onServiceUnregistered");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
