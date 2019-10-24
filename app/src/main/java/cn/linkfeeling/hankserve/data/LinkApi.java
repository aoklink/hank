package cn.linkfeeling.hankserve.data;


import com.link.feeling.framework.component.net.NetResult;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.WristbandPower;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LinkApi {


    @POST("gym/data/upload")
    Single<NetResult<Object>> uploadBleGymData(@Body BleDeviceInfo request);

    @POST("platform/coulometry/bracelet/add")
    Single<NetResult<Object>> uploadWristbandPower(@Body WristbandPower request);  //上传手环电量

    @POST("platform/coulometry/add")
    Single<NetResult<Object>> uploadDevicePower(@Body DevicePower request);  //上传设备电量
}
