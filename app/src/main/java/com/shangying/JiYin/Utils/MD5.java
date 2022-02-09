package com.shangying.JiYin.Utils;

import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Email: shangying611@gmail.com
 * Blog:  https://shangying.host/
 * Date: 2021/10/6.
 * Time: 11:27.
 * Explain:MD5加密工具类   服务器端也使用这个加密类
 */
public class MD5 {
    public  String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

}
