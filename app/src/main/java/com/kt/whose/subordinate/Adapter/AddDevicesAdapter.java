package com.kt.whose.subordinate.Adapter;

import android.content.Context;
import android.service.controls.DeviceTypes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.HttpEntity.Devices;
import com.kt.whose.subordinate.HttpEntity.DevicesType;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;

import java.util.List;

public class AddDevicesAdapter extends RecyclerView.Adapter<AddDevicesAdapter.ItemViewHolder> {


    private ClickListener.OnClickListener onClickListener;

    private Context context;
    private List<DevicesType> data;


    public AddDevicesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AddDevicesAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_devices, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddDevicesAdapter.ItemViewHolder holder, int position) {
        holder.textView.setText(data.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_add_d_name);

            if (onClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(getBindingAdapterPosition());
                    }
                });
            }

        }
    }

    public void setOnClickListener(ClickListener.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public void setData(List<DevicesType> data) {
        /*if (this.data == null) {
            this.data = data;
            notifyDataSetChanged();
        } else {
            this.data.addAll(data);
            notifyItemRangeInserted(getItemCount(), data.size());
        }*/
        this.data = data;
        notifyDataSetChanged();
    }



}
