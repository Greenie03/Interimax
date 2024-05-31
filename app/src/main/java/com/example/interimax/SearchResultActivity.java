package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.models.Offer;
import com.example.interimax.adapters.OfferAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SearchResultActivity extends AppCompatActivity {

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent data = getIntent();
        String result = data.getStringExtra("job_name");
        assert result != null;
        Optional<String> name = Optional.of(result);
        Optional<Integer> salaryFrom;
        Optional<Integer> salaryTo;
        Optional<String[]> employers;
        Optional<String[]> locations;
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
                this,
                name,
                employers,
                salaryFrom,
                salaryTo,
                locations
        ).thenAccept(offers -> {
            runOnUiThread(() -> {
                TextView title = findViewById(R.id.title);
                title.setText(result);
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
                adapter.setOnClickListener(new OfferAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Offer model) {
                        Intent offerIntent = new Intent(SearchResultActivity.this, OfferActivity.class);
                        offerIntent.putExtra("offer", model);
                        startActivity(offerIntent);
                    }
                });
            });
        });

    }
}