package com.shangying.JiYin.Utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Date: 2021/5/18$.
 * Time: 17:07$.
 * Explain:  用户输入判断工具类
 */
public class Choice {
    //判断邮箱
    public static boolean isEmail(EditText email){
        email.getText().toString().trim();
        if ("".equals(email)) {
            return false;
        }
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email.getText().toString().trim());
        return m.matches();
    }
//    判断非空nNull
    public static boolean nNull(EditText str){

        if ("".equals(str.getText().toString().trim())) {
            return false;
        }
        return true;
    }
    //    判断长度len
    public static boolean len (EditText str){
        if (str.getText().toString().trim().length()>7) {
            return true;
        }
        return false;
    }
    //    判断相等identical
    public static boolean identical (EditText str,EditText strs){
        if (str.getText().toString().trim().equals(strs.getText().toString().trim())) {
            return true;
        }
        return false;
    }

}
