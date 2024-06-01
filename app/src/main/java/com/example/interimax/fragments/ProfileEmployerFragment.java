package com.example.interimax.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileEmployerFragment extends Fragment {

    private static final String TAG = "ProfileEmployerFragment";

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileRole;
    private TextView companySectionTitle;
    private TextView companyName;
    private TextView companyLocation;
    private TextView companyDescription;
    private ImageView companyLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_employer, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileRole = view.findViewById(R.id.profile_role);
        companySectionTitle = view.findViewById(R.id.company_section_title);
        companyName = view.findViewById(R.id.company_name);
        companyLocation = view.findViewById(R.id.company_location);
        companyDescription = view.findViewById(R.id.company_description);
        companyLogo = view.findViewById(R.id.company_logo);

        setupToolbar(view);
        loadUserProfile();

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        TextView editTextView = view.findViewById(R.id.modify_text);
        editTextView.setOnClickListener(v -> {
            // Logic to handle edit profile action
            Toast.makeText(getContext(), "Modifier profil", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    String firstname = document.getString("firstname");
                    String lastname = document.getString("lastname");
                    String role = document.getString("role");
                    String profileImageUrl = document.getString("profileImageUrl");

                    profileName.setText(firstname + " " + lastname);
                    profileRole.setText(role);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.default_profile_image);
                    }

                    String companyNameStr = document.getString("companyName");
                    String companyLocationStr = document.getString("companyLocation");
                    String companyDescriptionStr = document.getString("companyDescription");
                    String companyLogoUrl = document.getString("companyLogoUrl");

                    if (companyNameStr != null && !companyNameStr.isEmpty()) {
                        companySectionTitle.setVisibility(View.VISIBLE);
                        companyName.setText(companyNameStr);
                        companyLocation.setText(companyLocationStr);
                        companyDescription.setText(companyDescriptionStr);
                        if (companyLogoUrl != null && !companyLogoUrl.isEmpty()) {
                            Glide.with(this).load(companyLogoUrl).into(companyLogo);
                        }
                        requireView().findViewById(R.id.company_section).setVisibility(View.VISIBLE);
                    }

                    // Handle other sections similarly
                } else {
                    Log.e(TAG, "Error getting user details", task.getException());
                }
            });
        }
    }
}
