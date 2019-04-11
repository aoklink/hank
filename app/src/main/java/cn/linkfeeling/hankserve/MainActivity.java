package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ScrollView;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;
import cn.linkfeeling.hankserve.factory.DataProcessorFactory;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
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
    private ConcurrentHashMap<String, BleDeviceInfo> wristbands = new ConcurrentHashMap<>();  //手环对应的集合   key为手环名称   value为整合后的数据（最终上传数据）
    private ConcurrentHashMap<Integer, UWBCoordData> fenceId_uwbData = new ConcurrentHashMap<>();//围栏id uwb设备对应关系  key为围栏id  value为uwb对象

    private List<UWBCoordData> list = new ArrayList<>();
    private TextView tv_ipTip, tv_logCat, tv_ipTipRemove;
    private ScrollView scrollView;
    private Gson gson = new Gson();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        tv_ipTip = findViewById(R.id.tv_ipTip);
        tv_ipTipRemove = findViewById(R.id.tv_ipTipRemove);
        tv_ipTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_ipTipRemove.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_logCat = findViewById(R.id.tv_logCat);
        scrollView = findViewById(R.id.scrollView);


        if (!App.getApplication().isSatrt()) {
            startServer();
        }

        connectWebSocket();
    }


    private void startServer() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                App.getApplication().setSatrt(true);
                NettyServer.getInstance().bind(new SocketCallBack() {
                    @Override
                    public void connectSuccess(String ip) {
                        tv_ipTip.append(ip + "连接成功！！\n"+ new SimpleDateFormat("MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                        tv_ipTip.append("\n\n");
                    }

                    @Override
                    public void disconnectSuccess(String ip) {
                        tv_ipTipRemove.append(ip + "断开连接！！\n" + new SimpleDateFormat("MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                        tv_ipTipRemove.append("\n\n");
                    }

                    @Override
                    public void getSubjectData(ScanData data) {
                        Log.i("server_receive_data", gson.toJson(data));

                        onLeScanSelf(data.getName(), data.getRssi(), data.getScanRecord());
                    }
                });
            }
        });

        startIntervalListener();
    }


    private void startIntervalListener() {
        Disposable disposable = Observable.interval(INTERVAL_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(aLong -> {
                    if (wristbands != null && !wristbands.isEmpty()) {
                        for (Map.Entry<String, BleDeviceInfo> entry : wristbands.entrySet()) {
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

                            //   getPresenter().uploadBleData(value);
                            runOnUiThread(() -> addText(tv_logCat, s));
                        }
                    }
                });
    }

    /**
     * 处理蓝牙广播数据
     *
     * @author zhangyong
     * @time 2019/3/20 14:55
     */
    private void onLeScanSelf(String name, int rssi, byte[] scanRecord) {

        Log.i("11111111111111", name + name);

        if (name.contains(LinkDataManager.TYPE_LEAP)) {
            try {
                BleDeviceInfo bleDeviceInfo;
                if (wristbands.get(name) != null) {
                    bleDeviceInfo = wristbands.get(name);
                } else {
                    bleDeviceInfo = new BleDeviceInfo();
                    LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                }
                IDataAnalysis leap = DataProcessorFactory.creteProcess(LinkDataManager.TYPE_LEAP);
                BleDeviceInfo bleDeviceFinal = leap.analysisBLEData(bleDeviceInfo, scanRecord, name);
                if (bleDeviceFinal == null) {
                    return;
                }
                wristbands.put(name, bleDeviceFinal);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String bleType = LinkDataManager.getInstance().getDeviceBleTypeMaps().get(name);
        if (bleType != null) {

            try {
                int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(name);
                boolean containsKey = fenceId_uwbData.containsKey(fenceId);
                if (!containsKey) {
                    return;
                }
                UWBCoordData uwbCoordData = fenceId_uwbData.get(fenceId);

                String bracelet_id = uwbCoordData.getWristband().getBracelet_id();
                BleDeviceInfo bleDeviceInfoNow = wristbands.get(bracelet_id);
                if (bleDeviceInfoNow == null) {
                    return;
                }
                IDataAnalysis iDataAnalysis = DataProcessorFactory.creteProcess(bleType);
                BleDeviceInfo bleDeviceInfoFinal = iDataAnalysis.analysisBLEData(bleDeviceInfoNow, scanRecord, name);
                if (bleDeviceInfoFinal == null) {
                    return;
                }
                wristbands.put(bracelet_id, bleDeviceInfoFinal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void connectWebSocket() {
        ThreadPoolManager.getInstance().execute(() -> {
            String url = "ws://192.168.50.119:8083/websocket/";
            String api_token = "projAdmin_fb84d0dbf481f46f8f760ab3092d9a64fe78f217";

            StringBuilder builder = new StringBuilder(url);
            builder.append("linkfeeling01");
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
            if (fenceId_uwbData.get(fenceId) != null) {
                return;
            }
            if (fenceId_uwbData.containsValue(newUwb)) {
                LinkSpecificDevice newDevice = newUwb.getDevice();
                Iterator iterator = fenceId_uwbData.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    UWBCoordData oldUwbValue = (UWBCoordData) entry.getValue();
                    Integer oldUwbKey = (Integer) entry.getKey();
                    if (oldUwbKey != newDevice.getFencePoint().getFenceId() && oldUwbValue.getCode().equals(newUwb.getCode())) {
                        //运动过程中 uwb偏移了
                        if (oldUwbValue.getDevice().getAbility() != 0) {
                            return;
                        }
                        BleDeviceInfo bleDeviceInfo = wristbands.get(oldUwbValue.getWristband().getBracelet_id());
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
            BleDeviceInfo bleDeviceInfo = wristbands.get(newUwb.getWristband().getBracelet_id());
            if (bleDeviceInfo != null) {
                //如果是hiit区域
                if (newUwb.getDevice().getDeviceName().equals("hiit")) {
                    if (!list.contains(newUwb)) {
                        list.add(newUwb);
                    }
                } else {
                    fenceId_uwbData.put(newUwb.getDevice().getFencePoint().getFenceId(), newUwb);
                }
                bleDeviceInfo.setDevice_name(newUwb.getDevice().getDeviceName());
            }
        }


        if (!within) {
            if (fenceId_uwbData.containsValue(newUwb)) {
                Iterator iterator = fenceId_uwbData.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    UWBCoordData oldUwbValue = (UWBCoordData) entry.getValue();
                    if (oldUwbValue.getDevice().getAbility() != 0) {
                        return;
                    }
                    if (oldUwbValue.getCode().equals(newUwb.getCode())) {
                        BleDeviceInfo bleDeviceInfo = wristbands.get(oldUwbValue.getWristband().getBracelet_id());
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
                    BleDeviceInfo bleDeviceInfo = wristbands.get(coordData.getWristband().getBracelet_id());
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
    public void uploadBleStatus(BleDeviceInfo bleDeviceInfo, boolean status) {
        LinkDataManager.getInstance().cleanFlyBird(bleDeviceInfo);
        if (!status) {
            //todo 上传失败做后续操作
        }
    }

    @Override
    public IUploadContract.IBleUploadPresenter createPresenter() {
        return new UploadPresenter();
    }

    int line;

    //添加日志
    private void addText(TextView textView, String content) {
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        if (line == 500) {
            textView.setText(null);
            line = 0;
        }
        textView.append(content);
        line++;
        textView.append("\n\n");
    }
}
