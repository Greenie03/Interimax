package com.example.interimax.adapters;

import static java.lang.Math.toIntExact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ActiveApplicationAdapter extends RecyclerView.Adapter<ActiveApplicationAdapter.ViewHolder> {

    private List<Map<String, Object>> itemList;
    private OnClickListener onClickListener;
    private Context context;
    public ActiveApplicationAdapter(Context context, List<Map<String, Object>> itemList) {
        this.context = context;
        this.itemList = itemList;
        Collections.reverse(this.itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        updateUI(holder, position);

    }

    private void updateUI(ViewHolder holder, int position){
        Map<String, Object> item = itemList.get(position);
        if(item.get("logoUrl") != null){
            Glide.with(context).load(item.get("logoUrl")).circleCrop().into(holder.icon);
        }
        holder.name.setText((String) item.get("name"));
        holder.employerName.setText((String) item.get("employerName"));
        String salary = item.get("salary") + "/h";
        holder.salary.setText(salary);
        holder.city.setText((String) item.get("city"));
        String periodText = item.get("period") + " h";
        holder.period.setText(periodText);
        Long status = (long) item.get("status");
        switch (toIntExact(status)){
            case 0:
                holder.status.setText(context.getResources().getString(R.string.waiting));
                holder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.waiting_status_background));
                break;
            case 1:
                holder.status.setText(context.getResources().getString(R.string.selection));
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.selection_status_background));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(holder.decisionLayout.getVisibility() == View.GONE){
                            holder.decisionLayout.setVisibility(View.VISIBLE);
                            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(onClickListener != null){
                                        onClickListener.onAcceptClick(itemList, item, position);
                                    }
                                }
                            });

                            holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(onClickListener != null){
                                        onClickListener.onDeclineClick(itemList, item, position);
                                    }
                                }
                            });
                        }else{
                            holder.decisionLayout.setVisibility(View.GONE);
                        }
                    }
                });
                break;
            case 2:
                holder.status.setText(context.getResources().getString(R.string.refuse));
                holder.status.setTextColor(context.getResources().getColor(R.color.red));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.reject_status_background));
                break;
            case 3:
                holder.status.setText(context.getResources().getString(R.string.accepted));
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.selection_status_background));
                break;
            case 4:
                holder.status.setText(context.getResources().getString(R.string.declined_by_you));
                holder.status.setTextColor(context.getResources().getColor(R.color.red));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.reject_status_background));
                break;
        }
    }

    public interface OnClickListener{
        void onAcceptClick(List<Map<String, Object>> list, Map<String, Object> item, int position);
        void onDeclineClick(List<Map<String, Object>> list, Map<String, Object> item, int position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView salary;
        private TextView employerName;
        private TextView city;
        private TextView status;
        private TextView period;
        private LinearLayout decisionLayout;
        private Button acceptButton;
        private Button refuseButton;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            salary = itemView.findViewById(R.id.salary);
            employerName = itemView.findViewById(R.id.employer_name);
            city = itemView.findViewById(R.id.city);
            status = itemView.findViewById(R.id.status);
            period = itemView.findViewById(R.id.period);
            decisionLayout = itemView.findViewById(R.id.decision_layout);
            acceptButton = itemView.findViewById(R.id.accept_button);
            refuseButton = itemView.findViewById(R.id.refuse_button);

        }
    }

}