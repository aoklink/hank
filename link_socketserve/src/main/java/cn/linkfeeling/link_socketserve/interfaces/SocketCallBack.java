package cn.linkfeeling.link_socketserve.interfaces;

import cn.linkfeeling.link_socketserve.bean.ScanData;

/**
 * @author create by zhangyong
 * @time 2019/4/10
 */
public interface SocketCallBack {

    void connectSuccess(String ip);
    void disconnectSuccess(String ip);

    void  getSubjectData(ScanData data);


}
