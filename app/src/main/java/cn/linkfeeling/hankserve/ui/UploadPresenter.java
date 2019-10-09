package cn.linkfeeling.hankserve.ui;

import com.link.feeling.framework.base.BasePresenter;
import com.link.feeling.framework.component.rx.BaseCompletableObserver;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.WristbandPower;
import cn.linkfeeling.hankserve.data.network.LinkDataRepositories;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author create by zhangyong
 * @time 2019/3/13
 */
public class UploadPresenter extends BasePresenter<IUploadContract.IBleUploadView> implements IUploadContract.IBleUploadPresenter {
    @Override
    public void uploadBleData(BleDeviceInfo temp,BleDeviceInfo bleDeviceInfo) {
        Completable completable = LinkDataRepositories.getInstance().uploadBleGymData(temp);
        completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseCompletableObserver(this) {
                    @Override
                    public void onComplete() {
                        super.onComplete();

                        onceViewAttached(view -> {
                            view.uploadBleStatus(temp,bleDeviceInfo, true,null);
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onceViewAttached(view -> {
                            view.uploadBleStatus(temp,bleDeviceInfo, false,e);
                          //  showToast(e.getMessage());
                        });
                    }
                });


    }

    @Override
    public void uploadWristPower(WristbandPower wristbandPower) {
        Completable completable = LinkDataRepositories.getInstance().uploadWristPowerData(wristbandPower);
        completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseCompletableObserver(this) {
                    @Override
                    public void onComplete() {
                        super.onComplete();

                        onceViewAttached(view -> {
                            view.uploadWristPowerStatus(true);
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onceViewAttached(view -> {
                            view.uploadWristPowerStatus(false);
                            //  showToast(e.getMessage());
                        });
                    }
                });
    }

    @Override
    public void uploadDevicePower(DevicePower devicePower) {
        Completable completable = LinkDataRepositories.getInstance().uploadPowerPowerData(devicePower);
        completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseCompletableObserver(this) {
                    @Override
                    public void onComplete() {
                        super.onComplete();

                        onceViewAttached(view -> {
                            view.uploadDevicePowerStatus(true);
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onceViewAttached(view -> {
                            view.uploadDevicePowerStatus(false);
                            //  showToast(e.getMessage());
                        });
                    }
                });
    }
}
