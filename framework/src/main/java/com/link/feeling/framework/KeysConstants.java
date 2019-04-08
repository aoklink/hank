package com.link.feeling.framework;

/**
 * Created on 2019/1/3  18:54
 * chenpan pan.chen@linkfeeling.cn
 * <p>
 * 服务器请求字段
 */
@SuppressWarnings("unused")
public interface KeysConstants {

    // 字体
    String NUMBER_FONT = "number_font.ttf";

    // 网络设置相关
    String USER_AGENT = "User-Agent";
    String CONTENT_TYPE = "Content-Type";
    String APPLICATION = "application/x-www-form-urlencoded;charset=UTF-8\"";

    String LOGIN_STATUS = "loginStatus";

    // 手机号
    String PHONE_NUM = "phone_num";

    // 	(除登陆，注册时，上传健身数据时 不用带) 用户唯一识别
    String UID = "uid";

    // (商家平台，领客后台 请求数据接口时需要带上 ，需要通过cookie方式传给服务器）
    String SESSION_ID = "sessionid";

    // 	用户类型(私教:coach,健身者:trainee）
    String USER_TYPE = "user_type";

    // 	客户端当前系统时间,取毫秒
    String REQUEST_TIME = "request_time";

    // 	当前平台(小程序:small_program, 安卓:android,苹果:ios,网页:h5)
    String PLATFORM = "platform";

    // 	生成过程:md5(product_id + “:” + user_type + “:” + request_time)
    String TK = "tk";

    //  当前网络(3G,4G,5G,wifi)
    String NETWORK = "network";

    // 	产品id
    String PRODUCT_ID = "product_id";

    // 客户端版本号
    String APP_VERSION = "app_ver";


    String ANDROID = "android";

    String LINK_FEELING = "Linkfeeling";

    //  登陆方式(短信:sms,密码:pwd,微信:wx)
    String SMS = "sms";
    String PWD = "pwd";
    String WX = "wx";


    String REGISTER = "register";
    // 头像
    String AVATAR = "avatar";
    // 昵称
    String NAME = "name";
    // 锻炼时间
    String PLAY_TIME = "playTime";
    // 分类名称
    String CATEGORY_NAME = "category_name";
      // 开始时间
    String START_TIME = "start_time";
      // 结束时间
    String END_TIME = "end_time";

    // 取值:day,week,month,year
    String DAY = "day";
    String WEEK = "week";
    String MONTH = "month";
    String YEAR = "year";

    String ITEM_HEIGHT = "itemHeight";

}
