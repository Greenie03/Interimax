package com.example.interimax;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileEmployerFragment extends Fragment {

    private static final String TAG = "ProfileEmployerFragment";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileRole;
    private TextView companySectionTitle;
    private TextView companyName;
    private TextView companyLocation;
    private TextView companyDescription;
    private ImageView companyLogo;

    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_employer, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileRole = view.findViewById(R.id.profile_role);
        companySectionTitle = view.findViewById(R.id.company_section_title);
        companyName = view.findViewById(R.id.company_name);
        companyLocation = view.findViewById(R.id.company_location);
        companyDescription = view.findViewById(R.id.company_description);
        companyLogo = view.findViewById(R.id.company_logo);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        loadUserProfile();

        profileImage.setOnClickListener(v -> pickImage());

        // Check if there's a new image URL from the arguments
        if (getArguments() != null) {
            String newProfileImageUrl = getArguments().getString("new_profile_image_url");
            if (newProfileImageUrl != null) {
                Glide.with(this).load(newProfileImageUrl).circleCrop().into(profileImage);
            }
        }

        return view;
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
                        Glide.with(this).load(profileImageUrl).circleCrop().into(profileImage);
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

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            openImagePickerFragment(selectedImageUri);
        }
    }

    private void openImagePickerFragment(Uri imageUri) {
        ProfileImagePickerFragment fragment = new ProfileImagePickerFragment();
        Bundle args = new Bundle();
        args.putParcelable("image_uri", imageUri);
        fragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
