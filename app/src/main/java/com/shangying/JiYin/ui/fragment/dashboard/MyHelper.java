package com.shangying.JiYin.ui.fragment.dashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 创建日期：2021/6/12 22:27
 * @author 林凯
 * 文件名称： MyHelper.java
 * 类说明： 操作 Sqlite 数据库的类
 */
public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(@Nullable Context context) {
        super(context, "jacklin_map.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户名，密码，性别，婚否，爱好
        // u_id     u_name    u_phone      u_password
        db.execSQL("CREATE TABLE userinfo(u_id INTEGER PRIMARY KEY AUTOINCREMENT, u_name VARCHAR(20), u_phone VARCHAR(20), u_password VARCHAR(20))");

//        # 动态表
//        d_id	  u_id		title	  content	  privacy		c_time
//        动态id	用户id	  标题	  内容		 隐私权限		创建时间
        db.execSQL("CREATE TABLE dynamic(d_id INTEGER PRIMARY KEY AUTOINCREMENT, u_id VARCHAR(20), title VARCHAR(20), content VARCHAR(250), privacy VARCHAR(20), c_time VARCHAR(20))");

//        # 评论表
//        c_id	         u_id	           d_id				 f_id	       		  content	    c_time
//        评论id	发表评论的用户id    评论对应的动态id	  评论的父id，没有则为-1   评论内容       评论时间
        db.execSQL("CREATE TABLE comment(c_id INTEGER PRIMARY KEY AUTOINCREMENT, u_id VARCHAR(20), d_id VARCHAR(20), f_id VARCHAR(20), content VARCHAR(20), c_time VARCHAR(20))");


        // 图片表：主要用来存储头像和动态中的图片
        // i_id         d_id                                                 u_id                           图片对应的2进制
        // 图片id       动态id（如果该图片是头像，则置为-1）      用户头像id（如果该图片是动态中的图片，则置为-1）   bitmap
        db.execSQL("create table images(i_id integer primary key autoincrement, d_id varchar(20), u_id varchar(20), bitmap blob)");

        // 运动数据表，这里用 text 来存储时间格式化的字符串，方便后面根据时间查询，之前没有想到，前面已经设计的表就不更改了
        // p_id     u_id        path        c_time
        // 运动轨迹id   用户id    运动轨迹    创建时间
        db.execSQL("create table mypath(p_id integer primary key autoincrement, u_id varchar(20), path blob, c_time text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println();
    }

       /*
        insert(){}
        query(){}
        update(){}
        delete(){}
        */

}


