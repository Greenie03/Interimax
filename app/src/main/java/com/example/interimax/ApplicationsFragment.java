package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.adapters.ApplicationsAdapter;
import com.example.interimax.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsFragment extends Fragment {

    private RecyclerView recyclerViewApplications;
    private ApplicationsAdapter applicationsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Application> applicationsList;
    private TextView textViewApplicationsCount;

    private static final String TAG = "ApplicationsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);

        initFirebase();
        initViews(view);
        setupToolbar(view);
        checkUserConnection();
        setupRecyclerView(view);
        loadApplications();
        handleBackPress();

        return view;
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        applicationsList = new ArrayList<>();
        Log.d(TAG, "Firebase initialized");
    }

    private void initViews(View view) {
        textViewApplicationsCount = view.findViewById(R.id.active_applications_text);
        Log.d(TAG, "Views initialized");
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.applications_title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> navigateUpOrBack());
        Log.d(TAG, "Toolbar set up");
    }

    private void checkUserConnection() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            Log.d(TAG, "User not connected, redirecting to login");
        } else {
            Log.d(TAG, "User connected: " + currentUser.getUid());
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupRecyclerView(View view) {
        recyclerViewApplications = view.findViewById(R.id.recycler_view_applications);
        applicationsAdapter = new ApplicationsAdapter(applicationsList, application -> {
            Intent intent = new Intent(getActivity(), JobViewActivity.class);
            intent.putExtra("applicationId", application.getId());
            Log.d(TAG, "Opening job view for application: " + application.getId());
            startActivity(intent);
        });
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewApplications.setAdapter(applicationsAdapter);
        Log.d(TAG, "RecyclerView set up");
    }

    private void loadApplications() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "L'utilisateur n'est pas connecté");
            return;
        }

        String userId = currentUser.getUid();
        db.collection("applications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applicationsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Application application = document.toObject(Application.class);
                                applicationsList.add(application);
                                Log.d(TAG, "Application loaded: " + application.getId());
                            }
                            applicationsAdapter.notifyDataSetChanged();
                            textViewApplicationsCount.setText(String.format("%d Applications", applicationsList.size()));
                            Log.d(TAG, "Applications loaded, count: " + applicationsList.size());
                        } else {
                            Log.e(TAG, "QuerySnapshot is null");
                        }
                    } else {
                        Log.e(TAG, "Erreur lors de la récupération des applications", task.getException());
                    }
                });
    }

    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateUpOrBack();
                Log.d(TAG, "Back button pressed");
            }
        });
    }

    private void navigateUpOrBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
            Log.d(TAG, "Navigated up");
        } else {
            requireActivity().onBackPressed();
            Log.d(TAG, "Navigated back");
        }
    }
}
