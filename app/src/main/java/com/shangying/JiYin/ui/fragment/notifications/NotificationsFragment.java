package com.shangying.JiYin.ui.fragment.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.shangying.JiYin.R;
import com.shangying.JiYin.Update.UpdateChecker;
import com.shangying.JiYin.ui.About;
import com.shangying.JiYin.ui.IndexActivity;
import com.shangying.JiYin.ui.LoginActivity;
import com.shangying.JiYin.ui.Showdata;
import com.shangying.JiYin.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class NotificationsFragment extends BaseFragment {


    public static final String BUNDLE_TITLE = "title";

    private View mContentView;
    private Unbinder unbinder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        TableRow imgc = mContentView.findViewById(R.id.more_page_row6);
        TableRow imgc2 = mContentView.findViewById(R.id.more_page_row7);
        TableRow imgc3 = mContentView.findViewById(R.id.more_page_row1);
        TableRow imgc4 = mContentView.findViewById(R.id.more_page_row3);
        TableRow imgc5 = mContentView.findViewById(R.id.more_page_row2);

//        更新方法
        imgc.setOnClickListener(new Update());
//        跳转方法
        imgc2.setOnClickListener(new Aboutt());
        imgc3.setOnClickListener(new ShowData());
        imgc4.setOnClickListener(new ExitLogin());
        imgc5.setOnClickListener(new UpdatePwd());
        initView();
        initData();

        return mContentView;

    }
//    更新
    private class Update implements View.OnClickListener{
        @Override
        public void onClick(View view){
            UpdateChecker.checkForDialog(getContext());
        }
    }
//    关于
    private class Aboutt implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), About.class);

            startActivity(intent);
        }
    }
//    查看数据
    private class ShowData implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), Showdata.class);
            startActivity(intent);
        }
    }
    //    更新密码
    private class UpdatePwd implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), IndexActivity.class);
            startActivity(intent);
        }
    }
//退出登录
    private class ExitLogin implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //步骤1：创建一个SharedPreferences对象
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            //步骤2： 实例化SharedPreferences.Editor对象
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.commit();
            String userId=sharedPreferences.getString("username","");
            if("".equals(userId)){
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
//            这里不关闭会空指针
                mContext.finish();
            }
        }
    }

    private void initView() {
        unbinder = ButterKnife.bind(this, mContentView);
    }
    //初始化数据
    private void initData() {
    }
    @OnClick({})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public static NotificationsFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}