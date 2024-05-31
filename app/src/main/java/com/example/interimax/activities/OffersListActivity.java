package com.example.interimax.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.adapters.OfferAdapter;
import com.example.interimax.models.Offer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OffersListActivity extends AppCompatActivity {

    private static final String TAG = "OffersListActivity";
    private RecyclerView recyclerView;
    private OfferAdapter offersAdapter;
    private List<Offer> offerList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_list);

        recyclerView = findViewById(R.id.recycler_view_offers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerList = new ArrayList<>();
        offersAdapter = new OfferAdapter(this, offerList);
        recyclerView.setAdapter(offersAdapter);

        db = FirebaseFirestore.getInstance();

        String viewType = getIntent().getStringExtra("view");
        if ("list".equals(viewType)) {
            loadOffersNearby();
        } else if ("all".equals(viewType)) {
            loadAllOffers();
        } else {
            Log.d(TAG, "Unknown view type: " + viewType);
        }
    }

    private void loadOffersNearby() {
        // Logique pour charger les offres autour de la localisation de l'utilisateur
        // Ici nous devons obtenir la localisation de l'utilisateur depuis les préférences ou Firestore
        double latitude = 48.8566; // Exemple: coordonnées de Paris
        double longitude = 2.3522; // Exemple: coordonnées de Paris
        db.collection("Job")
                .whereGreaterThan("coordinate.latitude", latitude - 0.1)
                .whereLessThan("coordinate.latitude", latitude + 0.1)
                .whereGreaterThan("coordinate.longitude", longitude - 0.1)
                .whereLessThan("coordinate.longitude", longitude + 0.1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offerList.add(offer);
                        }
                        offersAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting offers: ", task.getException());
                    }
                });
    }

    private void loadAllOffers() {
        // Logique pour charger toutes les offres triées par date de la plus récente à la plus ancienne
        db.collection("Jobs")
                .orderBy("publishDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offerList.add(offer);
                        }
                        offersAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting offers: ", task.getException());
                    }
                });
    }
}
