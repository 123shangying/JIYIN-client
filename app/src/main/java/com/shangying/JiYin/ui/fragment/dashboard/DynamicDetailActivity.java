package com.shangying.JiYin.ui.fragment.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;

import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 创建日期：2021/6/13 21:47
 *
 * @author shangying
 * 文件名称： DynamicDetailActivity.java
 * 类说明： 动态详情页面，用来展示用户发布的动态的详细信息
 */
public class DynamicDetailActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  //    插入评论数据
     */
    private final static String INSCOMM = MyApplication.S_IP + "comment/inscomm";
    /**
     *  //    查询动态对应评论
     */
    private final static String COMM = MyApplication.S_IP + "comment/showconn";
    /**
     * 查看评论个数
     */
    private final static String SIZE = MyApplication.S_IP + "comment/connsize";
    /**
     *  //     根据uid查询用户名
     */
    private final static String UNAME = MyApplication.S_IP + "user/uname";
    /**
     * 查看用户头像
     */
    private final static String ICON = MyApplication.S_IP+"icon/showicon";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    /**
     * 存放用户名
     */
    public static ArrayList<String> UUID= new ArrayList<>();
    /**
     * 存放评论内容
     */
    public static ArrayList<String> CCOMM= new ArrayList<>();
    /**
     * 存放评论时间
     */
    public static ArrayList<String> TTIM= new ArrayList<>();
    /**
     * 存放父评论id
     */
    public static ArrayList<Integer> FDID= new ArrayList<>();
    /**
     * 存放用户id
     *
     */
    public static ArrayList<String> IMAGE= new ArrayList<String>();
    ImageView iv_back, iv_share;

    ListView listView;
    /**
     * 接收条目值,传递显示动态的位置
     */
    static int CCon;
    /**
     * 评论数的大小，用来初始化评论数量
     */
   static   int Size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        // Android 4.0 之后不能在主线程中请求HTTP请求
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //接收前面传递的did,用来查询所属条目
        Intent intent = getIntent();
        int did=intent.getIntExtra("did",0);
//获取评论个数did
        getOne(did);
