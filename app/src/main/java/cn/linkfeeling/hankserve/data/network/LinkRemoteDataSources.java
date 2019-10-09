package cn.linkfeeling.hankserve.data.network;


import com.link.feeling.framework.component.net.NetResult;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.WristbandPower;
import cn.linkfeeling.hankserve.data.LinkApi;
import io.reactivex.Single;
import retrofit2.Retrofit;

/**
 * Created on 2019/1/17  11:28
 * chenpan pan.chen@linkfeeling.cn
 */
public final class LinkRemoteDataSources implements LinkDataSources{

    private final LinkApi mApi;

    LinkRemoteDataSources(Retrofit retrofit) {
        mApi = retrofit.create(LinkApi.class);
    }


    @Override
    public Single<NetResult<Object>> uploadBleGymData(BleDeviceInfo request) {
        return mApi.uploadBleGymData(request);
    }

    @Override
    public Single<NetResult<Object>> uploadWristbandPower(WristbandPower request) {
        return mApi.uploadWristbandPower(request);
    }


}

