package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.link.feeling.framework.base.FrameworkBaseActivity;
import com.link.feeling.framework.executor.ThreadPoolManager;
import com.link.feeling.framework.utils.data.L;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.adapter.BLEAdapter;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.factory.DataProcessorFactory;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.manager.LinkWSManager;
import cn.linkfeeling.hankserve.ui.IUploadContract;
import cn.linkfeeling.hankserve.ui.UploadPresenter;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.HexUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;
import cn.linkfeeling.link_socketserve.NettyServer;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;
import cn.linkfeeling.link_socketserve.unpack.SmartCarProtocol;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;

public class MainActivity extends FrameworkBaseActivity<IUploadContract.IBleUploadView, IUploadContract.IBleUploadPresenter> implements IUploadContract.IBleUploadView {
    private List<UWBCoordData> list = new ArrayList<>();
    private TextView tv_ipTip, tv_ipTipRemove;
    private Gson gson = new Gson();
    private SimpleDateFormat simpleDateFormat;
    private Disposable disposable;
    private RecyclerView recycleView;
    private BLEAdapter bleAdapter;

    private List<BleDeviceInfo> bleDeviceInfos = new ArrayList<>();


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        recycleView = findViewById(R.id.recycleView);
        tv_ipTip = findViewById(R.id.tv_ipTip);
        tv_ipTipRemove = findViewById(R.id.tv_ipTipRemove);
        tv_ipTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_ipTipRemove.setMovementMethod(ScrollingMovementMethod.getInstance());

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");


        recycleView.setLayoutManager(new LinearLayoutManager(this));
        bleAdapter = new BLEAdapter(this, bleDeviceInfos);
        recycleView.setAdapter(bleAdapter);

        if (!App.getApplication().isStart()) {
            startServer();
        }
        // UDPBroadcast.udpBroadcast(this);
        connectWebSocket();
        startIntervalListener();
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
//                                tv_ipTip.append(ip + "连接成功");
//                                tv_ipTip.append("\n");
//                                tv_ipTip.append(simpleDateFormat.format(System.currentTimeMillis()));
//                                tv_ipTip.append("\n\n");
                            }
                        });
                    }

                    @Override
                    public void disconnectSuccess(String ip, int channelsNum) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                App.getApplication().setChannelsNum(channelsNum);
