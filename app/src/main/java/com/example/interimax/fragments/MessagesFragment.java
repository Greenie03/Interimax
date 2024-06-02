package com.example.interimax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.interimax.R;
import com.example.interimax.adapters.ConversationAdapter;
import com.example.interimax.databinding.FragmentMessagesBinding;
import com.example.interimax.models.Conversation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(conversationList, this::openChatFragment);

        binding.rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversations.setAdapter(adapter);

        loadConversations();

        binding.fab.setOnClickListener(view1 -> {
            UserListFragment userListFragment = UserListFragment.newInstance();
            userListFragment.show(getParentFragmentManager(), "UserListFragment");
        });
    }

    private void loadConversations() {
        db.collection("conversations")
                .whereArrayContains("participants", auth.getCurrentUser().getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Snackbar.make(binding.getRoot(), "Failed to load conversations.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    conversationList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Conversation conversation = doc.toObject(Conversation.class);
                        conversationList.add(conversation);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void openChatFragment(Conversation conversation) {
        ChatFragment chatFragment = ChatFragment.newInstance(String.valueOf(conversation));
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, chatFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
