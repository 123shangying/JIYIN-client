package com.shangying.JiYin.ui.fragment.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shangying.JiYin.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryDynamicRecycleViewAdapter extends RecyclerView.Adapter<HistoryDynamicRecycleViewAdapter.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;

    // 声明自定义的接口
    OnRecyclerItemClickListener onRecyclerItemClickListener;

    /*
        key 的值如下
        d_id    u_id    title   content     privacy     c_time
    * */
    ArrayList<HashMap<String, String>>  allDynamics;       // 所有的动态数据

    /*
        单独写一个方法来实例化该 Listener 接口，和 RunFragment 不一致，不能在构造方法里面实例化
    * */
    public HistoryDynamicRecycleViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }


    // 单独定义一个方法实例化 item 点击监听的 Listener接口
    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    // 设置动态数据，在 MyHistoryDynamicActivity 中获 Adapter 实例之后调用此方法，将动态数据传递进来
    public void setDynamicsData(ArrayList<HashMap<String, String>> allDynamics) {
        this.allDynamics = allDynamics;
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();      //getTag()获取数据


        if(onRecyclerItemClickListener!=null){
            System.out.println("getid" + v.getId());
            switch (v.getId()) {
                case R.id.dynamic_history_btn_delete: {
                    // 点击 item 里面的删除按钮
                    onRecyclerItemClickListener.onItemClick(v, ViewName.DELETE, position);
                    break;
                }
                default: {
                    onRecyclerItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
                }
            }
        }
    }

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,       // 点击 RecyclerView 的 item
        DELETE      // 点击 item 里面的 delete 按钮
    }

    /**
     * 自定义RecyclerView 中item view点击回调接口，来实现 点击事件
     */
    public interface OnRecyclerItemClickListener {
        /**
         * item view 回调方法
         *
         * @param view     被点击的view
         * @param position 点击索引
         */
        void onItemClick(View view, ViewName viewName, int position);
    }

    /**
     * item显示类型
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item_dynamic_history, parent, false);
        //view.setBackgroundColor(Color.RED);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this::onClick);
        return viewHolder;
    }


    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_uname.setText("xxx");
        holder.tv_time.setText(allDynamics.get(position).get("c_time"));
        holder.tv_title.setText(allDynamics.get(position).get("title"));
        holder.tv_content.setText(allDynamics.get(position).get("content"));

        holder.btn_delete.setOnClickListener(this);

        // 别忘记设置 tag，整个 item 要设置，里面的控件如果有点击事件，也要设置
        holder.itemView.setTag(position);
        holder.btn_delete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return allDynamics.size();
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_uname, tv_time, tv_title, tv_content;
        Button btn_delete;


        public ViewHolder(View view) {
            super(view);
            tv_uname = (TextView) view.findViewById(R.id.dynamic_history_uname);
            tv_time = (TextView) view.findViewById(R.id.dynamic_history_time);
            tv_title = (TextView) view.findViewById(R.id.dynamic_history_title);
            tv_content = (TextView) view.findViewById(R.id.dynamic_history_content);
            btn_delete = (Button) view.findViewById(R.id.dynamic_history_btn_delete);
        }
    }
}
