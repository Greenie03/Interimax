package com.example.interimax;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.interimax.fragments.JobDescriptionFragment;
import com.example.interimax.fragments.JobMoreInfoFragment;
import com.example.interimax.fragments.JobRequiredFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class JobViewActivity extends AppCompatActivity {

    private TextView tvDescriptionTab, tvRequirementsTab, tvMoreInfoTab;
    private ImageView ivProfile;
    private TextView tvFullName, tvSalary, tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_view);

        // Find the views by their IDs
        ivProfile = findViewById(R.id.ivLogo);
        tvFullName = findViewById(R.id.tvJobTitle);
        tvSalary = findViewById(R.id.tvSalary);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescriptionTab = findViewById(R.id.tvDescriptionTab);
        tvRequirementsTab = findViewById(R.id.tvRequirementsTab);
        tvMoreInfoTab = findViewById(R.id.tvMoreInfoTab);
        FrameLayout contentFrame = findViewById(R.id.contentFrame);

        // Load the default fragment
        loadFragment(new JobDescriptionFragment());

        // Set click listeners on each tab to change the content accordingly
        tvDescriptionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new JobDescriptionFragment());
                updateTabColors(tvDescriptionTab);
            }
        });

        tvRequirementsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new JobRequiredFragment());
                updateTabColors(tvRequirementsTab);
            }
        });

        tvMoreInfoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new JobMoreInfoFragment());
                updateTabColors(tvMoreInfoTab);
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

        // Load user and job data from Firebase
        loadUserData();
        loadJobData();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    private void updateTabColors(TextView selectedTab) {
        tvDescriptionTab.setTextColor(getResources().getColor(R.color.grey_hard));
        tvRequirementsTab.setTextColor(getResources().getColor(R.color.grey_hard));
        tvMoreInfoTab.setTextColor(getResources().getColor(R.color.grey_hard));

        selectedTab.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userId);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        tvFullName.setText(fullName);

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(JobViewActivity.this).load(profileImageUrl).into(ivProfile);
                        } else {
                            ivProfile.setImageResource(R.drawable.spotify_logo); // Default image
                        }
                    }
                }
            });
        }
    }

    private void loadJobData() {
        // Assumption: You have a document reference to the job offer, replace "jobId" with your actual job id
        String jobId = "your_job_id";
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("jobs").document(jobId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String salary = documentSnapshot.getString("salary");
                    String location = documentSnapshot.getString("location");

                    tvSalary.setText(salary);
                    tvLocation.setText(location);
                }
            }
        });
    }
}
