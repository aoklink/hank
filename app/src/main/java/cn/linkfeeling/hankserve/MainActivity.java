package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.link.feeling.framework.base.FrameworkBaseActivity;
import com.link.feeling.framework.executor.ThreadPoolManager;
import com.link.feeling.framework.utils.data.L;
import com.link.feeling.framework.utils.data.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.adapter.BLEAdapter;
import cn.linkfeeling.hankserve.adapter.MatchAdapter;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.MatchResult;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.WristbandPower;
import cn.linkfeeling.hankserve.factory.DataProcessorFactory;
import cn.linkfeeling.hankserve.interfaces.IAnchDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.manager.LinkWSManager;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.ui.IUploadContract;
import cn.linkfeeling.hankserve.ui.UploadPresenter;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.HexUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;
import cn.linkfeeling.hankserve.utils.WatchScanRecord;
import cn.linkfeeling.link_socketserve.NettyServer;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;
import cn.linkfeeling.link_socketserve.unpack.SmartCarProtocol;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;
import static cn.linkfeeling.hankserve.constants.LinkConstant.Untied_Time;

public class MainActivity extends FrameworkBaseActivity<IUploadContract.IBleUploadView, IUploadContract.IBleUploadPresenter> implements IUploadContract.IBleUploadView {

    private TextView tv_ipTip, tv_ipTipRemove;
    private Gson gson = new Gson();
    private SimpleDateFormat simpleDateFormat;
    private Disposable disposable;
    private Disposable wristPowerDisposable;
    private Disposable devicePowerDisposable;
    private RecyclerView recycleView, match_recycleView;
    private BLEAdapter bleAdapter;
    private MatchAdapter matchAdapter;

    private List<BleDeviceInfo> bleDeviceInfos = new ArrayList<>();
    private List<MatchResult> matchResultList = new ArrayList<>();
    private List<WristbandPower.DataBean> wristPowerList = new ArrayList<>();
    private List<DevicePower.DataBean> devicePowerList = new ArrayList<>();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        recycleView = findViewById(R.id.recycleView);
        match_recycleView = findViewById(R.id.match_recycleView);
        tv_ipTip = findViewById(R.id.tv_ipTip);
        tv_ipTipRemove = findViewById(R.id.tv_ipTipRemove);
        tv_ipTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_ipTipRemove.setMovementMethod(ScrollingMovementMethod.getInstance());

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");


        recycleView.setLayoutManager(new LinearLayoutManager(this));
        match_recycleView.setLayoutManager(new LinearLayoutManager(this));
        bleAdapter = new BLEAdapter(this, bleDeviceInfos);
        recycleView.setAdapter(bleAdapter);

        matchAdapter = new MatchAdapter(this, matchResultList);
        match_recycleView.setAdapter(matchAdapter);

