package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.link.feeling.framework.base.FrameworkBaseActivity;
import com.link.feeling.framework.executor.ThreadPoolManager;
import com.link.feeling.framework.utils.data.L;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.adapter.BLEAdapter;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;
import cn.linkfeeling.hankserve.factory.DataProcessorFactory;
import cn.linkfeeling.hankserve.interfaces.IAnchDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
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
import cn.linkfeeling.link_websocket.RxWebSocket;
import cn.linkfeeling.link_websocket.WebSocketSubscriber;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;
import okio.AsyncTimeout;
import okio.ByteString;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;

public class MainActivity extends FrameworkBaseActivity<IUploadContract.IBleUploadView, IUploadContract.IBleUploadPresenter> implements IUploadContract.IBleUploadView {
    private TextView tv_ipTip, tv_ipTipRemove;
    private Gson gson = new Gson();
    private SimpleDateFormat simpleDateFormat;
    private Disposable disposable;

    private RecyclerView recycleView;
    private BLEAdapter bleAdapter;

    private List<BleDeviceInfo> bleDeviceInfos = new ArrayList<>();
    private static final int Untied_Time = 150;

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
                    public void getBLEStream(SmartCarProtocol smartCarProtocol) {
                        onLeScanSelf(smartCarProtocol.getContent());
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

//                                if (!tempBleInfo.getSpeed().equals("") && Float.parseFloat(tempBleInfo.getSpeed()) == 0) {
//                                    LinkSpecificDevice linkSpecificDevice = LinkDataManager.getInstance().queryDeviceByName(tempBleInfo.getDevice_name());
//                                    if (linkSpecificDevice != null) {
//                                        int fenceId = linkSpecificDevice.getFencePoint().getFenceId();
//                                        //解除绑定
//                                        if (FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId)) {
//                                            FinalDataManager.getInstance().removeUwb(fenceId);
//                                        }
//                                    }
//
//                                }

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



    /**
     * 处理蓝牙广播数据
     *
     * @author zhangyong
     * @time 2019/3/20 14:55
     */
    private void onLeScanSelf(byte[] scanRecord) {
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

        Log.i("nnnnnnnnnnnnn", name);
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

        if (name.startsWith(LinkDataManager.ANCH)) {
            try {
                IAnchDataAnalysis anchDataAnalysis = (IAnchDataAnalysis) DataProcessorFactory.creteProcess(LinkDataManager.ANCH, name);
                anchDataAnalysis.analysisAnchData(scanRecord, name);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void connectWebSocket() {
        ThreadPoolManager.getInstance().execute(() -> {
            String url = "ws://47.111.183.148:8083/websocket/";
            String api_token = "projAdmin_fb84d0dbf481f46f8f760ab3092d9a64fe78f217";
            //  String api_token = "projAdmin_3eb3a71f555ff04d3088e4199987af58c3d1e029";
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

   /* private void writeQueue(UWBCoordData uwbCoordData) {
        UwbQueue<Point> points = FinalDataManager.getInstance().getCode_points().get(uwbCoordData.getCode());
        if (points == null) {
            UwbQueue<Point> uwbQueue = new UwbQueue<>(25);
            Point point = new Point();
            if (uwbCoordData.getDevice() == null) {
                point.setId(-1);
            } else {
                point.setId(uwbCoordData.getDevice().getId());
            }
            point.setX(uwbCoordData.getX());
            point.setY(uwbCoordData.getY());
            uwbQueue.offer(point);
            FinalDataManager.getInstance().getCode_points().put(uwbCoordData.getCode(), uwbQueue);
        } else {
            Point point = new Point();
            if (uwbCoordData.getDevice() == null) {
                point.setId(-1);
            } else {
                point.setId(uwbCoordData.getDevice().getId());
            }
            point.setX(uwbCoordData.getX());
            point.setY(uwbCoordData.getY());
            points.offer(point);
        }
    }*/


    @Override
    public void uploadBleStatus(BleDeviceInfo temp,BleDeviceInfo bleDeviceInfo, boolean status, Throwable throwable) {
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
