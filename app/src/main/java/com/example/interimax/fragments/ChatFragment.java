package com.example.interimax.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private static final String ARG_EMAIL = "email";
    private static final String TAG = "ChatFragment";

    private String recieverMail;

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
            recieverMail = getArguments().getString(ARG_EMAIL);
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
        db.collection("users").whereEqualTo("email", recieverMail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String name = document.getString("firstname") + " " + document.getString("lastname");
                        String role = document.getString("role");
                        String profileImageUrl = document.getString("profileImageUrl");

                        binding.toolbar.setTitle("");
                        binding.tvNameUser.setText(name);
                        binding.tvRole.setText(role);

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this).load(profileImageUrl).circleCrop().into(binding.profileImage);
                        } else {
                            Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to load user info: " + task.getException());
                        }
                    }
                });

        binding.toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void loadMessages() {
    db.collection("messages")
        .orderBy("time", Query.Direction.ASCENDING)
        .addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Failed to load messages.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load messages: ", error);
                return;
            }

            if (value == null) {
                Log.e(TAG, "No messages found.");
                return;
            }

                messageList.clear();
                auth = FirebaseAuth.getInstance();
                String currentUserEmail = auth.getCurrentUser().getEmail();
                for (QueryDocumentSnapshot doc : value) {
                    Message message = doc.toObject(Message.class);
                    Log.d(TAG, "Message retrieved: " + message.getContent());

                    message.setTime((Long) doc.get("time"));


                    // Ajout de vérifications nulles avant de comparer les adresses e-mail
                    if (message.getSender() != null && message.getReceiver() != null &&
                            currentUserEmail != null && recieverMail != null &&
                            ((message.getSender().equals(currentUserEmail) && message.getReceiver().equals(recieverMail)) ||
                             (message.getSender().equals(recieverMail) && message.getReceiver().equals(currentUserEmail)))) {
                        messageList.add(message);
                        Log.d(TAG, "Message added to list: " + message.getContent());
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Total messages: " + messageList.size());
            });
    }

    private void sendMessage() {
    String messageContent = binding.etMessage.getText().toString().trim();
    if (messageContent.isEmpty()) {
        return;
    }
    auth = FirebaseAuth.getInstance();
    String senderEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
    Message message = new Message(senderEmail, messageContent, System.currentTimeMillis(), "text", userEmail);  // Add receiver email to the message object
    db.collection("messages").add(message)
            .addOnSuccessListener(documentReference -> {
                binding.etMessage.setText("");
                Log.d(TAG, "Message sent successfully");
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to send message: ", e);
            });
}



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
