package com.example.interimax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.adapters.ApplicationsAdapter;
import com.example.interimax.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsEmployerFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApplicationsAdapter adapter;
    private List<Application> applicationList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView activeApplicationsText;
    private TextView filterAll;
    private TextView filterRefused;
    private TextView filterPending;

    public static ApplicationsEmployerFragment newInstance() {
        return new ApplicationsEmployerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications_employer, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        activeApplicationsText = view.findViewById(R.id.active_applications_text);
        filterAll = view.findViewById(R.id.filter_all);
        filterRefused = view.findViewById(R.id.filter_refused);
        filterPending = view.findViewById(R.id.filter_pending);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        applicationList = new ArrayList<>();
        adapter = new ApplicationsAdapter(applicationList, this::onApplicationClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadApplications();

        filterAll.setOnClickListener(v -> filterApplications("all"));
        filterRefused.setOnClickListener(v -> filterApplications("refused"));
        filterPending.setOnClickListener(v -> filterApplications("pending"));

        return view;
    }

    private void loadApplications() {
        // Charger les candidatures depuis Firestore
        db.collection("applications")
                .whereEqualTo("employerId", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applicationList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Application application = document.toObject(Application.class);
                            applicationList.add(application);
                        }
                        adapter.notifyDataSetChanged();
                        updateActiveApplicationsCount();
                    }
                });
    }

    private void updateActiveApplicationsCount() {
        int count = applicationList.size();
        activeApplicationsText.setText("Vous avez " + count + " candidatures actives");
    }

    private void filterApplications(String status) {
        List<Application> filteredList = new ArrayList<>();
        for (Application application : applicationList) {
            if (status.equals("all") || application.getStatus().equals(status)) {
                filteredList.add(application);
            }
        }
        adapter.updateApplications(filteredList);

        // Mettre à jour les styles des filtres
        updateFilterStyles(status);
    }

    private void updateFilterStyles(String selectedFilter) {
        int selectedColor = getResources().getColor(R.color.purple_hard);
        int unselectedColor = getResources().getColor(R.color.purple_soft);

        filterAll.setBackgroundColor(selectedFilter.equals("all") ? selectedColor : unselectedColor);
        filterRefused.setBackgroundColor(selectedFilter.equals("refused") ? selectedColor : unselectedColor);
        filterPending.setBackgroundColor(selectedFilter.equals("pending") ? selectedColor : unselectedColor);
    }

    private void onApplicationClick(Application application) {
        // Logique pour gérer le clic sur une candidature
    }
}
