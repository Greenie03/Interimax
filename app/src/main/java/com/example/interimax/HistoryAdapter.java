package com.example.interimax;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<String> itemList;
    private OnClickListener onClickListener;
    private OnClickListener crossOnClickListener;

    public HistoryAdapter(Context context, Set<String> itemList) {
        this.itemList = new ArrayList<>(itemList);
        Collections.reverse(this.itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_element_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = itemList.get(position);
        holder.title.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener != null){
                    onClickListener.onClick(position, item);
                }
            }
        });

        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(crossOnClickListener != null){
                    crossOnClickListener.onClick(position, item);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setCrossOnClickListener(OnClickListener onClickListener) {
        this.crossOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, String model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView title;
        public ImageButton crossButton;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.history_icon_id);
            title = itemView.findViewById(R.id.history_tv);
            crossButton = itemView.findViewById(R.id.delete_button);
        }
    }

}
