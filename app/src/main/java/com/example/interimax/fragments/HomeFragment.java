package com.example.interimax.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;

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
            Log.d(TAG, "Current user ID: " + userId);
            getUserInfo(userId);
        } else {
            Log.d(TAG, "No current user");
            nomUserTextView.setText("Anonyme");
        }
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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                nomUserTextView.setText(firstName + " " + lastName);
                                Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
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
            } else {
                Log.d(TAG, "Task failed with exception: ", task.getException());
                nomUserTextView.setText("Anonyme");
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "Failed to get document: " + e.getMessage());
            nomUserTextView.setText("Anonyme");
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
        this.map = googleMap;
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande de permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                handleLocationDenied();
            }
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                saveUserLocation(location);
                loadOffersNearby(location.getLatitude(), location.getLongitude());
            } else {
                Log.d(TAG, "Failed to get user location");
                handleLocationDenied();
            }
        });
    }

    private void saveUserLocation(Location location) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.update("location", new LatLng(location.getLatitude(), location.getLongitude()))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User location updated"))
                    .addOnFailureListener(e -> Log.d(TAG, "Failed to update user location", e));
        }
    }

    private void handleLocationDenied() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    String country = task.getResult().getString("country");
                    if (country != null) {
                        loadOffersByCountry(country);
                    } else {
                        loadRandomOffers();
                    }
                } else {
                    loadRandomOffers();
                }
            });
        } else {
            loadRandomOffers();
        }
    }

    private void loadOffersNearby(double latitude, double longitude) {
        // Logique pour charger et afficher les offres autour de la localisation de l'utilisateur
        LatLng userLocation = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        map.addMarker(new MarkerOptions().position(userLocation).title("Vous êtes ici"));

        // Exemple : charger les offres depuis Firestore et ajouter des marqueurs sur la carte
        db.collection("offers")
                .whereGreaterThan("latitude", latitude - 0.1)
                .whereLessThan("latitude", latitude + 0.1)
                .whereGreaterThan("longitude", longitude - 0.1)
                .whereLessThan("longitude", longitude + 0.1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            double offerLat = document.getDouble("latitude");
                            double offerLng = document.getDouble("longitude");
                            String title = document.getString("title");
                            map.addMarker(new MarkerOptions().position(new LatLng(offerLat, offerLng)).title(title));
                        }
                    } else {
                        Log.d(TAG, "Error getting offers: ", task.getException());
                    }
                });
    }

    private void loadOffersByCountry(String country) {
        // Logique pour charger et afficher les offres basées sur le pays de l'utilisateur
        Log.d(TAG, "Loading offers for country: " + country);
        // Exemple : charger les offres depuis Firestore et ajouter des marqueurs sur la carte
        db.collection("offers")
                .whereEqualTo("country", country)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            double offerLat = document.getDouble("latitude");
                            double offerLng = document.getDouble("longitude");
                            String title = document.getString("title");
                            map.addMarker(new MarkerOptions().position(new LatLng(offerLat, offerLng)).title(title));
                        }
                    } else {
                        Log.d(TAG, "Error getting offers: ", task.getException());
                    }
                });
    }

    private void loadRandomOffers() {
        // Logique pour charger et afficher des offres aléatoires
        Log.d(TAG, "Loading random offers");
        // Exemple : charger les offres depuis Firestore et ajouter des marqueurs sur la carte
        db.collection("offers")
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            double offerLat = document.getDouble("latitude");
                            double offerLng = document.getDouble("longitude");
                            String title = document.getString("title");
                            map.addMarker(new MarkerOptions().position(new LatLng(offerLat, offerLng)).title(title));
                        }
                    } else {
                        Log.d(TAG, "Error getting offers: ", task.getException());
                    }
                });
    }
}
