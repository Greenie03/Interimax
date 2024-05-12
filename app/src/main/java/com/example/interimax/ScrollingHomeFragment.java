package com.example.interimax;

import static com.example.interimax.models.Offer.getAllOffers;
import static com.example.interimax.models.Offer.getOffers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interimax.models.Offer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ScrollingHomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scrolling_home, container, false);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configure the map as needed
        configureMap();
    }

    private void configureMap() {
        // Configurations de base de la carte
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Type de carte
        mMap.setTrafficEnabled(false); // Afficher le trafic
        mMap.setIndoorEnabled(false); // Afficher les vues intérieures
        mMap.getUiSettings().setZoomControlsEnabled(true); // Activer les contrôles de zoom
        mMap.getUiSettings().setAllGesturesEnabled(true); // Activer tous les gestes

        // Ajouter des marqueurs
        loadOffersAndMarkOnMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    private void loadOffersAndMarkOnMap() {
        // Liste d'offres simulée
        //List<Offer> offers = getAllOffers();
        List<Offer> offers = getOffers();

        for (Offer offer : offers) {
            // Créer un marqueur pour chaque offre
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(offer.getLatitude(), offer.getLongitude()))
                    .title(offer.getName())
                    .snippet("Prix: " + offer.getSalary() + "€");

            // Ajouter le marqueur sur la carte
            mMap.addMarker(options);
        }
    }

}
