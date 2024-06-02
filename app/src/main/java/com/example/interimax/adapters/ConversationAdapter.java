package com.example.interimax.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.models.Conversation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversationList;
    private OnConversationClickListener onConversationClickListener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationAdapter(List<Conversation> conversationList, OnConversationClickListener onConversationClickListener) {
        this.conversationList = conversationList;
        this.onConversationClickListener = onConversationClickListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversationList.get(position);
        holder.bind(conversation, onConversationClickListener);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
    private CircleImageView profileImage;
    private TextView tvNameUser, tvLastMessage, tvTimestamp, tvUnreadCount;

    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profile_image);
        tvNameUser = itemView.findViewById(R.id.tvName);
        tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
    }

    public void bind(Conversation conversation, OnConversationClickListener onConversationClickListener) {
        tvNameUser.setText(conversation.getUserName());
        tvLastMessage.setText(String.format("%s", conversation.getLastMessage()));

        // Convert timestamp to readable date format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(conversation.getTimestamp()));
        tvTimestamp.setText(formattedDate);

        tvUnreadCount.setText(String.valueOf(conversation.getUnreadCount()));

        itemView.setOnClickListener(v -> onConversationClickListener.onConversationClick(conversation));

        // Load profile image using Glide
        if (conversation.getProfileImageUrl() != null && !conversation.getProfileImageUrl().isEmpty()) {
            Glide.with(profileImage.getContext())
                    .load(conversation.getProfileImageUrl())
                    .circleCrop()
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.default_profile_image);
        }

        itemView.setOnClickListener(v -> onConversationClickListener.onConversationClick(conversation));
    }
}
    }
