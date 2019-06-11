package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;
import cn.linkfeeling.hankserve.factory.DataProcessorFactory;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.ui.IUploadContract;
import cn.linkfeeling.hankserve.ui.UploadPresenter;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.link_socketserve.NettyServer;
import cn.linkfeeling.link_socketserve.bean.ScanData;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;
import cn.linkfeeling.link_websocket.RxWebSocket;
import cn.linkfeeling.link_websocket.WebSocketSubscriber;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;
import okio.ByteString;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;

public class MainActivity extends FrameworkBaseActivity<IUploadContract.IBleUploadView, IUploadContract.IBleUploadPresenter> implements IUploadContract.IBleUploadView {
    private List<UWBCoordData> list = new ArrayList<>();
    private TextView tv_ipTip, tv_ipTipRemove, receiver_ip;
    private Gson gson = new Gson();
    private SimpleDateFormat simpleDateFormat;

    private RecyclerView recycleView;
    private BLEAdapter bleAdapter;

    private List<BleDeviceInfo> bleDeviceInfos = new ArrayList<>();
    private Disposable disposable;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        tv_ipTip = findViewById(R.id.tv_ipTip);
        tv_ipTipRemove = findViewById(R.id.tv_ipTipRemove);
        receiver_ip = findViewById(R.id.receiver_ip);
        tv_ipTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_ipTipRemove.setMovementMethod(ScrollingMovementMethod.getInstance());

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");


        recycleView = findViewById(R.id.recycleView);


        if (!App.getApplication().isStart()) {
            startServer();
        }
        //   UDPBroadcast.udpBroadcast(this);
        connectWebSocket();

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        bleAdapter = new BLEAdapter(this, bleDeviceInfos);
        recycleView.setAdapter(bleAdapter);


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
                    public void getSubjectData(ScanData data, String ip) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                receiver_ip.setText(ip);
                            }
                        });
                        ThreadPoolManager.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                onLeScanSelf(data.getName(), data.getRssi(), data.getScanRecord());
                            }
                        });


                    }
                });
            }
        });


    }


    private void startIntervalListener() {
        if(disposable==null){
            disposable = Observable.interval(INTERVAL_TIME, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        if (FinalDataManager.getInstance().getWristbands() != null && !FinalDataManager.getInstance().getWristbands().isEmpty()) {
                            Log.i("Disposable", "Disposable");
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


                                getPresenter().uploadBleData(value);

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
    private void onLeScanSelf(String name, int rssi, byte[] scanRecord) {

        if (name == null) {
            return;
        }

        Log.i("11111111111111", name + name);

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

    private void connectWebSocket() {
        ThreadPoolManager.getInstance().execute(() -> {
            String url = "ws://47.111.183.148:8083/websocket/";
            String api_token = "projAdmin_fb84d0dbf481f46f8f760ab3092d9a64fe78f217";
            String project_name = BuildConfig.PROJECT_NAME;
            StringBuilder builder = new StringBuilder(url);
            builder.append(project_name);
            builder.append("_0");
            builder.append("_2D");
            builder.append(api_token);
            builder.append("_type|coord");
            createWsConnect(builder.toString());
        });
    }

    private void createWsConnect(String url) {
        RxWebSocket.get(url).subscribe(new WebSocketSubscriber() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
            }

            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                super.onOpen(webSocket);
                L.i("========", "open");
            }

            @Override
            protected void onMessage(@NonNull String text) {
                super.onMessage(text);
                L.i("========", "onMessageString---" + text);
                dealMessage(text);

            }

            @Override
            protected void onMessage(@NonNull ByteString byteString) {
                super.onMessage(byteString);
            }

            @Override
            protected void onReconnect() {
                super.onReconnect();
                L.i("========", "onReconnect");
            }


            @Override
            protected void onClose() {
                super.onClose();
                L.i("========", "onClose");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                L.i("========", "onError");
            }
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
        boolean within = withinTheScope(newUwb);
        if (within) {
            int fenceId = newUwb.getDevice().getFencePoint().getFenceId();
            if (FinalDataManager.getInstance().getFenceId_uwbData().get(fenceId) != null) {
                return;
            }
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

    private boolean withinTheScope(UWBCoordData uwbCoorData) {

        double x = uwbCoorData.getX();
        double y = uwbCoorData.getY();

        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().getDevicesData();
        for (LinkSpecificDevice devicesDatum : devicesData) {
            UWBCoordData.FencePoint fencePoint = devicesDatum.getFencePoint();
            double x1 = fencePoint.getLeft_top().getX();
            double y1 = fencePoint.getLeft_top().getY();
            double x2 = fencePoint.getRight_bottom().getX();
            double y2 = fencePoint.getRight_bottom().getY();

            if ((x > x1 && x < x2) && (y > y1 && y < y2)) {
                uwbCoorData.setDevice(devicesDatum);
                uwbCoorData.setWristband(new Wristband(LinkDataManager.getInstance().getUwbCode_wristbandName().get(uwbCoorData.getCode())));
                return true;
            }

        }
        return false;
    }

    @Override
    public void uploadBleStatus(BleDeviceInfo bleDeviceInfo, boolean status, Throwable throwable) {
        try {
            if (!status) {

                String s = gson.toJson(bleDeviceInfo);
                L.i("wwwwwwwwwwww", s);
                L.i("wwwwwwwwwwww", throwable.getMessage());
                bleDeviceInfo.setReport(false);
                updateData(bleDeviceInfo);

            } else {
                String s = gson.toJson(bleDeviceInfo);
                L.i("ffffffffffff", s);
                bleDeviceInfo.setReport(true);
                updateData(bleDeviceInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!"".equals(bleDeviceInfo.getTime())) {

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
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

}
