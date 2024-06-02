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
import com.example.interimax.adapters.MessageAdapter;
import com.example.interimax.databinding.FragmentMessagesBinding;
import com.example.interimax.models.Message;
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
    private MessageAdapter adapter;
    private List<Message> messageList;

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
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        binding.messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messagesRecyclerView.setAdapter(adapter);

        loadMessages();

        binding.fab.setOnClickListener(view1 -> showUserListFragment());
    }

    private void loadMessages() {
        db.collection("messages")
                .orderBy("time")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Snackbar.make(binding.getRoot(), "Failed to load messages.", Snackbar.LENGTH_SHORT).show();
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

    private void showUserListFragment() {
        UserListFragment userListFragment = new UserListFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.add(R.id.main_fragment, userListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
