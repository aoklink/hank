package cn.linkfeeling.link_socketserve.interfaces;

import cn.linkfeeling.link_socketserve.bean.ScanData;
import cn.linkfeeling.link_socketserve.unpack.SmartCarProtocol;
import io.netty.channel.group.ChannelGroup;

/**
 * @author create by zhangyong
 * @time 2019/4/10
 */
public interface SocketCallBack {

    void connectSuccess(String ip,int channelsNum);
    void disconnectSuccess(String ip,int channelsNum );

    void getBLEStream(String hostString,SmartCarProtocol tbody);


}
