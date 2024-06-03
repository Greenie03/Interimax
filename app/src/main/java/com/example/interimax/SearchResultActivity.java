package com.example.interimax;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interimax.adapters.OfferAdapter;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SearchResultActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Marker focusedMarker;
    private List<Offer> offers;

    private static final String TAG = "SearchResultActivity";

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

        Intent data = getIntent();
        Optional<String> name;
        Optional<Integer> salaryFrom;
        Optional<Integer> salaryTo;
        Optional<String[]> employers;
        Optional<String[]> locations;
        if(data.hasExtra("job_name")){
            name = Optional.of(data.getStringExtra("job_name"));
        }else{
            name = Optional.empty();
        }
        if(data.hasExtra("salaryFrom")){
            salaryFrom = Optional.of(data.getIntExtra("salaryFrom",5));
        }else{
            salaryFrom = Optional.empty();
        }
        if(data.hasExtra("salaryTo")){
            salaryTo = Optional.of(data.getIntExtra("salaryTo",75));
        }else{
            salaryTo = Optional.empty();
        }
        if(data.hasExtra("employers")){
            employers = Optional.of(data.getStringArrayExtra("employers"));
        }else{
            employers = Optional.empty();
        }
        if(data.hasExtra("locations")){
            locations = Optional.of(data.getStringArrayExtra("locations"));
        }else{
            locations = Optional.empty();
        }

        Offer.findOffer(
                name,
                employers,
                salaryFrom,
                salaryTo,
                locations,
                Optional.empty()
        ).thenAccept(offers -> {
            runOnUiThread(() -> {
                this.offers = offers;
                TextView title = findViewById(R.id.title);
                if(name.isPresent()){
                    title.setText(name.get());
                }else{
                    title.setText("Offres correspondantes");
                }
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
                listResult.setLayoutManager(new LinearLayoutManager(this));
                OfferAdapter adapter = new OfferAdapter(this, offers);
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
                    mapFragment.getMapAsync(this);
                }

                adapter.setOnClickListener(new OfferAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Offer model) {
                        FirebaseFirestore.getInstance().collection("Job").document(model.getId()).update("popularity", model.getPopularity()+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("update", "popularity updated");
                            }
                        });
                        FirebaseFirestore.getInstance().collection("job_popularity").document(model.getJobTitle()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot docSnap = task.getResult();
                                    if(docSnap.contains("popularity")){
                                        FirebaseFirestore.getInstance().collection("job_popularity").document(model.getJobTitle()).update("popularity", (long) docSnap.get("popularity") + 1);
                                    }else{
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("popularity", 1);
                                        FirebaseFirestore.getInstance().collection("job_popularity").document(model.getJobTitle()).set(data);
                                    }
                                }
                            }
                        });

                        Intent offerIntent = new Intent(SearchResultActivity.this, OfferActivity.class);
                        offerIntent.putExtra("offer", model);
                        startActivity(offerIntent);
                    }
                });
            });
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(SearchResultActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SearchResultActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchResultActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                        Intent intent = new Intent(SearchResultActivity.this, OfferActivity.class);
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
                        Glide.with(SearchResultActivity.this).load(offer.getLogoUrl()).circleCrop().into(icon);
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