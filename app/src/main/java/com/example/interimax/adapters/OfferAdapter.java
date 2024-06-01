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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Offer> itemList;
    private OnClickListener onClickListener;
    private FirebaseFirestore db;

    public OfferAdapter(Context context, List<Offer> itemList) {
        this.itemList = itemList;
        Collections.reverse(this.itemList);
        db = FirebaseFirestore.getInstance();
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
        holder.jobTitle.setText(item.getJobTitle());
        holder.salary.setText(String.format("%s/h", item.getSalary()));
        holder.city.setText(item.getCity());
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, item);
            }
        });

        fetchEmployerName(item.getId(), holder.employerName, holder);
    }

    private void fetchEmployerName(String employerId, TextView employerNameView, OfferAdapter.ViewHolder holder) {
        db.collection("users").document(employerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String firstName = document.getString("firstname");
                String lastName = document.getString("lastname");
                if (firstName != null && lastName != null) {
                    holder.employerName.setText(String.format("%s %s", firstName, lastName));
                } else {
                    employerNameView.setText("Nom inconnu");
                }
            } else {
                employerNameView.setText("Nom inconnu");
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
        private TextView jobTitle;
        private TextView salary;
        private TextView employerName;
        private TextView city;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            jobTitle = itemView.findViewById(R.id.name);
            salary = itemView.findViewById(R.id.salary);
            employerName = itemView.findViewById(R.id.employer_name);
            city = itemView.findViewById(R.id.city);
        }
    }
}
