package cn.linkfeeling.hankserve.mqtt;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.link.feeling.framework.KeysConstants;
import com.link.feeling.framework.base.BaseApplication;

import com.link.feeling.framework.bean.MqttRequest;
import com.link.feeling.framework.utils.data.DeviceUtils;
import com.link.feeling.framework.utils.data.L;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import cn.linkfeeling.hankserve.BuildConfig;

/**
 * Created on 2019/10/30  11:29
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class MqttManager {

    private static final String TAG = "MqttManager";

    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;


    public static MqttManager newInstance() {
        return new MqttManager();
    }


    public void connect(MqttCallbackExtended callback){
        if (mqttAndroidClient == null) {
            mqttAndroidClient = new MqttAndroidClient(BaseApplication.getAppContext(), KeysConstants.SERVER_URL, KeysConstants.GID + DeviceUtils.getMac());
            mqttAndroidClient.setCallback(callback);
        }
        if (mqttConnectOptions == null) {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setKeepAliveInterval(15);
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setMaxInflight(1000);
            try {
                mqttConnectOptions.setUserName(KeysConstants.SIGNATURE + KeysConstants.ACCESS_KEY + KeysConstants.SEPARATOR + KeysConstants.INSTANCE_ID);
                mqttConnectOptions.setPassword(Tool.macSignature(KeysConstants.GID + DeviceUtils.getMac(), KeysConstants.SECRET_KEY).toCharArray());
            } catch (Exception e) {
                L.e(TAG, "exception:setPassword", e);
            }
        }


        try {
            mqttAndroidClient.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void subscribeToTopic() {
        try {
            final String topicFilter[] = {KeysConstants.TOPIC + BuildConfig.GYM_NAME};
            final int[] qos = {1};


            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    L.e(TAG, "subscribe:success");
                    publishMessage(JSON.toJSONString(new MqttRequest(1, BuildConfig.GYM_NAME)));
                    publishMessage(JSON.toJSONString(new MqttRequest(2, BuildConfig.GYM_NAME)));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    L.e(TAG, "subscribe:failed", exception);
                }
            });

        } catch (MqttException ex) {
            L.e(TAG, "subscribe:exception", ex);
        }
    }


    public void publishMessage(String json) {
        try {
            MqttMessage message = new MqttMessage();
            final String msg = json;
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(KeysConstants.TOPIC_FATHER, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    L.e(TAG, "publish:success:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    L.e(TAG, "publish:failed:" + msg);
                }
            });
        } catch (MqttException e) {
            L.e(TAG, "publish:exception", e);
        }
    }

    public boolean connectStatus() {
        if (mqttAndroidClient != null) {
            return mqttAndroidClient.isConnected();
        }
        return false;
    }

    public void reConnect() throws MqttException {
        if(mqttAndroidClient!=null && !connectStatus()){
                Log.i("333333333333333","点击重启");
                mqttAndroidClient.connect(mqttConnectOptions);

        }
    }

    public void disConnect(){
        try {

            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