//  获取所有评论did
         getShowaC(did);
        // 初始化控件
        initWidget();
        // 初始化数据
        initData();
    }

    private void initData() {
        System.out.println(UUID.size()+"大小");
        Intent intent = getIntent();
//        接收动态索引
        CCon= intent.getIntExtra("data",0);

        // 3.为 ListView 设置 Adapter，创建自定义Adapter，继承BaseAdapter 因为 baseAdapter 是一个抽象类
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter();
        listView = findViewById(R.id.dynamic_detail_listview);
        listView.setAdapter(customBaseAdapter);
        // 设置 ListView 的 item 项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
    }



    private void initWidget() {
        System.out.println(UUID.size()+"UUID.size()");
        System.out.println(UUID);
        // 初始化控件
        iv_back = findViewById(R.id.dynamic_detail_back);
        iv_share = findViewById(R.id.dynamic_detail_share);


        // 添加监听事件
        iv_back.setOnClickListener(this::onClick);
        iv_share.setOnClickListener(this::onClick);


    }

    // 定义一个内部类，继承自 BaseAdapter，并重写其方法
    private class CustomBaseAdapter extends BaseAdapter {

        final int TYPE_1 = 0;
        final int TYPE_2 = 1;

        @Override
        public int getCount() {
            if (Size == 0) {
                return 2;
            } else {
                return Size;
            }

        }

        @Override
        public Object getItem(int i) {

                return UUID.get(i);

        }

        @Override
        public int getItemViewType(int position) {
            // 为不同的 item 设置不同的布局
            if (position == 0) {
                return TYPE_1;
            } else {
                return TYPE_2;
            }
        }

        @Override
        public int getViewTypeCount() {
            // 共有2中布局
            return 2;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // 这个方法事件我们 ListView 里面为 item 定义的样式文件转换成为一个 View 对象
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            int type = getItemViewType(i);
            switch (type) {
                case TYPE_1: {
                    // ListView 的第一个 item，用来展示动态的具体内容

                    ViewHolderFirst holderFirst;
                    if (view == null) {
                        // 设置 item 的布局文件
                        view = View.inflate(DynamicDetailActivity.this, R.layout.listview_item_dynamic_detail_info, null);
                        holderFirst = new ViewHolderFirst();

                        // 实例化 list_item 中的 TextView 控件  注意这里一定是 view.findViewById() 不能直接 findViewById()
                        holderFirst.tv_uname = (TextView) view.findViewById(R.id.dynamic_detail_uname);      // 动态发布者的用户名
                        holderFirst.tv_title = (TextView) view.findViewById(R.id.dynamic_detail_tv_title);      // 动态标题
                        holderFirst.tv_time = (TextView) view.findViewById(R.id.dynamic_detail_time);      // 动态标题
                        holderFirst.tv_content = (TextView) view.findViewById(R.id.dynamic_detail_info_tv_content);      // 动态内容
                        holderFirst.linearLayout01 = view.findViewById(R.id.dynamic_detail_linearlayout_01);    // 放置图片的 linearlayout
                        holderFirst.linearLayout02 = view.findViewById(R.id.dynamic_detail_linearlayout_02);    // 放置图片的 linearlayout

                        // item 中的 button 控件，EditView 控件
                        holderFirst.et_content = (EditText) view.findViewById(R.id.dynamic_detail_et_content);      // 评论文本内容
                        holderFirst.btn_clear = (Button) view.findViewById(R.id.dynamic_detail_btn_clean);        // 清空评论按钮
                        holderFirst.btn_comment = (Button) view.findViewById(R.id.dynamic_detail_btn_comment);    // 发布评论按钮
                        holderFirst.profile_image =  view.findViewById(R.id.pprofile_image);    // 发布评论按钮

                        view.setTag(holderFirst);
                    } else {
                        holderFirst = (ViewHolderFirst) view.getTag();
                    }
                    try {
                        // 为 txtView 赋值（展示动态具体内容）,从 CommunityFragment 中获取传递过来的动态详情信息
                        holderFirst.tv_uname.setText(DashboardFragment.Uid.get(CCon));     // 动态发布者用户名
                        holderFirst.tv_title.setText(DashboardFragment.Title.get(CCon));     // 动态标题
                        holderFirst.tv_content.setText(DashboardFragment.Content.get(CCon));     // 动态内容
                        //得到可用的图片
                        if(IMAGE.size()==0){

                        }else {
                            String url = "https://tu.shangying.xyz/imgs/2021/10/1%20("+DashboardFragment.IMAGE.get(CCon)+").png";
                            System.out.println(url+"--URL");
                            Bitmap bitmap = getHttpBitmap(url);

                            holderFirst.profile_image.setImageBitmap(bitmap);
                        }
                    }catch (Exception e){

                    }

                    holderFirst.tv_time.setText(DashboardFragment.GmtModified.get(CCon));        // 动态发布时间

                    // 为清空评论按钮，发布评论按钮添加监听事件
                    holderFirst.btn_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holderFirst.et_content.setText("");
                        }
                    });

                    // 发布评论内容
                    holderFirst.btn_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //没有父评论的评论
                            insConn(DashboardFragment.ID.get(CCon),DashboardFragment.Did.get(CCon),-1,holderFirst.et_content.getText().toString());
                            Toast.makeText(getApplicationContext(), "发布成功！", Toast.LENGTH_SHORT).show();
                            holderFirst.et_content.setText("");
                        }
                    });

                    break;
                }
                case TYPE_2: {
                    // 后续的 item，用来展示 评论
                    // 优化的 BaseAdapter，使用 ViewHolder
                    ViewHolderSecond holderSecond;
                    if (view == null) {
                        // 设置 item 的布局文件
                        view = View.inflate(DynamicDetailActivity.this, R.layout.listview_item_dynamic_detail_comment, null);
                        holderSecond = new ViewHolderSecond();

                        // 实例化 list_item 中的 TextView 控件
                        holderSecond.tv_uname = (TextView) view.findViewById(R.id.list_item_comment_uname);
                        holderSecond.tv_time = (TextView) view.findViewById(R.id.list_item_comment_time);
                        holderSecond.tv_content = (TextView) view.findViewById(R.id.list_item_comment_content);
                        holderSecond.profile_image =  view.findViewById(R.id.rofile_image);

                        view.setTag(holderSecond);

                    } else {
                        holderSecond = (ViewHolderSecond) view.getTag();
                    }
                        try {


                    // 特别注意：这里因为第1项不是评论，所以要写成 i - 1
                    // 这里detalist.get(i) 获得的是 MapItem，存放的是评论信息
                    holderSecond.tv_uname.setText(UUID.get(i));
                    holderSecond.tv_content.setText(CCOMM.get(i));
                    holderSecond.tv_time.setText(TTIM.get(i));
                            //得到可用的图片
                            if(IMAGE.size()==0){

                            }else {
                                String url = "https://tu.shangying.xyz/imgs/2021/10/1%20("+IMAGE.get(i)+").png";
                                System.out.println(url+"--URL");
                                Bitmap bitmap = getHttpBitmap(url);

                                holderSecond.profile_image.setImageBitmap(bitmap);
                            }

                    }catch (Exception e){}
                    // 同时在这里添加 list_item 的监听事件
                    holderSecond.tv_content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    });
                    break;

                }
                default:
            }

            // 把 item 对应的布局转换为 View 对象之后，最后记得返回
            return view;
        }



        // 特别注意，在 list_item 里面的控件要在这里声明，然后再实例化

        // ListView 的一个 item 的控件的资源，用来展示动态详情
        class ViewHolderFirst {
            // 动态详情：发布动态的用户名，动态标题，动态内容
            TextView tv_uname, tv_title, tv_time, tv_content;
            // 评论文本内容
            EditText et_content;
            // 清空评论按钮，发布评论按钮
            Button btn_clear, btn_comment;

            LinearLayout linearLayout01, linearLayout02;
            ImageView profile_image;

        }
        // listView 的后续的 item 的控件资源，用来展示评论
        class ViewHolderSecond {
            TextView tv_uname;
            TextView tv_time;
            TextView tv_content;        // 评论内容
            ImageView profile_image;
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dynamic_detail_back:
                // 返回按钮：销毁当前活动
                finish();
                break;
            case R.id.dynamic_detail_share:
                // 分享按钮：
                Intent intent=new Intent(Intent.ACTION_SEND);
                //分享发送的数据类型
                intent.setType("text/plain");
                //分享的主题
                intent.putExtra(Intent.EXTRA_SUBJECT, "SHARE FUNNY GAME");
                intent.putExtra(Intent.EXTRA_TEXT,
                                " 我正在使用迹印!   分享我的动态！赶快下载迹印和我一起互动吧！" +"\n"+
                                        "点击复制下载链接:"+"\n"+
                                "https://sport.shangying.xyz/app/app-debug.apk");
                              //  getApplicationContext().getPackageName());  //分享的内容
                //在额外的进程中开启分享的程序
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //弹出的窗口设置
                startActivity(Intent.createChooser(intent,getTitle()+" SHARE"));
                Toast.makeText(getApplicationContext(), "暂未开放...", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

    }

    //插入评论数据
    private void insConn(int u_id,int d_id,int f_id,String content){
        Map<String, Object> params = new HashMap<>();
        params.put("id", u_id);
        params.put("did", d_id);
        params.put("fid", f_id);
        params.put("content", content);
        OkHttp.post(INSCOMM, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if (OK.equals(response)) {
                    ToastUtilPuls.showShort(getApplicationContext(),"发表成功！");
                } else if(NO.equals(response)){
                    ToastUtilPuls.showShort(getApplicationContext(),"发表失败！");
                }
                return null;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(getApplicationContext(),"服务器错误！");
            }
        });

    }

    /**
     * 查看所有评论数据
     */
    public static void getShowaC(int did) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("did", did);
        OkHttp.post(COMM, params, new OkCallback() {
            @Override
            public String onResponse(String response) throws JSONException {
                CCOMM.clear();
                FDID.clear();
                TTIM.clear();
                IMAGE.clear();
                JSONArray jsonArray = JSONArray.parseArray(response);
                for(int i=0; i<jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    int uid = (int) object.get("uid");
                    System.out.println(uid+"怎么是1");
                    int did = (int) object.get("fid");
                    Object content = object.get("content");
                    Object gmtModified = object.get("gmtModified");
//                        //content
                    String SISID = String.valueOf(content);

                    //评论内容
                    CCOMM.add(SISID);

                    //父评论id

                    FDID.add(did);

                    String data = String.valueOf(gmtModified);
                    String d=data.substring(0,19);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
                    LocalDateTime localDate = LocalDateTime.parse(d, formatter);
                    //评论时间

                    TTIM.add(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss").format(localDate));

                    //用户id
                    getusername((int)uid);
                    getuseriocon(uid);

                }
                System.out.println(CCOMM.size()+"CCOMM.size()");
                System.out.println(FDID.size()+"FDID.size()");
                System.out.println(TTIM.size()+"TTIM.size()");
                UUID.clear();
                UUID.clear();
                return null;
            }
            @Override
            public void onFailure(String error) {
            }
        });
    }



    /**
     * 查看评论个数预加载评论条数
     */
    public static void getOne(int did) {

        Map<String, Object> params = new HashMap<>(16);
        params.put("did", did);
        OkHttp.post(SIZE, params, new OkCallback() {
            @Override
            public String onResponse(String response) throws JSONException {

                Size=Integer.parseInt(response.trim());
                System.out.println(Size+"Size");
                return response;
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    /**
     * 通过用户id获取用户名
     * @param id  用户id
     */
    public static void getusername(int id){

        UUID.clear();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        OkHttp.post(UNAME, params, new OkCallback() {
            @Override
            public String onResponse(String response) {

                JSONArray jsonArray = JSONArray.parseArray("["+response+"]");
                for(int i=0; i<jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Object title = object.get("username");
                    String SISID = String.valueOf(title);
                    //用户名
                    UUID.add(new String(SISID));
                }

                return null;
            }
            @Override
            public void onFailure(String error) {

            }
        });

    }
    /**
     * 通过用户id获取头像
     * @param id  用户id
     */
    public static void getuseriocon(int id){

        Map<String, Object> params = new HashMap<>();
        params.put("uid",id);
        OkHttp.post(ICON, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                String sss=response.substring(0,response.length() - 1);
                String ssss=sss.substring(1);
                Map maps = (Map) JSON.parse(ssss);
                Object getname = maps.get("image");  //获取指定键所映射的值
                //判断键值是否为String类型
                String name = (String) getname;  //获取指定的value值

                IMAGE.add(name);

                return null;
            }
            @Override
            public void onFailure(String error) {

            }
        });


    }
    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

}