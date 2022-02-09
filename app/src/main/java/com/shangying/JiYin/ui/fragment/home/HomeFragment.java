package com.shangying.JiYin.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.RunActivity2;
import com.shangying.JiYin.ui.Weather;
import com.shangying.JiYin.ui.fragment.BaseFragment;
import com.shangying.JiYin.ui.maps.Run.RunActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
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
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        Button weather = mContentView.findViewById(R.id.weather);
//        更新方法
        weather.setOnClickListener(new weather());
        Button run = mContentView.findViewById(R.id.run);
//        更新方法
        run.setOnClickListener(new Run());
        Button calendar=mContentView.findViewById(R.id.history);
        calendar.setOnClickListener(new calendar());
        initView();
        initData();

        return mContentView;
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
    public static HomeFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    private class  weather implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), Weather.class);
            startActivity(intent);
        }
    }
    private class  Run implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), RunActivity.class);
            startActivity(intent);
        }
    }


    private class  calendar implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent();
            intent.setClass(requireActivity(), RunActivity2.class);
            startActivity(intent);
        }
    }

}