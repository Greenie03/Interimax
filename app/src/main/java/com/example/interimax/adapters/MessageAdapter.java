package com.example.interimax.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserEmail;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserEmail) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(Objects.equals(message.getSender(), currentUserEmail)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            holder.messageText.setBackground(context.getResources().getDrawable(R.drawable.waiting_status_background));
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            holder.messageText.setBackground(context.getResources().getDrawable(R.drawable.border));
        }
        holder.messageText.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.time_text);
        }

        public void bind(Message message) {
            messageText.setText(message.getContent());
            timeText.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(message.getTime())));
        }
    }
}
