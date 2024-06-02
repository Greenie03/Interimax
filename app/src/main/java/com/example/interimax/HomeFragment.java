package com.example.interimax;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.interimax.models.Offer;
import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.MainActivity;
import com.example.interimax.OffersListActivity;
import com.example.interimax.ResearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "HomeFragment";
    private View rootView;
    private EditText searchField;
    private TextView nomUserTextView, viewListLink, viewAllLink;
    private FloatingActionButton fabAddOffer;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap gMap;
    private List<Offer> offers;
    private Marker CURRENT_POSITION_MARKER;
    private Marker focusedMarker;
    private LatLngBounds.Builder builder;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        FirebaseStorage.getInstance().getReference("pfp/Otacos_logo.svg.png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Log.d(TAG + " OTacos url", task.getResult().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        setupListeners();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        offers = new ArrayList<>();

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
                                String profileImageUrl = document.getString("profileImageUrl");
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

                                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                                Glide.with(this).load(profileImageUrl).circleCrop().into(profileImageView);
                                            } else {
                                                profileImageView.setImageResource(R.drawable.default_profile_image);
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "First name or Last name is null");
                                    nomUserTextView.setText("Anonyme");
                                }
                            } else {
                                Log.d(TAG, "User document not found");
                                nomUserTextView.setText("Anonyme");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Error fetching user document: ", e);
                            nomUserTextView.setText("Anonyme");
                        });
            }
        }
    }

    private void initializeViews() {
        searchField = rootView.findViewById(R.id.search_field);
        viewListLink = rootView.findViewById(R.id.view_list_link);
        viewAllLink = rootView.findViewById(R.id.view_all_link);
        nomUserTextView = rootView.findViewById(R.id.nom_user);
        fabAddOffer = rootView.findViewById(R.id.fab_add_offer);
        profileImageView = rootView.findViewById(R.id.profile_image);

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

        fabAddOffer.setOnClickListener(view -> {
            NewOfferFragment newOfferFragment = new NewOfferFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down);
            transaction.replace(R.id.main_fragment, newOfferFragment);
            transaction.addToBackStack(null);
            transaction.commit();
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
        }

        this.gMap = googleMap;
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.d("Marker1", marker.toString());
                if(!marker.equals(CURRENT_POSITION_MARKER)) {
                    Log.d("not current position marker", marker.getId());
                    if(marker.equals(focusedMarker)) {
                        Log.d("already focused", marker.toString());
                        Offer o = (Offer) marker.getTag();
                        Intent intent = new Intent(getContext(), OfferActivity.class);
                        intent.putExtra("offer", o);
                        startActivity(intent);
                        focusedMarker = null;
                        return true;
                    }
                }
                focusedMarker = marker;
                Log.d("Marker", focusedMarker.toString());
                return false;
            }
        });
        builder = new LatLngBounds.Builder();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d(TAG, "User location: " + location.getLatitude() + ", " + location.getLongitude());
                        // Utiliser la localisation pour afficher les offres autour de l'utilisateur
                        Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.home_icon));
                        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        builder.include(currentPosition);
                        CURRENT_POSITION_MARKER = gMap.addMarker(new MarkerOptions().position(currentPosition).title("Vous").icon(BitmapDescriptorFactory.fromBitmap(icon)));
                        loadOffersNearby(currentPosition.latitude, currentPosition.longitude);
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
        String city = getCityNameFromCoordinates(latitude, longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));
        Offer.findOffer(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new String[]{city}), Optional.empty())
                .thenAccept(offers -> {
                    getActivity().runOnUiThread(() -> {
                        this.offers = offers;
                        displayOffers();
                    });
                });
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
                        Log.d(TAG, "Document for user does not exist");
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
        Offer.getAllOffers().thenAccept(offersRes -> getActivity().runOnUiThread(() -> {

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try{
                Log.d(TAG + " offers total", String.valueOf(offersRes.size()));
                for(Offer o : offersRes){
                    List<Address> addresses = geocoder.getFromLocation(o.getCoordinate().getLatitude(), o.getCoordinate().getLongitude(), 1);
                    if(addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        if (Objects.equals(address.getCountryName(), country)) {
                            this.offers.add(o);
                        }
                    }
                }
                displayOffers();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }));
    }

    private void displayOffers(){
        for(Offer o : this.offers){
            Log.d(TAG, o.toString());
            builder.include(new LatLng(o.getCoordinate().getLatitude(), o.getCoordinate().getLongitude()));
            LatLng coordinates = new LatLng(o.getCoordinate().getLatitude(), o.getCoordinate().getLongitude());
            Marker marker = gMap.addMarker(new MarkerOptions().position(coordinates).title(o.getName()));
            marker.setTag(o);
        }
        LatLngBounds bounds = builder.build();
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    private void loadRandomOffers() {
        // Logique pour charger des offres aléatoires
        Log.d(TAG, "Loading random offers");
        // Implémentez la logique pour charger et afficher les offres ici
        Offer.getAllOffers().thenAccept(offers1 -> {
            this.offers = offers1;
            displayOffers();
        });
    }

    private String getCityNameFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getLocality();
            } else {
                Log.e("error address", "No address found");
            }
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return "";
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
