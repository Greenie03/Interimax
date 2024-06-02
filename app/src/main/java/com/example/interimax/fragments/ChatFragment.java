package com.example.interimax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.interimax.adapters.MessageAdapter;
import com.example.interimax.databinding.FragmentChatBinding;
import com.example.interimax.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String ARG_EMAIL = "email";

    private FragmentChatBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String userEmail;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String email) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
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
            userEmail = getArguments().getString(ARG_EMAIL);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        binding.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChat.setAdapter(adapter);

        loadMessages();
        setupToolbar();

        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupToolbar() {
        db.collection("users").whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String name = document.getString("name");
                        String role = document.getString("role");
                        String profileImageUrl = document.getString("profileImageUrl");

                        binding.toolbar.setTitle(name);
                        binding.toolbar.setSubtitle(role);
                        Glide.with(this).load(profileImageUrl).circleCrop().into(binding.profileImage);
                    } else {
                        Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                    }
                });

        binding.toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void loadMessages() {
        db.collection("messages")
                .whereEqualTo("sender", auth.getCurrentUser().getEmail())
                .whereEqualTo("receiver", userEmail)
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Failed to load messages.", Toast.LENGTH_SHORT).show();
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
        String messageContent = binding.etMessage.getText().toString().trim();
        if (messageContent.isEmpty()) {
            return;
        }

        Message message = new Message(auth.getCurrentUser().getEmail(), messageContent, System.currentTimeMillis(), "text");
        db.collection("messages").add(message)
                .addOnSuccessListener(documentReference -> binding.etMessage.setText(""))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
