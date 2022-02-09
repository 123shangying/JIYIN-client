package com.shangying.JiYin.ui.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shangying.JiYin.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<MyPath> myPathArrayList;
    OnRecyclerItemClickListener onRecyclerItemClickListener;

    public RecycleViewAdapter(Context context, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
        this.mInflater = LayoutInflater.from(context);
    }

    // 设置 PathList，用于数据填充
    public void setPathList(ArrayList<MyPath> myPathArrayList) {
        this.myPathArrayList = myPathArrayList;
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
        View view = mInflater.inflate(R.layout.recyclerview_item_run, parent, false);
        //view.setBackgroundColor(Color.RED);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerItemClickListener!=null){
                    onRecyclerItemClickListener.onItemClick(view, (int)view.getTag());
                }
            }
        });
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        MyPath myPath = myPathArrayList.get(position);
        Date startTime = new Date(myPath.getStartTime());
        String stratTimeFormat = sdf.format(startTime);
        holder.tv_time.setText("运动开始时间：" + stratTimeFormat);

        String distanceFormat = new DecimalFormat("0.0").format(myPath.getDistance());
        holder.tv_distance.setText("运动举例：" + String.valueOf(distanceFormat) + " 米");

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return myPathArrayList.size();
    }

    /**
     * 自定义RecyclerView 中item view点击回调方法
     */
    public interface OnRecyclerItemClickListener {
        /**
         * item view 回调方法
         *
         * @param view     被点击的view
         * @param position 点击索引
         */
        void onItemClick(View view, int position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_time;        // 运动时间
        public TextView tv_distance;    // 运动举例

        public ViewHolder(View view) {
            super(view);
            tv_time = (TextView) view.findViewById(R.id.run_recycleview_tv_1);
            tv_distance = (TextView) view.findViewById(R.id.run_recycleview_tv_2);
        }
    }
}
