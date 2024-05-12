package com.example.interimax;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        EditText searchField = findViewById(R.id.search_field);
        TextView viewListLink = findViewById(R.id.view_list_link);
        TextView viewAllLink = findViewById(R.id.view_all_link);

        viewListLink.setOnClickListener(v -> viewList());
        viewAllLink.setOnClickListener(v -> viewAll());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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
