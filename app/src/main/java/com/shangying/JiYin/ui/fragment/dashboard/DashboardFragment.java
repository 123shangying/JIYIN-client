package com.shangying.JiYin.ui.fragment.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;

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
 * @author shangying
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {
    /**
     * //    查询所有动态
     */
    private final static String SHOWALL = MyApplication.S_IP + "dynamic/showall";
    /**
     * //     根据uid查询用户名
     */
    private final static String UNAME = MyApplication.S_IP + "user/uname";
    /**
     * 查询数量，解决数组越界问题
     */
    public final static String CONN = MyApplication.S_IP + "dynamic/connpublic";
    /**
     * 查看用户头像
     */
    private final static String ICON = MyApplication.S_IP + "icon/showicon";
    /**
     * 存放用户名
     */
    public static ArrayList<String> Uid = new ArrayList<>();
    /**
     * 存放用户id
     */
    public static ArrayList<Integer> ID = new ArrayList<>();
    /**
     * 存放标题
     */
    public static ArrayList<String> Title = new ArrayList<>();
    /**
     * 存放内容
     */
    public static ArrayList<String> Content = new ArrayList<>();
    /**
     * 存放时间
     */
    public static ArrayList<String> GmtModified = new ArrayList<>();
    /**
     * 存放
     */
    public static ArrayList<String> List = new ArrayList<>(Uid.size());
    /**
     * 存放用户id
     */
    public static ArrayList<String> IMAGE = new ArrayList<String>();
    /**
     * 存放动态id
     */
    public static ArrayList<Integer> Did = new ArrayList<>();
    public static int Con = 0;
    FloatingActionButton float_btn;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android 4.0 之后不能在主线程中请求HTTP请求
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        List.clear();
        getConn();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    // 在 Fragment 与 Activity 通信时，必须要等到所有的Fragment都已经初始化完成之后，才能够开始进行，所以要写在 onActivityCreated 里面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List.clear();
        getConn();

        // 初始化控件
        init();
        // 初始化数据(先初始化数据)
        initData();
    }

    private void initData() {
//        List.clear();
        getShowall();
        List.clear();
        List.addAll(Uid);
        System.out.println(Did);
        Uid.clear();
        ID.clear();
        Did.clear();
        Title.clear();
        Content.clear();
        GmtModified.clear();
    }

    private void init() {
        listView = this.getActivity().findViewById(R.id.community_listview);
        float_btn = this.getActivity().findViewById(R.id.community_float_btn);
        // 创建自定义Adapter，继承BaseAdapter 因为 baseAdapter 是一个抽象类
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter();
        listView.setAdapter(customBaseAdapter);
        // 设置 ListView 的 item 项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
// 传递 map 给另一个 Activity。
                for (int D = 0; D < Did.size(); D++) {
                    if (i == D) {//第一个item
                        Toast.makeText(DashboardFragment.this.getActivity(), "点击了第" + i + "个,,Did为" + Did.get(i), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        //   intent.putExtra("data", Did.get(i));
                        intent.putExtra("data", i);
                        DynamicDetailActivity.getOne(i);
                        //DynamicDetailActivity.Size=1;
                        intent.putExtra("did", Did.get(i));
                        intent.setClass(getContext(), DynamicDetailActivity.class);
                        startActivity(intent);
                    } else {

                    }
                }
            }
        });
        // 浮动按钮，转发，评论，点赞按钮添加点击事件
        float_btn.setOnClickListener(this::onClick);
