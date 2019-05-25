package cn.linkfeeling.link_socketserve;

import cn.linkfeeling.link_socketserve.bean.ScanData;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;

/**
 * @author create by zhangyong
 * @time 2019/5/25
 */
public class Test {
    public static void main(String args[]){
        NettyServer.getInstance().bind(new SocketCallBack() {
            @Override
            public void connectSuccess(String ip, int channelsNum) {

            }

            @Override
            public void disconnectSuccess(String ip, int channelsNum) {

            }

            @Override
            public void getSubjectData(ScanData data) {

            }
        });
    }
}
