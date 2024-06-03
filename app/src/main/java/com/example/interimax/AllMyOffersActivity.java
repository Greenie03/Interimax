package com.example.interimax;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.adapters.OfferEmployerAdapter;
import com.example.interimax.models.Offer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AllMyOffersActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap gMap;
    private Marker focusedMarker;
    private List<Offer> offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(view -> finish());

        offers = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Job")
                .whereEqualTo("employerId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot doc : task.getResult()){
                                Offer offer = doc.toObject(Offer.class);
                                offer.setId(doc.getId());
                                offers.add(offer);
                            }
                            updateUi();
                        }
                    }
                });

    }

    private void updateUi(){
        runOnUiThread(() -> {
            TextView jobNumberTV = findViewById(R.id.job_number);
            String jobNumberText;
            if(offers.size() > 1){
                jobNumberText = offers.size() + " " + getResources().getString(R.string.job_found);
            } else if (offers.size() == 1) {
                jobNumberText = offers.size() + " " + getResources().getString(R.string.one_job_found);
            } else{
                jobNumberText = getResources().getString(R.string.no_job_found);
            }
            jobNumberTV.setText(jobNumberText);

            RecyclerView listResult = findViewById(R.id.list_result);
            listResult.setLayoutManager(new LinearLayoutManager(AllMyOffersActivity.this));
            OfferEmployerAdapter adapter = new OfferEmployerAdapter(AllMyOffersActivity.this, offers);
            listResult.setAdapter(adapter);

            ToggleButton toggle = findViewById(R.id.toggle_button);
            RelativeLayout frame = findViewById(R.id.map_frame);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        listResult.setVisibility(View.GONE);
                        frame.setVisibility(View.VISIBLE);
                    }else{
                        listResult.setVisibility(View.VISIBLE);
                        frame.setVisibility(View.GONE);
                    }
                }
            });

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(AllMyOffersActivity.this);
            }

            adapter.setOnClickListener(new OfferEmployerAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Offer model) {
                    Intent offerIntent = new Intent(AllMyOffersActivity.this, ApplicationsActivity.class);
                    offerIntent.putExtra("offer", model);
                    startActivity(offerIntent);
                }

                @Override
                public void onDeleteClick(int position, Offer model) {
                    new AlertDialog.Builder(AllMyOffersActivity.this)
                            .setTitle("Effacer l'offre")
                            .setMessage("Etes-vous sur de vouloir effacer l'offre ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore.getInstance().collection("candidature")
                                            .whereEqualTo("offer", model.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for(DocumentSnapshot doc : task.getResult()){
                                                            doc.getReference().delete();
                                                        }
                                                    }
                                                }
                                            });
                                    FirebaseFirestore.getInstance().collection("Job").document(model.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                offers.remove(model);
                                                updateUi();
                                            }
                                        }
                                    });
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("Non", null)
                            .show();
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(AllMyOffersActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(AllMyOffersActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AllMyOffersActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        this.gMap = googleMap;
        Log.d("Map's ready", "map ready");
        if(!this.offers.isEmpty()) {
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Offer offer = (Offer) marker.getTag();
                    if(marker.equals(focusedMarker)) {
                        Intent intent = new Intent(AllMyOffersActivity.this, ApplicationsActivity.class);
                        intent.putExtra("offer", offer);
                        startActivity(intent);
                        focusedMarker = null;
                        return true;
                    }
                    focusedMarker = marker;
                    //set infos
                    ImageView icon = findViewById(R.id.icon);
                    TextView name = findViewById(R.id.name);
                    TextView salary = findViewById(R.id.salary);
                    TextView employerName = findViewById(R.id.employer_name);
                    TextView city = findViewById(R.id.city);

                    if(offer.getLogoUrl() != null){
                        icon.setVisibility(View.VISIBLE);
                        Glide.with(AllMyOffersActivity.this).load(offer.getLogoUrl()).circleCrop().into(icon);
                    }else{
                        icon.setVisibility(View.INVISIBLE);
                    }
                    name.setText(offer.getName());
                    employerName.setText(offer.getEmployerName());
                    String salaryText = offer.getSalary() + "/h";
                    salary.setText(salaryText);
                    city.setText(offer.getCity());
                    return false;
                }
            });

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Offer o : this.offers) {
                builder.include(new LatLng(o.getCoordinate().getLatitude(), o.getCoordinate().getLongitude()));
                LatLng coordinates = new LatLng(o.getCoordinate().getLatitude(), o.getCoordinate().getLongitude());
                Marker marker = gMap.addMarker(new MarkerOptions().position(coordinates).title(o.getName()));
                marker.setTag(o);
            }
            LatLngBounds bounds = builder.build();
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        }


    }
}