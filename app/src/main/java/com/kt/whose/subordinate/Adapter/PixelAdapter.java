package com.kt.whose.subordinate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.R;

import java.util.List;
import java.util.Set;

public class PixelAdapter extends RecyclerView.Adapter<PixelAdapter.ItemViewHolder> {

    private List<String> data;

    private int width;
    private int height;


    public PixelAdapter(int width,int height){
        this.width = width;
        this.height = height;
    }


    @NonNull
    @Override
    public PixelAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pixel, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PixelAdapter.ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return width * height;
//        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setData(List<String> data){
        if (this.data == null) {
            this.data = data;
            notifyDataSetChanged();
        } else {
            this.data.addAll(data);
            notifyItemRangeInserted(getItemCount(), data.size());
        }
    }

}
