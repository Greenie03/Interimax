package com.example.interimax.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.models.Notification;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> notificationsList;

    public NotificationsAdapter(List<Notification> notificationsList) {
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationsList.get(position);
        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationDescription.setText(notification.getDescription());
        holder.notificationTime.setText(notification.getTime());
        holder.notificationImage.setImageResource(notification.getImageResId()); // Assurez-vous que l'image est définie correctement
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationImage;
        TextView notificationTitle;
        TextView notificationDescription;
        TextView notificationTime;

        public ViewHolder(View itemView) {
            super(itemView);
            notificationImage = itemView.findViewById(R.id.notification_image);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationDescription = itemView.findViewById(R.id.notification_description);
            notificationTime = itemView.findViewById(R.id.notification_time);
        }
    }
}
