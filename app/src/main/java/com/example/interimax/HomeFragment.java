package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private View rootView;
    private EditText searchField;
    private TextView viewListLink, viewAllLink;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    private void initializeViews() {
        searchField = rootView.findViewById(R.id.search_field);
        viewListLink = rootView.findViewById(R.id.view_list_link);
        viewAllLink = rootView.findViewById(R.id.view_all_link);
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
