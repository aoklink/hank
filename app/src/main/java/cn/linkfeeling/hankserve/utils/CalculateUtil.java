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
     * 将4字节的byte数组转成一个int值
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        byte[] a = new byte[4];
        int i = a.length - 1, j = b.length - 1;
        for (; i >= 0; i--, j--) {//从b的尾部(即int值的低位)开始copy数据
            if (j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }


    /**
     * int to byte
     * @param number
     * @return
     */
    public static int toByte(int number) {
        int tmp = number & 0xff;
        if ((tmp & 0x80) == 0x80) {
            int bit = 1;
            int mask = 0;
            for(;;) {
                mask |= bit;
                if ((tmp & bit) == 0) {
                    bit <<=1;
                    continue;
                }
                int left = tmp & (~mask);
                int right = tmp & mask;
                left = ~left;
                left &= (~mask);
                tmp = left | right;
                tmp = -(tmp & 0xff);
                break;
            }
        }
        return tmp;
    }

}
