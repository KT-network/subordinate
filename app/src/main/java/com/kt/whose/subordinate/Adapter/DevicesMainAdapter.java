package com.kt.whose.subordinate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.model.DevicesInfoSql;

import java.util.List;

public class DevicesMainAdapter extends RecyclerView.Adapter<DevicesMainAdapter.ItemViewHolder> {

    private Context mContext;
    private List<DevicesInfoSql> data;
    private ClickListener.OnClickListener onClickListener;
    private ClickListener.OnLongClickListener onLongClickListener;


    public DevicesMainAdapter(Context context){
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

            if (onClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(getBindingAdapterPosition());
                    }
                });
            }

            if (onLongClickListener != null){

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


    public void setData(List<DevicesInfoSql> data){
        if (this.data == null){
            this.data = data;
            notifyDataSetChanged();
        }else {
            this.data.addAll(data);
            notifyItemRangeInserted(getItemCount(), data.size());
        }

    }

    public void setOnClickListener(ClickListener.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(ClickListener.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }


}
