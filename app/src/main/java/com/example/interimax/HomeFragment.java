package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        EditText searchField = rootView.findViewById(R.id.search_field);
        TextView viewListLink = rootView.findViewById(R.id.view_list_link);
        TextView viewAllLink = rootView.findViewById(R.id.view_all_link);

        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResearchActivity.class);
                startActivity(intent);
            }
        });

        viewListLink.setOnClickListener(v -> viewList());
        viewAllLink.setOnClickListener(v -> viewAll());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return rootView;
    }

    private void viewList() {
        // Code pour naviguer vers la liste des offres
    }

    private void viewAll() {
        // Code pour naviguer vers toutes les offres
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Configurez ici la carte, par exemple en définissant des markers ou en zoomant sur un emplacement
    }
}