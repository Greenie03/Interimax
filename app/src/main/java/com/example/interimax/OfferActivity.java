package com.example.interimax;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.Offer;

public class OfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getIntent().getExtras() != null) {
            Offer offer = getIntent().getParcelableExtra("offer");

            ImageButton backButton = findViewById(R.id.back_button);
            ImageButton saveButton = findViewById(R.id.save_button);
            TextView name = findViewById(R.id.name);
            TextView employerName = findViewById(R.id.employer_name);
            TextView salaryTV  = findViewById(R.id.salary);
            TextView cityTV = findViewById(R.id.city);
            TextView descriptionTV = findViewById(R.id.description);
            Button candidateButton = findViewById(R.id.candidate);

            String salary = offer.getSalary() + "/h";

            name.setText(offer.getName());
            employerName.setText(offer.getEmployerName());
            salaryTV.setText(salary);
            cityTV.setText(offer.getCity());
            descriptionTV.setText(offer.getDescription());
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            candidateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}