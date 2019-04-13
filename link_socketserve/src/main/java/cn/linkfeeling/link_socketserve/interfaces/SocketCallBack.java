package cn.linkfeeling.link_socketserve.interfaces;

import cn.linkfeeling.link_socketserve.bean.ScanData;
import io.netty.channel.group.ChannelGroup;

/**
 * @author create by zhangyong
 * @time 2019/4/10
 */
public interface SocketCallBack {

    void connectSuccess(String ip,int channelsNum);
    void disconnectSuccess(String ip,int channelsNum );

    void  getSubjectData(ScanData data);


}
