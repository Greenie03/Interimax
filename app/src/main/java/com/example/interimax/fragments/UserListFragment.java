package com.example.interimax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.adapters.UserAdapter;
import com.example.interimax.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends DialogFragment {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        rvUsers = view.findViewById(R.id.rvUsers);
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this::openChatFragment);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUsers();

        // Fermer le fragment lorsque l'on clique en dehors
        RelativeLayout parentLayout = view.findViewById(R.id.parent_layout);
        parentLayout.setOnClickListener(v -> dismiss());

        // Empêcher la fermeture lorsqu'on clique à l'intérieur du cadre principal
        LinearLayout mainLayout = view.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(v -> {
            // Ne rien faire, empêcher le clic de passer au parent
        });

        return view;
    }

    private void loadUsers() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (!user.getEmail().equals(auth.getCurrentUser().getEmail())) {
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openChatFragment(User user) {
        String mail = user.getEmail();
        ChatFragment chatFragment = ChatFragment.newInstance(mail);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, chatFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        dismiss(); // Fermer le UserListFragment après avoir ouvert le ChatFragment
    }
}