package cn.linkfeeling.hankserve.factory;


import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.subjects.BicycleProcessor;
import cn.linkfeeling.hankserve.subjects.FlyBirdProcessor;
import cn.linkfeeling.hankserve.subjects.OvalProcessor;
import cn.linkfeeling.hankserve.subjects.TreadMillProcessor;
import cn.linkfeeling.hankserve.subjects.WristbandProcessor;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 */
public class DataProcessorFactory {


    public static IDataAnalysis creteProcess(String type) {
        switch (type) {

            case LinkDataManager.TYPE_LEAP:
                return WristbandProcessor.getInstance();

            case LinkDataManager.TREADMILL_1:
                return TreadMillProcessor.getInstance();

            case LinkDataManager.BICYCLE_1:
                return BicycleProcessor.getInstance();

            case LinkDataManager.OVAL_1:
                return OvalProcessor.getInstance();

            case LinkDataManager.BIRD_1:
                return FlyBirdProcessor.getInstance();


            default:
                return null;
        }
    }
}