        if (!App.getApplication().isStart()) {
            startServer();
        }
        // UDPBroadcast.udpBroadcast(this);
        connectWebSocket();
        startIntervalListener();
        startIntervalPowerUpload();
        startIntervalDevicePowerUpload();
    }


    private void startServer() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                App.getApplication().setStart(true);
                NettyServer.getInstance().bind(new SocketCallBack() {
                    @Override
                    public void connectSuccess(String ip, int channelsNum) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                App.getApplication().setChannelsNum(channelsNum);
                                tv_ipTip.append(ip + "连接成功");
                                tv_ipTip.append("\n");
                                tv_ipTip.append(simpleDateFormat.format(System.currentTimeMillis()));
                                tv_ipTip.append("\n\n");
                            }
                        });
                    }

                    @Override
                    public void disconnectSuccess(String ip, int channelsNum) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                App.getApplication().setChannelsNum(channelsNum);
                                tv_ipTipRemove.append(ip + "断开连接");
                                tv_ipTipRemove.append("\n");
                                tv_ipTipRemove.append(simpleDateFormat.format(System.currentTimeMillis()));
                                tv_ipTipRemove.append("\n\n");
                            }
                        });
                    }

                    @Override
                    public void getBLEStream(String hostString, SmartCarProtocol smartCarProtocol) {
                        if (smartCarProtocol != null && smartCarProtocol.getContent() != null && smartCarProtocol.getContent().length == 1) {
                            Log.i("kkkkkkkidle----", hostString + "++++++" + CalculateUtil.byteArrayToInt(smartCarProtocol.getContent()));
                            DevicePower.DataBean gateWay = new DevicePower.DataBean();
                            gateWay.setSerial_no(String.valueOf(1));
                            gateWay.setDevice("GateWay" + CalculateUtil.byteArrayToInt(smartCarProtocol.getContent()));
                            gateWay.setDevice_id("GateWay" + CalculateUtil.byteArrayToInt(smartCarProtocol.getContent()));
                            gateWay.setBattery(String.valueOf(-2));
                            FinalDataManager.getInstance().getBleName_dateBean().put(hostString, gateWay);
                        }
                        onLeScanSelf(hostString, smartCarProtocol.getContent());
//                        ThreadPoolManager.getInstance().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                onLeScanSelf(smartCarProtocol.getContent());
//                            }
//                        });


                    }

                    @Override
                    public void offLine(String hostString) {
                        if (hostString != null) {
                            DevicePower.DataBean dataBean = FinalDataManager.getInstance().getBleName_dateBean().get(hostString);
                            if (dataBean != null) {
                                dataBean.setBattery(String.valueOf(-1));
                            }
                        }
                    }
                });
            }
        });


    }

    BleDeviceInfo tempBleInfo;

    private void startIntervalListener() {
        if (disposable == null) {
            disposable = Observable.interval(INTERVAL_TIME, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        if (FinalDataManager.getInstance().getWristbands() != null && !FinalDataManager.getInstance().getWristbands().isEmpty()) {
                            for (Map.Entry<String, BleDeviceInfo> entry : FinalDataManager.getInstance().getWristbands().entrySet()) {
                                BleDeviceInfo value = entry.getValue();
                                if (value == null) {
                                    return;
                                }
                                if (!TextUtils.isEmpty(value.getSpeed())) {
                                    if (Float.parseFloat(value.getSpeed()) == 0) {
                                        value.setDistance(String.valueOf((float) 0));
                                    } else {
                                        BigDecimal bigDecimal = CalculateUtil.floatDivision(INTERVAL_TIME * Float.parseFloat(value.getSpeed()), 3600);
                                        value.setDistance(bigDecimal.toString());
                                    }
                                }
                                Object clone = value.clone();
                                if (clone != null) {
                                    tempBleInfo = (BleDeviceInfo) clone;
                                    getPresenter().uploadBleData(tempBleInfo, value);
                                }

                            /*    if (!tempBleInfo.getSpeed().equals("") && Float.parseFloat(tempBleInfo.getSpeed()) == 0) {
                                    LinkSpecificDevice linkSpecificDevice = LinkDataManager.getInstance().queryDeviceByName(tempBleInfo.getDevice_name());
                                    if (linkSpecificDevice != null) {
                                        linkSpecificDevice.setAbility(0);
                                    }
                                }
*/

                                if (tempBleInfo.getCurve() != null && !tempBleInfo.getCurve().isEmpty()) {
                                    value.getCurve().removeAll(tempBleInfo.getCurve());
                                }

                                if (!"".equals(tempBleInfo.getTime()) && !"".equals(tempBleInfo.getU_time())) {
                                    LinkDataManager.getInstance().cleanFlyBird(value);

                                    //解除绑定
                                    int fenceId = LinkDataManager.getInstance().queryFenceIdByDeviceName(tempBleInfo.getDevice_name());
                                    if (FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId)) {
                                        FinalDataManager.getInstance().removeUwb(fenceId);
                                    }
                                }

                            }
                        }
                    });
        }
    }

    private void startIntervalPowerUpload() {
        if (wristPowerDisposable == null) {
            wristPowerDisposable = Observable.interval(10, TimeUnit.MINUTES)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        wristPowerList.clear();
                        WristbandPower wristbandPower = new WristbandPower();
                        wristbandPower.setGym_name(BuildConfig.GYM_NAME);
                        ConcurrentHashMap<String, Integer> wristPowerMap = LinkDataManager.getInstance().getWristPowerMap();
                        for (Map.Entry<String, Integer> next : wristPowerMap.entrySet()) {
                            WristbandPower.DataBean dataBean = new WristbandPower.DataBean();
                            dataBean.setBracelet_id(next.getKey());
                            dataBean.setBattery(String.valueOf(next.getValue()));
                            wristPowerList.add(dataBean);
                        }
                        wristbandPower.setData(wristPowerList);
                        Log.i("kkkkkkk", gson.toJson(wristbandPower));
                        getPresenter().uploadWristPower(wristbandPower);


                    });
        }
    }

    private void startIntervalDevicePowerUpload() {
        if (devicePowerDisposable == null) {
            devicePowerDisposable = Observable.interval(10, TimeUnit.MINUTES)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        devicePowerList.clear();
                        ConcurrentHashMap<String, DevicePower.DataBean> bleName_dateBean = FinalDataManager.getInstance().getBleName_dateBean();
                        if (bleName_dateBean != null && !bleName_dateBean.isEmpty()) {
                            DevicePower devicePower = new DevicePower();
                            devicePower.setGym_name(BuildConfig.GYM_NAME);
                            for (Map.Entry<String, DevicePower.DataBean> next : bleName_dateBean.entrySet()) {
                                DevicePower.DataBean value = next.getValue();
                                devicePowerList.add(value);
                            }
                            devicePower.setData(devicePowerList);
                            Log.i("kkkkk", gson.toJson(devicePower));
                            getPresenter().uploadDevicePower(devicePower);
                        }
                    });
        }
    }


    /**
     * 处理蓝牙广播数据
     *
     * @author zhangyong
     * @time 2019/3/20 14:55
     */
    private void onLeScanSelf(String hostString, byte[] scanRecord) {
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        if (linkScanRecord == null || linkScanRecord.getDeviceName() == null) {
            return;
        }
        String name = linkScanRecord.getDeviceName();
        if ("I7".equals(name)) {
            WatchScanRecord watchScanRecord = WatchScanRecord.parseFromBytes(scanRecord);
            byte[] bytes = watchScanRecord.getManufacturerSpecificData().valueAt(0);
            byte[] mac = new byte[2];
            mac[0] = bytes[0];
            mac[1] = bytes[1];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(HexUtil.encodeHexStr(mac));
            name = stringBuilder.toString();
            Log.i("777777777777" + name, Arrays.toString(bytes));
        }


        if ("I7PLUS".equals(name)) {
            byte[] bytes = linkScanRecord.getManufacturerSpecificData().valueAt(0);
            byte[] mac = new byte[2];
            mac[0] = bytes[0];
            mac[1] = bytes[1];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(HexUtil.encodeHexStr(mac));
            name = stringBuilder.toString();
        }

        Log.i("nnnnnnnnnnnnn", hostString + "-----" + name);
        if (LinkDataManager.getInstance().getUwbCode_wristbandName().containsValue(name)) {
            try {
                BleDeviceInfo bleDeviceInfo;
                if (FinalDataManager.getInstance().getWristbands().get(name) != null) {
                    bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(name);
                } else {
                    bleDeviceInfo = new BleDeviceInfo();
                    LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                }
                IWristbandDataAnalysis leap = (IWristbandDataAnalysis) DataProcessorFactory.creteProcess(LinkDataManager.TYPE_LEAP, name);
                BleDeviceInfo bleDeviceFinal = leap.analysisWristbandData(bleDeviceInfo, scanRecord, name);
                if (bleDeviceFinal == null) {
                    return;
                }
                FinalDataManager.getInstance().getWristbands().put(name, bleDeviceFinal);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String bleType = LinkDataManager.getInstance().getDeviceBleTypeMaps().get(name);
        if (bleType != null) {
            try {
                IDataAnalysis iDataAnalysis = DataProcessorFactory.creteProcess(bleType, name);
                BleDeviceInfo bleDeviceInfoFinal = iDataAnalysis.analysisBLEData(hostString, scanRecord, name);
                if (bleDeviceInfoFinal == null) {
                    return;
                }
                FinalDataManager.getInstance().getWristbands().put(bleDeviceInfoFinal.getBracelet_id(), bleDeviceInfoFinal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (name.startsWith(LinkDataManager.ANCH)) {
            try {
                IAnchDataAnalysis anchDataAnalysis = (IAnchDataAnalysis) DataProcessorFactory.creteProcess(LinkDataManager.ANCH, name);
                anchDataAnalysis.analysisAnchData(scanRecord, name);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 连接UWB基站长连接
     */
    private void connectWebSocket() {
        LinkWSManager.getInstance().connectWebSocket(text -> {
            try {
                dealMessage(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 连接后端webSocket
     */
    public void connectLinkWS() {
        LinkWSManager.getInstance().connectLinkWsConnect(text -> {
            Log.i("link==ws", text);
//            ChannelMatcher matcher = new ChannelMatcher() {
//                @Override
//                public boolean matches(Channel channel) {
//                    String hostString = ((InetSocketAddress) channel.remoteAddress()).getHostString();
//                    if (channel.isOpen() && channel.isActive() && hostString.equals("192.168.0.105")) {
//                        return true;
//                    }
//                    return false;
//                }
//            };
//            Global.group.writeAndFlush(text, matcher);
        });
    }


    private UWBCoordData newUwb;

    /**
     * 处理uwb设备数据
     *
     * @author zhangyong
     * @time 2019/3/20 14:57
     */
    private synchronized void dealMessage(String text) {
        newUwb = gson.fromJson(text, UWBCoordData.class);
        if (newUwb == null) {
            return;
        }
        //写入队列
        Log.i("666666666", "查看长度，，，" + FinalDataManager.getInstance().getFenceId_uwbData().size() + "");
        boolean within = LinkDataManager.getInstance().isPointInRect(newUwb);
        //1、首先查找在哪个围栏内
        String code = newUwb.getCode();
        UWBCoordData uwbCoordData = FinalDataManager.getInstance().queryUwb(code);


        if (within) {
            //  writeQueue(newUwb);
            if (uwbCoordData != null) {
                //2.说明已经绑定围栏
                //4、说明该标签已经绑定设备
                if (LinkDataManager.getInstance().contain(uwbCoordData, newUwb)) {
                    uwbCoordData.setSemaphore(0);
                } else {
                    //6、说明进入的不是之前绑定的区域
                    if (uwbCoordData.getSemaphore() == Untied_Time) {
                        //7、需要解除绑定

                        FinalDataManager.getInstance().removeUwb(uwbCoordData.getDevice().getFencePoint().getFenceId());
                        uwbCoordData.setSemaphore(0);

                        if (newUwb.getWristband().getBracelet_id() == null) {
                            return;
                        }
                        BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(newUwb.getWristband().getBracelet_id());
                        if (bleDeviceInfo != null) {
                            LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                        }

                    } else {
                        uwbCoordData.setSemaphore(uwbCoordData.getSemaphore() + 1);
                    }
                }
                return;
            }


            List<UWBCoordData> list = FinalDataManager.getInstance().querySpareFireUwb(code);
            if (!list.isEmpty()) {
                for (UWBCoordData spareFireUwb : list) {
                    if (LinkDataManager.getInstance().contain(spareFireUwb, newUwb)) {
                        spareFireUwb.setSemaphore(0);
                    } else {
                        if (spareFireUwb.getSemaphore() == Untied_Time) {
                            //7、需要解除绑定
                            FinalDataManager.getInstance().removeSpareFireUwb(spareFireUwb);
                            spareFireUwb.setSemaphore(0);

                        } else {
                            //8、将信号量+1
                            spareFireUwb.setSemaphore(spareFireUwb.getSemaphore() + 1);
                        }
                    }
                }
            }

            if (newUwb.getWristband().getBracelet_id() == null) {
                return;
            }
            BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(newUwb.getWristband().getBracelet_id());
            if (bleDeviceInfo != null) {
                bleDeviceInfo.setDevice_name(newUwb.getDevice().getDeviceName());
            }
        }
        if (!within) {
            //   writeQueue(newUwb);
            if (uwbCoordData != null) {
                if (LinkDataManager.getInstance().contain(uwbCoordData, newUwb)) {
                    uwbCoordData.setSemaphore(0);
                } else {
                    if (uwbCoordData.getSemaphore() == Untied_Time) {
                        //7、需要解除绑定
                        FinalDataManager.getInstance().removeUwb(uwbCoordData.getDevice().getFencePoint().getFenceId());
                        uwbCoordData.setSemaphore(0);

                        if (newUwb.getWristband().getBracelet_id() == null) {
                            return;
                        }
                        BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(newUwb.getWristband().getBracelet_id());
                        if (bleDeviceInfo != null) {
                            LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                        }

                    } else {
                        uwbCoordData.setSemaphore(uwbCoordData.getSemaphore() + 1);
                  /*      UWBCoordData.FencePoint.Point centerPoint = uwbCoordData.getDevice().getCenterPoint();
                        if(CalculateUtil.pointDistance(centerPoint.getX(),centerPoint.getY(),newUwb.getX(),newUwb.getY())>150){
                            //8、将信号量+1
                            uwbCoordData.setSemaphore(uwbCoordData.getSemaphore() + 1);
                        }*/
                    }
                }
                return;
            }

            List<UWBCoordData> list = FinalDataManager.getInstance().querySpareFireUwb(code);
            if (!list.isEmpty()) {
                for (UWBCoordData spareFireUwb : list) {
                    if (LinkDataManager.getInstance().contain(spareFireUwb, newUwb)) {
                        spareFireUwb.setSemaphore(0);
                    } else {
                        if (spareFireUwb.getSemaphore() == Untied_Time) {
                            //7、需要解除绑定
                            FinalDataManager.getInstance().removeSpareFireUwb(spareFireUwb);
                            spareFireUwb.setSemaphore(0);

                        } else {
                       /*     UWBCoordData.FencePoint.Point centerPoint = spareFireUwb.getDevice().getCenterPoint();
                            if (CalculateUtil.pointDistance(centerPoint.getX(), centerPoint.getY(), newUwb.getX(), newUwb.getY()) > 150) {
                                //8、将信号量+1
                                spareFireUwb.setSemaphore(spareFireUwb.getSemaphore() + 1);
                            }*/

                            spareFireUwb.setSemaphore(spareFireUwb.getSemaphore() + 1);
                        }
                    }
                }
            }

            if (newUwb.getWristband().getBracelet_id() == null) {
                return;
            }
            BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(newUwb.getWristband().getBracelet_id());
            if (bleDeviceInfo != null) {
                LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
            }
        }
    }

    @Override
    public void uploadBleStatus(BleDeviceInfo temp, BleDeviceInfo bleDeviceInfo, boolean status, Throwable throwable) {
        try {
            if (!status) {

                String s = gson.toJson(temp);
                L.i("wwwwwwwwwwww", s);
                L.i("wwwwwwwwwwww", throwable.getMessage());
                bleDeviceInfo.setReport(false);
                updateData(bleDeviceInfo);
            } else {
                String s = gson.toJson(temp);
                L.i("ffffffffffff", s);
                bleDeviceInfo.setReport(true);
                updateData(bleDeviceInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void uploadWristPowerStatus(boolean status, Throwable throwable) {
        showToast(status ? "手环电量上传成功" : throwable.getMessage());
    }

    @Override
    public void uploadDevicePowerStatus(boolean status, Throwable throwable) {
        showToast(status ? "设备电量上传成功" : throwable.getMessage());
    }


    private void updateData(BleDeviceInfo bleDeviceInfo) {
        int index = bleDeviceInfos.indexOf(bleDeviceInfo);
        if (index == -1) {
            bleDeviceInfos.add(bleDeviceInfo);
        } else {
            bleDeviceInfos.set(index, bleDeviceInfo);
        }
        bleAdapter.notifyDataSetChanged();
    }

    @Override
    public IUploadContract.IBleUploadPresenter createPresenter() {
        return new UploadPresenter();
    }


    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();


        if (itemId == R.id.action_settings) {
            ToastUtils.showToast("清空匹配数据");
            matchResultList.clear();
            matchAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMatchData(MatchResult matchResult) {
        matchResultList.add(matchResult);
        matchAdapter.notifyDataSetChanged();
        match_recycleView.scrollToPosition(matchAdapter.getItemCount() - 1);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        if (wristPowerDisposable != null && !wristPowerDisposable.isDisposed()) {
            wristPowerDisposable.dispose();
            wristPowerDisposable = null;
        }

        if (devicePowerDisposable != null && !devicePowerDisposable.isDisposed()) {
            devicePowerDisposable.dispose();
            devicePowerDisposable = null;
        }
    }
}
