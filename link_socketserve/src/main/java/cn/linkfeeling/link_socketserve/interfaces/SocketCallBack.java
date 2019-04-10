package cn.linkfeeling.link_socketserve.interfaces;

/**
 * @author create by zhangyong
 * @time 2019/4/10
 */
public interface SocketCallBack {

    void connectSuccess(String ip);
    void disconnectSuccess(String ip);

    void  getSubjectData();


}
