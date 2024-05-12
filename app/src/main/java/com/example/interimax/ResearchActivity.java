package com.example.interimax;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.Offer;

import java.util.Set;
import java.util.stream.Collectors;

public class ResearchActivity extends AppCompatActivity {
    EditText search_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_research);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        search_input = findViewById(R.id.search_input);
        LinearLayout button_layout = findViewById(R.id.popular_buttons);
        Set<String> jobTitle = Offer.getAllOffers().stream().map(Offer::getJobTitle).collect(Collectors.toSet());
        jobTitle.forEach(jt -> {
            Button b = createNewPopularButton(jt);
            button_layout.addView(b);
        });

        ImageButton search_button = findViewById(R.id.search_button_id);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text = search_input.getText().toString();
                if(!search_text.isEmpty()){
                    Intent intent = new Intent(ResearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("job_name", search_text);
                    startActivity(intent);
                }
            }
        });

        ImageButton filter_button = findViewById(R.id.filter_button);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ResearchActivity.this, "Filter button", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Button createNewPopularButton(String name){
        Button b = new Button(this);
        b.setText(name);
        b.setBackground(getDrawable(R.drawable.popular_button_background));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(18,0,18,0);
        b.setLayoutParams(lp);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_input.setText(name);
            }
        });

        return b;
    }
}