package cn.linkfeeling.hankserve.ui;

import com.link.feeling.framework.base.BaseMvpPresenter;
import com.link.feeling.framework.base.BaseMvpView;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.WristbandPower;


/**
 * @author create by zhangyong
 * @time 2019/3/13
 */
public interface IUploadContract {

    interface IBleUploadView extends BaseMvpView {
        void uploadBleStatus(BleDeviceInfo temp,BleDeviceInfo bleDeviceInfo,boolean status,Throwable e);
        void uploadWristPowerStatus(boolean status,Throwable throwable);
        void uploadDevicePowerStatus(boolean status,Throwable throwable);
    }

    interface IBleUploadPresenter extends BaseMvpPresenter<IBleUploadView> {
        void uploadBleData(BleDeviceInfo temp,BleDeviceInfo bleDeviceInfo);
        void uploadWristPower(WristbandPower wristbandPower);
        void uploadDevicePower(DevicePower devicePower);
    }
}
