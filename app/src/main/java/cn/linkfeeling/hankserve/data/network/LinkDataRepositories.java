package cn.linkfeeling.hankserve.data.network;

import com.link.feeling.framework.component.net.FrameworkNet;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.WristbandPower;
import io.reactivex.Completable;

import static com.link.feeling.framework.component.net.json.ResponseParse.completableFun;

/**
 * Created on 2019/1/17  11:24
 * chenpan pan.chen@linkfeeling.cn
 */
public final class LinkDataRepositories {

    private final static LinkDataRepositories INSTANCE = new LinkDataRepositories();

    private final LinkDataSources mRemoteDataSources;

    private LinkDataRepositories() {
        mRemoteDataSources = new LinkRemoteDataSources(FrameworkNet.getInstance().providerRetrofit());
    }

    public static LinkDataRepositories getInstance() {
        return INSTANCE;
    }



    public Completable uploadBleGymData(BleDeviceInfo request) {
        return mRemoteDataSources.uploadBleGymData(request).flatMapCompletable(completableFun());
    }

    /**
     * 上报手环电量数据
     * @param request
     * @return
     */
    public Completable uploadWristPowerData(WristbandPower request) {
        return mRemoteDataSources.uploadWristbandPower(request).flatMapCompletable(completableFun());
    }

    /**
     * 上报设备电量
     * @param request
     * @return
     */
    public Completable uploadPowerPowerData(DevicePower request) {
        return mRemoteDataSources.uploadDevicePower(request).flatMapCompletable(completableFun());
    }


}
