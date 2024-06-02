package com.example.interimax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.interimax.adapters.MessageAdapter;
import com.example.interimax.databinding.FragmentChatBinding;
import com.example.interimax.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String ARG_USER_EMAIL = "user_email";
    private FragmentChatBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String userEmail;

    public static ChatFragment newInstance(String userEmail) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_EMAIL, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_USER_EMAIL);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        binding.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChat.setAdapter(adapter);

        loadMessages();

        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        db.collection("messages")
                .whereEqualTo("sender", auth.getCurrentUser().getEmail())
                .whereEqualTo("receiver", userEmail)
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    messageList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Message message = doc.toObject(Message.class);
                        messageList.add(message);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void sendMessage() {
        String content = binding.etMessage.getText().toString();
        if (content.isEmpty()) {
            return;
        }
        String userEmail = auth.getCurrentUser().getEmail();
        Message message = new Message(userEmail, content, System.currentTimeMillis(), "text");
        db.collection("messages").add(message)
                .addOnSuccessListener(documentReference -> binding.etMessage.setText(""))
                .addOnFailureListener(e -> {});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
