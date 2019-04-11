package cn.linkfeeling.hankserve.data.network;

import com.link.feeling.framework.component.net.FrameworkNet;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
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


}
