package cn.linkfeeling.hankserve.utils;

import java.math.BigDecimal;

/**
 * @author create by zhangyong
 * @time 2019/3/19
 */
public class CalculateUtil {

    /**
     * TODO 除法运算，保留小数
     *
     * @param a 被除数
     * @param b 除数
     * @return 商
     */
    public static float txFloat(int a, int b) {
        // TODO 自动生成的方法存根

        return (float) a / b;
    }


    /**
     * 两个float类型数据做除法，保留7位小数  防止出现科学计数法
     *
     * @author zhangyong
     * @time 2019/3/25 14:03
     */
    public static BigDecimal floatDivision(float num1, float num2) {
        BigDecimal bigDecimal1 = new BigDecimal(num1);
        BigDecimal bigDecimal2 = new BigDecimal(num2);
        BigDecimal divide = bigDecimal1.divide(bigDecimal2, 7, BigDecimal.ROUND_HALF_UP);
        return divide;


    }

    /**
     * byte[]转int
     *
     * @param
     * @return
     */
//byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

}
