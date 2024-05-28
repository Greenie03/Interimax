package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.Offer;

import java.util.List;
import java.util.Optional;
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
        Intent data = getIntent();
        String result = data.getStringExtra("job_name");
        assert result != null;
        Offer.findOffer(
                Optional.of(result),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ).thenAccept(offers -> {
            StringBuilder display = new StringBuilder();
            for(Offer o : offers){
                display.append(o.getName()).append(" : ").append(o.getEmployerName()).append("\n");
            }
            if(display.toString().isEmpty()) {
                display.append("Aucune offre trouvée.");
            }
            runOnUiThread(() -> {
                TextView job_title = findViewById(R.id.job_title);
                job_title.setText(display.toString());
            });
        });

    }
}