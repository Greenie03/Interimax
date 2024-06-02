package com.example.interimax.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.interimax.R;
import com.example.interimax.adapters.ConversationAdapter;
import com.example.interimax.databinding.FragmentMessagesBinding;
import com.example.interimax.models.Conversation;
import com.example.interimax.models.Message;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        conversationList = new ArrayList<>();  // Initialisation de conversationList
        adapter = new ConversationAdapter(conversationList, conversation -> {
            // Handle conversation click
            // Open ChatFragment or ChatActivity
            ChatFragment chatFragment = ChatFragment.newInstance(conversation.getUserName());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversations.setAdapter(adapter);

        loadConversations();

        binding.fab.setOnClickListener(view1 -> {
            UserListFragment userListFragment = UserListFragment.newInstance();
            userListFragment.show(getParentFragmentManager(), "UserListFragment");
        });
    }

    private void loadConversations() {
        auth = FirebaseAuth.getInstance();
        db.collection("messages")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Snackbar.make(binding.getRoot(), "Failed to load conversations.", Snackbar.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to load conversations: ", error);
                        return;
                    }

                    if (value == null) {
                        Log.e(TAG, "No conversations found.");
                        return;
                    }

                    Map<String, Conversation> conversationMap = new HashMap<>();
                    String currentUserEmail = auth.getCurrentUser().getEmail();

                    for (QueryDocumentSnapshot doc : value) {
                        Message message = doc.toObject(Message.class);
                        Log.d(TAG, "Message retrieved: " + message.getContent());

                        String otherUserEmail = message.getSender().equals(currentUserEmail) ? message.getReceiver() : message.getSender();

                        if (!conversationMap.containsKey(otherUserEmail)) {
                            Conversation conversation = new Conversation();
                            conversation.setUserName(otherUserEmail); // Temporary until we fetch user details
                            conversation.setLastMessage(message.getContent());
                            conversation.setTimestamp(message.getTime());
                            conversation.setUnreadCount(0); // You can implement unread count logic
                            conversation.setProfileImageUrl(""); // Temporary until we fetch user details

                            conversationMap.put(otherUserEmail, conversation);
                        }
                    }

                    conversationList.clear();
                    conversationList.addAll(conversationMap.values());
                    adapter.notifyDataSetChanged();

                    // Fetch user details for each conversation
                    for (Conversation conversation : conversationList) {
                        fetchUserDetails(conversation);
                    }
                });
    }

    private void fetchUserDetails(Conversation conversation) {
        db.collection("users").whereEqualTo("email", conversation.getUserName()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String firstName = document.getString("firstname");
                        String lastName = document.getString("lastname");
                        String role = document.getString("role");
                        String profileImageUrl = document.getString("profileImageUrl");

                        conversation.setUserName(firstName + " " + lastName);
                        conversation.setProfileImageUrl(profileImageUrl);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to load user details for conversation: " + conversation.getUserName());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
