package com.example.interimax.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.models.Application;

import java.util.List;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {

    private List<Application> applications;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Application application);
    }

    public ApplicationsAdapter(List<Application> applications, OnItemClickListener listener) {
        this.applications = applications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);
        holder.bind(application, listener);
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void updateApplications(List<Application> newApplications) {
        applications = newApplications;
        notifyDataSetChanged();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private ImageView companyLogo;
        private TextView jobTitle;
        private TextView companyName;
        private TextView location;
        private TextView salary;
        private TextView status;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            companyLogo = itemView.findViewById(R.id.company_logo);
            jobTitle = itemView.findViewById(R.id.job_title);
            companyName = itemView.findViewById(R.id.company_name);
            location = itemView.findViewById(R.id.location);
            salary = itemView.findViewById(R.id.salary);
            status = itemView.findViewById(R.id.status);
        }

        public void bind(Application application, OnItemClickListener listener) {
            jobTitle.setText(application.getJobTitle());
            companyName.setText(application.getCompanyName());
            location.setText(application.getLocation());
            salary.setText(application.getSalary());
            status.setText(application.getStatus());

            // Use Glide to load the company logo
            Glide.with(itemView.getContext())
                    .load(application.getCompanyLogoUrl())
                    .into(companyLogo);

            itemView.setOnClickListener(v -> listener.onItemClick(application));
        }
    }
}
