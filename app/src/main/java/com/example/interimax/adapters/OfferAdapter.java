package com.example.interimax.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.models.Offer;

import java.util.Collections;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Offer> itemList;
    private OnClickListener onClickListener;

    public OfferAdapter(Context context, List<Offer> itemList) {
        this.itemList = itemList;
        Collections.reverse(this.itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Offer item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.employerName.setText(item.getEmployerName());
        String salary = String.valueOf(item.getSalary()) + "/h";
        holder.salary.setText(salary);
        holder.city.setText(item.getCity());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener != null){
                    onClickListener.onClick(position, item);
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

    public interface OnClickListener {
        void onClick(int position, Offer model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView salary;
        private TextView employerName;
        private TextView city;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            salary = itemView.findViewById(R.id.salary);
            employerName = itemView.findViewById(R.id.employer_name);
            city = itemView.findViewById(R.id.city);

        }
    }

}