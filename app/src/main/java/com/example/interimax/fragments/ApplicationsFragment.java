package com.example.interimax.fragments;

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

import com.example.interimax.JobViewActivity;
import com.example.interimax.R;
import com.example.interimax.activities.LoginActivity;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);

        // Initialiser Firebase Auth et Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser la liste des applications
        applicationsList = new ArrayList<>();

        // Initialiser la toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.applications_title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> navigateUpOrBack());

        // Vérifier la connexion de l'utilisateur
        checkUserConnection();

        // Initialiser le RecyclerView et son adaptateur
        recyclerViewApplications = view.findViewById(R.id.recycler_view_applications);
        applicationsAdapter = new ApplicationsAdapter(applicationsList, application -> {
            Intent intent = new Intent(getActivity(), JobViewActivity.class);
            intent.putExtra("applicationId", application.getId());
           Log.d("application frag", application.getId());
            startActivity(intent);
        });
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewApplications.setAdapter(applicationsAdapter);

        // TextView pour le nombre de candidatures
        textViewApplicationsCount = view.findViewById(R.id.active_applications_text);

        // Charger les applications
        loadApplications();

        // Utiliser OnBackPressedDispatcher pour gérer le bouton de retour
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Gérer l'action de retour ici pour revenir à l'activité précédente
                navigateUpOrBack();
            }
        });
        return view;
    }

    private void checkUserConnection() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Rediriger vers l'activité de login si l'utilisateur n'est pas connecté
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish(); // Optionnel : fermer l'activité actuelle
            Log.d("checkUserConnection","not connected");
        }
    }

    private void loadApplications() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e("ApplicationsFragment", "L'utilisateur n'est pas connecté");
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
                            }
                            applicationsAdapter.notifyDataSetChanged();
                            textViewApplicationsCount.setText(String.format(String.valueOf(R.string.applications_count), applicationsList.size()));
                        } else {
                            Log.e("ApplicationsFragment", "QuerySnapshot est null");
                        }
                    } else {
                        Log.e("ApplicationsFragment", "Erreur lors de la récupération des applications", task.getException());
                    }
                });
    }
    private void navigateUpOrBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().onBackPressed();
        }
    }
}