//                                tv_ipTipRemove.append(ip + "断开连接");
//                                tv_ipTipRemove.append("\n");
//                                tv_ipTipRemove.append(simpleDateFormat.format(System.currentTimeMillis()));
//                                tv_ipTipRemove.append("\n\n");
                            }
                        });
                    }

                    @Override
                    public void getBLEStream(String hostString, SmartCarProtocol smartCarProtocol) {

                        onLeScanSelf(hostString, smartCarProtocol.getContent());
//                        ThreadPoolManager.getInstance().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                onLeScanSelf(smartCarProtocol.getContent());
//                            }
//                        });


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
                                if (value != null && !TextUtils.isEmpty(value.getSpeed())) {
                                    if (Float.parseFloat(value.getSpeed()) == 0) {
                                        value.setDistance(String.valueOf((float) 0));
                                    } else {
                                        BigDecimal bigDecimal = CalculateUtil.floatDivision(INTERVAL_TIME * Float.parseFloat(value.getSpeed()), 3600);
                                        value.setDistance(bigDecimal.toString());
                                    }
                                }
                                String s = gson.toJson(value);
                                L.i("rrrrrrrrrrrrrrrr", s);

                                tempBleInfo = gson.fromJson(gson.toJson(value), BleDeviceInfo.class);
//
//
//                                tempBleInfo = new BleDeviceInfo();
//                                tempBleInfo.setBracelet_id(value.getBracelet_id());
//                                tempBleInfo.setU_time(value.getU_time());
//                                tempBleInfo.setDevice_name(value.getDevice_name());
//                                tempBleInfo.setDistance(value.getDistance());
//                                tempBleInfo.setExercise_time(value.getExercise_time());
//                                tempBleInfo.setGradient(value.getGradient());
//                                tempBleInfo.setGravity(value.getGravity());
//                                tempBleInfo.setGym_name(value.getGym_name());
//                                tempBleInfo.setHeart_rate(value.getHeart_rate());
//                                tempBleInfo.setReport(value.isReport());
//                                tempBleInfo.setSpeed(value.getSpeed());
//                                tempBleInfo.setTime(value.getTime());
//

                                getPresenter().uploadBleData(tempBleInfo, value);

                            }
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
                BleDeviceInfo bleDeviceInfoFinal = iDataAnalysis.analysisBLEData(scanRecord, name);
                if (bleDeviceInfoFinal == null) {
                    return;
                }
                FinalDataManager.getInstance().getWristbands().put(bleDeviceInfoFinal.getBracelet_id(), bleDeviceInfoFinal);
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
            dealMessage(text);
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


    /**
     * 处理uwb设备数据
     *
     * @author zhangyong
     * @time 2019/3/20 14:57
     */
    private void dealMessage(String text) {
        UWBCoordData newUwb = gson.fromJson(text, UWBCoordData.class);
        if (newUwb == null) {
            return;
        }
        boolean within = LinkDataManager.getInstance().isPointInRect(newUwb);
        if (within) {
            int fenceId = newUwb.getDevice().getFencePoint().getFenceId();
            if (FinalDataManager.getInstance().getFenceId_uwbData().get(fenceId) != null) {
                return;
            }
            //&& System.currentTimeMillis() - newUwb.getDevice().getReceiveDeviceBleTime() > 4000

            if (newUwb.getDevice().getAbility() != 0 && System.currentTimeMillis() - newUwb.getDevice().getReceiveDeviceBleTime() > 10000) {

                L.i("mmmmmmmmmmm", System.currentTimeMillis() - newUwb.getDevice().getReceiveDeviceBleTime() + "");
                return;
            }

            if (FinalDataManager.getInstance().getFenceId_uwbData().containsValue(newUwb)) {
                Iterator iterator = FinalDataManager.getInstance().getFenceId_uwbData().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    UWBCoordData oldUwbValue = (UWBCoordData) entry.getValue();
                    if (oldUwbValue.getCode().equals(newUwb.getCode())) {
                        //运动过程中 uwb偏移了
                        Log.i("77777", oldUwbValue.getDevice().getAbility() + "");

                        if (oldUwbValue.getDevice().getAbility() != 0) {
                            return;
                        }
                        BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(oldUwbValue.getWristband().getBracelet_id());
                        if (bleDeviceInfo != null) {
                            LinkDataManager.getInstance().cleanBleDeviceInfo(bleDeviceInfo);
                        }
                        iterator.remove();
                    }
                }
            }

            if (newUwb.getWristband().getBracelet_id() == null) {
                return;
            }
            BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(newUwb.getWristband().getBracelet_id());
            if (bleDeviceInfo != null) {
                //如果是HIIT区域
                if (newUwb.getDevice().getDeviceName().equals("HIIT")) {
                    if (!list.contains(newUwb)) {
                        list.add(newUwb);
                    }
                } else {
                    FinalDataManager.getInstance().getFenceId_uwbData().put(newUwb.getDevice().getFencePoint().getFenceId(), newUwb);
                }
                bleDeviceInfo.setDevice_name(newUwb.getDevice().getDeviceName());
            }
        }


        if (!within) {
            if (FinalDataManager.getInstance().getFenceId_uwbData().containsValue(newUwb)) {
                Iterator iterator = FinalDataManager.getInstance().getFenceId_uwbData().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    UWBCoordData oldUwbValue = (UWBCoordData) entry.getValue();

                    if (oldUwbValue.getCode().equals(newUwb.getCode())) {

                        L.i("777777", oldUwbValue.getDevice().getDeviceName() + "---" + oldUwbValue.getDevice().getAbility());
                        if (oldUwbValue.getDevice().getAbility() != 0) {
                            return;
                        }
                        BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(oldUwbValue.getWristband().getBracelet_id());
                        if (bleDeviceInfo != null) {
                            LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                        }
                        iterator.remove();
                    }
                }
                return;
            }

            Iterator<UWBCoordData> iterator = list.iterator();
            while (iterator.hasNext()) {
                UWBCoordData coordData = iterator.next();
                if (newUwb.getCode().equals(coordData.getCode())) {
                    BleDeviceInfo bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(coordData.getWristband().getBracelet_id());
                    if (bleDeviceInfo != null) {
                        LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                        iterator.remove();
                    }
                }
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

        if (!"".equals(temp.getTime()) && !"".equals(temp.getU_time())) {
            LinkDataManager.getInstance().cleanFlyBird(bleDeviceInfo);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
}