//        community_bar_follow.setOnClickListener(this::onClick);
    }

    // 定义一个内部类，继承自 BaseAdapter，并重写其方法
    private class CustomBaseAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            if (Con == 0) {
                return 1;
            } else {
                return Con;
            }

        }

        @Override
        public Object getItem(int i) {

            return Uid.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // 这个方法事件我们 ListView 里面为 item 定义的样式文件转换成为一个 View 对象
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // 优化的 BaseAdapter，使用 ViewHolder 不适用 LayoutInflater，上面的要注释掉
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(DashboardFragment.this.getActivity(), R.layout.listview_item_dynamic, null);
                holder = new ViewHolder();
                // 实例化 list_item 中的 TextView 控件
                holder.tv_uname = view.findViewById(R.id.dynamic_item_uname);
                holder.tv_time = view.findViewById(R.id.dynamic_item_time);
                holder.tv_title = view.findViewById(R.id.dynamic_item_title);
                holder.tv_content = view.findViewById(R.id.dynamic_item_content);
                holder.layout_share = view.findViewById(R.id.dynamic_item_layout_share);
                holder.layout_comment = view.findViewById(R.id.dynamic_item_layout_comment);
                holder.layout_star = view.findViewById(R.id.dynamic_item_layout_star);
                holder.fx = view.findViewById(R.id.fx);
                holder.pl = view.findViewById(R.id.pl);
                holder.dz = view.findViewById(R.id.dz);
                holder.imageView = view.findViewById(R.id.image);
                view.setTag(holder);
            } else {

                holder = (ViewHolder) view.getTag();
                try {
                    holder.tv_uname.setText(Uid.get(i));
                    holder.tv_title.setText(Title.get(i));
                    holder.tv_time.setText(GmtModified.get(i));
                    holder.tv_content.setText(Content.get(i));
                    holder.fx.setText("" + (int) (1 + Math.random() * (10 - 1 + 1)));
                    holder.pl.setText("" + (int) (1 + Math.random() * (10 - 1 + 1)));
                    holder.dz.setText("" + (int) (1 + Math.random() * (10 - 1 + 1)));
                    //得到可用的图片
                    if (IMAGE.size() == 0) {

                    } else {
                        String url = "https://tu.shangying.xyz/imgs/2021/10/1%20(" + IMAGE.get(i) + ").png";
                        System.out.println(url + "--URL");
                        Bitmap bitmap = getHttpBitmap(url);

                        holder.imageView.setImageBitmap(bitmap);
                    }

                } catch (Exception e) {

                }
            }
            // 同时在这里添加 list_item 的监听事件
            holder.layout_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DashboardFragment.this.getActivity(), "转发", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            holder.layout_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DashboardFragment.this.getActivity(), "评论", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            holder.layout_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DashboardFragment.this.getActivity(), "点赞", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });
            // 把 item 对应的布局转换为 View 对象之后，最后记得返回
            return view;
        }

        // 特别注意，在 list_item 里面的控件要在这里声明，然后再实例化
        class ViewHolder {
            TextView tv_uname;
            TextView tv_title;
            TextView tv_content;
            TextView tv_time;
            TextView fx;
            TextView pl;
            TextView dz;
            ImageView imageView;
            LinearLayout layout_share, layout_comment, layout_star;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.community_float_btn: {
                Intent intent = new Intent(this.getActivity(), ShareActivity.class);
                startActivity(intent);
                Uid.clear();
                Did.clear();
                ID.clear();
                break;
            }
            default:
        }
    }

    /**
     * getShowall
     */
    public static void getShowall() {
        Map<String, Object> params = new HashMap<>(16);
        OkHttp.post(SHOWALL, params, new OkCallback() {
            @Override
            public String onResponse(String response) throws JSONException {
                IMAGE.clear();
                JSONArray jsonArray = JSONArray.parseArray(response);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    int uid = (int) object.get("uid");
                    int did = (int) object.get("did");
                    Object title = object.get("title");
                    Object content = object.get("content");
                    Object gmtModified = object.get("gmtModified");
//                    //title
                    String SISID = String.valueOf(title);
                    Title.add(SISID);
                    Did.add(did);
                    ID.add(uid);
                    //content
                    String SITID = String.valueOf(content);
                    Content.add(SITID);
                    //gmtModified
                    String data = String.valueOf(gmtModified);
                    String d = data.substring(0, 19);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
                    LocalDateTime localDate = LocalDateTime.parse(d, formatter);
                    GmtModified.add(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss").format(localDate));
                    // GmtModified.add(d);
                    getusername((int) uid);
                    getuser(uid);
                }
                return null;
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    /**
     * 通过用户id获取用户名
     *
     * @param id 用户id
     */
    public static void getusername(int id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        OkHttp.post(UNAME, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                JSONArray jsonArray = JSONArray.parseArray("[" + response + "]");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Object title = object.get("username");
                    String SISID = String.valueOf(title);
                    Uid.add(new String(SISID));
                }
                return null;
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }

    /**
     * 获取评论总数
     */
    public static void getConn() {
        Map<String, Object> params = new HashMap<>();
        OkHttp.post(CONN, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Con = Integer.parseInt(response.trim());
                return response;
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 通过用户id获取头像
     *
     * @param id 用户id
     */
    public static void getuser(int id) {

        Map<String, Object> params = new HashMap<>();
        params.put("uid", id);
        OkHttp.post(ICON, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                String sss = response.substring(0, response.length() - 1);
                String ssss = sss.substring(1);
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

}