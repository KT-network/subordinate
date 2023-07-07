package com.kt.whose.subordinate.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Fragment.Main.DevicesFragment;
import com.kt.whose.subordinate.HttpEntity.Devices;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.DHGenParameterSpec;

public class DevicesMainAdapter extends RecyclerView.Adapter<DevicesMainAdapter.ItemViewHolder> {

    private static final String TAG = "DevicesMainAdapter";

    private Context mContext;
//    private List<Devices> data;
    private ClickListener.OnClickListener onClickListener;
    private ClickListener.OnLongClickListener onLongClickListener;

    private android.os.Handler handler;


    public DevicesMainAdapter(Context context) {
        this.mContext = context;
//        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public DevicesMainAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesMainAdapter.ItemViewHolder holder, int position) {
        String name = DevicesFragment.devicesList.get(position).getName();
        String picUrl = DevicesFragment.devicesList.get(position).getPicUrl();
        holder.devicesName.setText(name);
        boolean state = DevicesFragment.devicesList.get(position).isState();
        holder.devicesConnectStateIcon.setSelected(state);

    }

    @Override
    public int getItemCount() {
        return DevicesFragment.devicesList == null ? 0 : DevicesFragment.devicesList.size();
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

            /*if (clickDevicesInfoListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickDevicesInfoListener.onDevicesInfo(DevicesFragment.devicesList.get(getBindingAdapterPosition()));
                    }
                });
            }*/

            if (onClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(getBindingAdapterPosition());
                    }
                });
            }

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











    // 意外原因断开，初始化所有的item
    public void setDisconnectedState() {
        for (Devices devices : DevicesFragment.devicesList) {
            devices.setNowTime(0);
            devices.setNowTime(0);
            devices.setState(false);
        }
        notifyDataSetChanged();
    }

    public void notifyItem(Devices infoSql, int index) {

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

                        if (DevicesFragment.devicesList.size() != 0) {

                            for (int i = 0; i < DevicesFragment.devicesList.size(); i++) {
                                Devices devices = DevicesFragment.devicesList.get(i);

                                long lastTime = devices.getLastTime();
                                long nowTime = devices.getNowTime();

                                if (lastTime != nowTime && (Math.abs(nowTime - lastTime) < 6 || Math.abs(lastTime - nowTime) < 6)) {
                                    devices.setLastTime(devices.getNowTime());
                                    //devices.setNowTime(0);
                                    //devices.setLastTime(0);
                                    // devices.setState(true);
                                } else {
//                                    Log.i(TAG, "id: "+devices.getDevicesId());
//                                    Log.i(TAG, "last: "+lastTime);
//                                    Log.i(TAG, "now: "+nowTime);
                                    devices.setState(false);
                                    devicesDisConnectedListener.onDevicesId(devices.getDevicesId());
                                }

                                int finalI = i;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItem(devices, finalI);
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

    public interface ClickDevicesInfoListener {
        void onDevicesInfo(Devices devicesInfoSql);
    }

    public void setClickDevicesInfoListener(ClickDevicesInfoListener clickDevicesInfoListener) {
        this.clickDevicesInfoListener = clickDevicesInfoListener;
    }

}
