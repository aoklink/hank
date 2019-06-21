package cn.linkfeeling.hankserve.factory;


import java.util.concurrent.ConcurrentHashMap;

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


    public static IDataAnalysis creteProcess(String type, String name) {
        switch (type) {

            case LinkDataManager.TYPE_LEAP:
                return WristbandProcessor.getInstance();

            case LinkDataManager.TREADMILL_1:
                ConcurrentHashMap<String, TreadMillProcessor> treadMillMap = TreadMillProcessor.map;
                if (treadMillMap != null) {
                    TreadMillProcessor treadMillProcessor = treadMillMap.get(name);
                    if (treadMillProcessor != null) {
                        return treadMillProcessor;
                    } else {
                        TreadMillProcessor treadMillProcessor1 = new TreadMillProcessor();
                        TreadMillProcessor.map.put(name, treadMillProcessor1);
                        return treadMillProcessor1;
                    }
                }
                return null;

            case LinkDataManager.BICYCLE_1:
                ConcurrentHashMap<String, BicycleProcessor> bicycleMap = BicycleProcessor.map;
                if (bicycleMap != null) {
                    BicycleProcessor bicycleProcessor = bicycleMap.get(name);
                    if (bicycleProcessor != null) {
                        return bicycleProcessor;
                    } else {
                        BicycleProcessor bicycleProcessor1 = new BicycleProcessor();
                        BicycleProcessor.map.put(name, bicycleProcessor1);
                        return bicycleProcessor1;
                    }
                }
                return null;

            case LinkDataManager.OVAL_1:
                ConcurrentHashMap<String, OvalProcessor> ovalMap = OvalProcessor.map;
                if (ovalMap != null) {
                    OvalProcessor ovalProcessor = ovalMap.get(name);
                    if (ovalProcessor != null) {
                        return ovalProcessor;
                    } else {
                        OvalProcessor ovalProcessor01 = new OvalProcessor();
                        OvalProcessor.map.put(name, ovalProcessor01);
                        return ovalProcessor01;
                    }
                }
                return null;
            case LinkDataManager.BIRD_1:

                ConcurrentHashMap<String, FlyBirdProcessor> birdMap = FlyBirdProcessor.map;
                if (birdMap != null) {
                    FlyBirdProcessor flyBirdProcessor = birdMap.get(name);
                    if (flyBirdProcessor != null) {
                        return flyBirdProcessor;
                    } else {
                        FlyBirdProcessor flyBirdProcessorNew = new FlyBirdProcessor();
                        FlyBirdProcessor.map.put(name, flyBirdProcessorNew);
                        return flyBirdProcessorNew;
                    }
                }
                return null;


            default:
                return null;
        }
    }
}
