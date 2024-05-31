package com.example.interimax.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.interimax.R;
import com.example.interimax.activities.MainActivity;
import com.example.interimax.activities.ResearchActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "HomeFragment";
    private View rootView;
    private EditText searchField;
    private TextView nomUserTextView, viewListLink, viewAllLink;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        setupListeners();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "Current user ID: " + userId);
            getUserInfo(userId);
        } else {
            Log.d(TAG, "No current user");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    private void getUserInfo(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String firstName = document.getString("firstname");
                    String lastName = document.getString("lastname");
                    Log.d(TAG, "First name: " + firstName + ", Last name: " + lastName);
                    if (firstName != null && lastName != null) {
                        getActivity().runOnUiThread(() -> nomUserTextView.setText(firstName + " " + lastName));
                    } else {
                        Log.d(TAG, "First name or Last name is null");
                    }
                } else {
                    Log.d(TAG, "Document does not exist");
                }
            } else {
                Log.d(TAG, "Task failed with exception: ", task.getException());
            }
        });
    }

    private void initializeViews() {
        searchField = rootView.findViewById(R.id.search_field);
        viewListLink = rootView.findViewById(R.id.view_list_link);
        viewAllLink = rootView.findViewById(R.id.view_all_link);
        nomUserTextView = rootView.findViewById(R.id.nom_user);

        rootView.findViewById(R.id.profile_image).setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupListeners() {
        searchField.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ResearchActivity.class);
            startActivity(intent);
        });

        viewListLink.setOnClickListener(v -> viewList());
        viewAllLink.setOnClickListener(v -> viewAll());
    }

    private void viewList() {
        // Code pour naviguer vers la liste des offres
    }

    private void viewAll() {
        // Code pour naviguer vers toutes les offres
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Configurez ici la carte, par exemple en définissant des markers ou en zoomant sur un emplacement
    }
}
