package cn.linkfeeling.link_socketserve;

import java.util.Arrays;

import cn.linkfeeling.link_socketserve.bean.ScanData;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;

/**
 * @author create by zhangyong
 * @time 2019/5/25
 */
public class Test {
    public static void main(String args[]){


//        byte[] kk={(byte)0xAA,(byte)0x55};
//        System.out.println(getShort(kk));

//        byte[] bytes = putShort((short) 0xAA55);
//        System.out.println(Arrays.toString(bytes));

        short aa= (short) 0x55AA;

//        byte[] bytes = putShort(aa);
//        System.out.println(Arrays.toString(bytes));
//
//        System.out.println(getShort(bytes));
        System.out.println(aa);









    }

    public static short getShort(byte[] bytes)
    {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static byte[] putShort( short s) {
        byte[] b=new byte[2];
        b[1] = (byte) (s >> 8);
        b[0] = (byte) (s >> 0);

        return b;
    }
}
