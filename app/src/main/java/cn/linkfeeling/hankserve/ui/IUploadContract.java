package cn.linkfeeling.hankserve.ui;

import com.link.feeling.framework.base.BaseMvpPresenter;
import com.link.feeling.framework.base.BaseMvpView;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;


/**
 * @author create by zhangyong
 * @time 2019/3/13
 */
public interface IUploadContract {

    interface IBleUploadView extends BaseMvpView {
        void uploadBleStatus(BleDeviceInfo bleDeviceInfo, boolean status);
    }

    interface IBleUploadPresenter extends BaseMvpPresenter<IBleUploadView> {
        void uploadBleData(BleDeviceInfo bleDeviceInfo);
    }
}