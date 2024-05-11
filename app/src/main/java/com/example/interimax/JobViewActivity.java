package com.example.interimax;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class JobViewActivity extends AppCompatActivity {

    private TextView tvDescriptionTab, tvRequirementsTab, tvMoreInfoTab;
    private TextView tvContent;  // A single TextView to display various contents based on tab selection
    private FrameLayout contentFrame;  // This is where you might want to add dynamic content

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_view);

        // Find the TextViews by their IDs
        tvDescriptionTab = findViewById(R.id.tvDescriptionTab);
        tvRequirementsTab = findViewById(R.id.tvRequirementsTab);
        tvMoreInfoTab = findViewById(R.id.tvMoreInfoTab);
        contentFrame = findViewById(R.id.contentFrame); // Assuming you are using a TextView inside this FrameLayout for now

        // Initialize the text content to show the job's description by default
        setContent("Description");

        // Set click listeners on each tab to change the content accordingly
        tvDescriptionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent("Description");
                tvDescriptionTab.setTextColor(Integer.parseInt("#FF000000"));
                tvRequirementsTab.setTextColor(Integer.parseInt("#9C9C9C"));
                tvMoreInfoTab.setTextColor(Integer.parseInt("#9C9C9C"));
            }
        });

        tvRequirementsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent("Requis");
                tvRequirementsTab.setTextColor(Integer.parseInt("#FF000000"));
                tvDescriptionTab.setTextColor(Integer.parseInt("#9C9C9C"));
                tvMoreInfoTab.setTextColor(Integer.parseInt("#9C9C9C"));

            }
        });

        tvMoreInfoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent("More Info");
                tvMoreInfoTab.setTextColor(Integer.parseInt("#FF000000"));
                tvDescriptionTab.setTextColor(Integer.parseInt("#9C9C9C"));
                tvRequirementsTab.setTextColor(Integer.parseInt("#9C9C9C"));

            }
        });

        // Example of setting up a button to apply
        Button btnApply = findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the apply action here
            }
        });
    }

    private void setContent(String section) {
        // This method should update the `tvContent` with data based on the section
        // For simplicity, we're using static string, replace this with real data fetching logic
        String content = "";
        switch (section) {
            case "Description":
                content = "Here is a detailed description of the job...";
                break;
            case "Requis":
                content = "Requirements include...";
                break;
            case "More Info":
                content = "Additional information about the job...";
                break;
        }
        tvContent.setText(content);
    }
}
