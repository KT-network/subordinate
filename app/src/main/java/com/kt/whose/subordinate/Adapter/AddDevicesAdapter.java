package com.kt.whose.subordinate.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;

public class AddDevicesAdapter extends RecyclerView.Adapter<AddDevicesAdapter.ItemViewHolder> {

    private String[] data;
    private ClickListener.OnClickListener onClickListener;


    public AddDevicesAdapter(String[] data){
        this.data = data;
    }

    @NonNull
    @Override
    public AddDevicesAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_devices,parent,false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddDevicesAdapter.ItemViewHolder holder, int position) {
        holder.textView.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_add_d_name);

            if (onClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(getBindingAdapterPosition());
                    }
                });
            }

        }
    }

    public void setOnClickListener(ClickListener.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

}
