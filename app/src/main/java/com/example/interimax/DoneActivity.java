package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.Offer;

public class DoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_done);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getIntent().getExtras() != null){
            Offer offer = getIntent().getParcelableExtra("offer");
            TextView successTV = findViewById(R.id.successTV);
            String successString = getResources().getString(R.string.success_text) + " " + offer.getEmployerName();
            successTV.setText(successString);

            Button applicationsButton = findViewById(R.id.applications);
            applicationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DoneActivity.this, ApplicationsActivity.class);
                    startActivity(intent);
                }
            });

            Button otherOffer = findViewById(R.id.other_offer);
            otherOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DoneActivity.this, ResearchActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}