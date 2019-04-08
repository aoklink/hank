package com.link.feeling.framework.utils.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 2019/1/3  19:45
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class MD5Utils {


    public static String toMd5(String s) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        md5.update(s.getBytes());
        byte[] md5Bytes = md5.digest();
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
        }
        return hexValue.toString();
    }

    //生成32位MD5
    public static String to32Md5(String plainText) {
        String re_md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuilder buf = new StringBuilder();
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * 加密
     * */
    public static String to16Md5(String srcText){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(srcText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder();
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
