package com.kt.whose.subordinate.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;

import java.util.List;

public class DevicesMainAdapter extends RecyclerView.Adapter<DevicesMainAdapter.ItemViewHolder> {

    private static final String TAG = "DevicesMainAdapter";

    private Context mContext;
    private List<DevicesInfoSql> data;
    private ClickListener.OnClickListener onClickListener;
    private ClickListener.OnLongClickListener onLongClickListener;

    private android.os.Handler handler;


    public DevicesMainAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public DevicesMainAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesMainAdapter.ItemViewHolder holder, int position) {
        String name = data.get(position).getName();
        String picUrl = data.get(position).getPicUrl();
        holder.devicesName.setText(name);
        boolean state = data.get(position).isState();
        holder.devicesConnectStateIcon.setSelected(state);

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView devicesConnectStateIcon;
        ImageView devicesPic;
        TextView devicesName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            devicesConnectStateIcon = itemView.findViewById(R.id.item_devices_connect_state_icon);
            devicesPic = itemView.findViewById(R.id.item_devices_pic);
            devicesName = itemView.findViewById(R.id.item_devices_name);

            if (clickDevicesInfoListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickDevicesInfoListener.onDevicesInfo(data.get(getBindingAdapterPosition()));
                    }
                });
            }

            /*if (onClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(getBindingAdapterPosition());
                    }
                });
            }*/

            if (onLongClickListener != null) {

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onLongClickListener.onLongClick(getBindingAdapterPosition());
                        return false;
                    }
                });

            }

        }
    }


    public void setData(List<DevicesInfoSql> data) {
        if (this.data == null) {
            this.data = data;
            notifyDataSetChanged();
        } else {
            this.data.addAll(data);
            notifyItemRangeInserted(getItemCount(), data.size());
        }

    }

    public void notifyItem(DevicesInfoSql infoSql, int index) {

//        data.get(index).setState(infoSql.isState());

        notifyItemChanged(index, infoSql);
    }

    public void setOnClickListener(ClickListener.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(ClickListener.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setItemConnectState() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        if (data != null) {

                            for (int i = 0; i < data.size(); i++) {
                                DevicesInfoSql devicesInfoSql = data.get(i);

                                long lastTime = devicesInfoSql.getLastTime();
                                long nowTime = devicesInfoSql.getNowTime();

                                if (lastTime != nowTime && (Math.abs(nowTime - lastTime) < 6 || Math.abs(lastTime - nowTime) < 6)) {
                                    devicesInfoSql.setLastTime(devicesInfoSql.getNowTime());
                                    //devicesInfoSql.setNowTime(0);
                                    //devicesInfoSql.setLastTime(0);
                                    // devicesInfoSql.setState(true);
                                } else {
//                                    Log.i(TAG, "id: "+devicesInfoSql.getDevicesId());
//                                    Log.i(TAG, "last: "+lastTime);
//                                    Log.i(TAG, "now: "+nowTime);
                                    devicesInfoSql.setState(false);
                                    devicesDisConnectedListener.onDevicesId(devicesInfoSql.getDevicesId());
                                }

                                int finalI = i;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItem(devicesInfoSql, finalI);
                                    }
                                });

                            }

                        }
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).start();


    }

    private DevicesDisConnectedListener devicesDisConnectedListener;

    public interface DevicesDisConnectedListener {
        void onDevicesId(String s);
    }

    public void setDevicesDisConnectedListener(DevicesDisConnectedListener devicesDisConnectedListener) {
        this.devicesDisConnectedListener = devicesDisConnectedListener;
    }


    private ClickDevicesInfoListener clickDevicesInfoListener;
    public interface ClickDevicesInfoListener{
        void onDevicesInfo(DevicesInfoSql devicesInfoSql);
    }

    public void setClickDevicesInfoListener(ClickDevicesInfoListener clickDevicesInfoListener){
        this.clickDevicesInfoListener = clickDevicesInfoListener;
    }

}
