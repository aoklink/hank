package cn.linkfeeling.hankserve.data;


import com.link.feeling.framework.component.net.NetResult;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LinkApi {


    @POST("gym/data/upload")
    Single<NetResult<Object>> uploadBleGymData(@Body BleDeviceInfo request);
}
