package com.example.interimax.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.interimax.R;
import com.example.interimax.activities.MainActivity;
import com.example.interimax.activities.OffersListActivity;
import com.example.interimax.activities.ResearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton fabAddOffer;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        setupListeners();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        updateUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("updateUI", "Current user ID: " + userId);
            getUserInfo(userId);
        } else {
            Log.d(TAG, "No current user");
            nomUserTextView.setText("Anonyme");
        }
    }

    private void getUserInfo(String userId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String firstName = document.getString("firstname");
                                String lastName = document.getString("lastname");
                                String role = document.getString("role");
                                Log.d(TAG, "First name: " + firstName + ", Last name: " + lastName + ", Role: " + role);
                                if (firstName != null && lastName != null) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            nomUserTextView.setText(firstName + " " + lastName);
                                            Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                            if ("Employeur".equals(role)) {
                                                fabAddOffer.setVisibility(View.VISIBLE);
                                            } else {
                                                fabAddOffer.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "First name or Last name is null");
                                    nomUserTextView.setText("Anonyme");
                                }
                            } else {
                                Log.d(TAG, "Document does not exist");
                                nomUserTextView.setText("Anonyme");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to get document: " + e.getMessage());
                            nomUserTextView.setText("Anonyme");
                        });
            } else {
                Log.d(TAG, "Current user email is null");
                nomUserTextView.setText("Anonyme");
            }
        } else {
            Log.d(TAG, "No current user");
            nomUserTextView.setText("Anonyme");
        }
    }


    private void initializeViews() {
        searchField = rootView.findViewById(R.id.search_field);
        viewListLink = rootView.findViewById(R.id.view_list_link);
        viewAllLink = rootView.findViewById(R.id.view_all_link);
        nomUserTextView = rootView.findViewById(R.id.nom_user);
        fabAddOffer = rootView.findViewById(R.id.fab_add_offer);

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

        fabAddOffer.setOnClickListener(v -> {
            // Implémentez la logique pour ajouter une offre ici
        });
    }

    private void viewList() {
        Intent intent = new Intent(getContext(), OffersListActivity.class);
        intent.putExtra("view", "list");
        startActivity(intent);
    }

    private void viewAll() {
        Intent intent = new Intent(getContext(), OffersListActivity.class);
        intent.putExtra("view", "all");
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Demander la permission de localisation
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d(TAG, "User location: " + location.getLatitude() + ", " + location.getLongitude());
                        // Utiliser la localisation pour afficher les offres autour de l'utilisateur
                        loadOffersNearby(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.d(TAG, "Failed to get user location");
                        loadUserCountryOffers();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Failed to get user location: " + e.getMessage());
                    loadUserCountryOffers();
                });
    }

    private void loadOffersNearby(double latitude, double longitude) {
        // Logique pour charger les offres autour de la localisation de l'utilisateur
        Log.d(TAG, "Loading offers near " + latitude + ", " + longitude);
        // Implémentez la logique pour charger et afficher les offres ici
    }

    private void loadUserCountryOffers() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String country = document.getString("country");
                        if (country != null && !country.isEmpty()) {
                            Log.d(TAG, "User country: " + country);
                            // Utiliser le pays pour afficher les offres
                            loadOffersByCountry(country);
                        } else {
                            Log.d(TAG, "Country not set for user");
                            loadRandomOffers();
                        }
                    } else {
                        Log.d(TAG, "Document does not exist");
                        loadRandomOffers();
                    }
                } else {
                    Log.d(TAG, "Failed to get user country", task.getException());
                    loadRandomOffers();
                }
            });
        } else {
            loadRandomOffers();
        }
    }

    private void loadOffersByCountry(String country) {
        // Logique pour charger les offres par pays
        Log.d(TAG, "Loading offers in country: " + country);
        // Implémentez la logique pour charger et afficher les offres ici
    }

    private void loadRandomOffers() {
        // Logique pour charger des offres aléatoires
        Log.d(TAG, "Loading random offers");
        // Implémentez la logique pour charger et afficher les offres ici
    }
}
