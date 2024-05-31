package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApplicationsAdapter adapter;
    private List<Application> applicationList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setting up the back button functionality
        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Load profile image
        ImageView profileImage = view.findViewById(R.id.profile_image);
        loadProfileImage(profileImage);

        TextView activeApplicationsText = view.findViewById(R.id.active_applications_text);

        Button tagAll = view.findViewById(R.id.tag_all);
        Button tagRefused = view.findViewById(R.id.tag_refused);
        Button tagPending = view.findViewById(R.id.tag_pending);

        tagAll.setOnClickListener(v -> loadApplications("all"));
        tagRefused.setOnClickListener(v -> loadApplications("refused"));
        tagPending.setOnClickListener(v -> loadApplications("pending"));

        recyclerView = view.findViewById(R.id.recycler_view_applications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationsAdapter(applicationList, application -> {
            // Handle item click
            Intent intent = new Intent(getContext(), JobViewActivity.class);
            intent.putExtra("application_id", application.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Load all applications by default
        loadApplications("all");

        // Handle back button
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        return view;
    }

    private void loadProfileImage(ImageView profileImage) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);
        }
    }

    private void loadApplications(String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("applications")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applicationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Application application = document.toObject(Application.class);
                            if (status.equals("all") || application.getStatus().equals(status)) {
                                applicationList.add(application);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
