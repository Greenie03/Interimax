package com.example.interimax.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ProfileImagePickerFragment extends Fragment {

    private static final String TAG = "ProfileImagePicker";

    private ImageView croppedImageView;
    private Button confirmButton;
    private Button cancelButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri croppedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image_picker, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        croppedImageView = view.findViewById(R.id.image_view_preview);
        confirmButton = view.findViewById(R.id.button_confirm);
        cancelButton = view.findViewById(R.id.button_cancel);

        assert getArguments() != null;
        Uri imageUri = getArguments().getParcelable("image_uri");
        if (imageUri != null) {
            startCrop(imageUri);
        }

        confirmButton.setOnClickListener(v -> uploadImageToFirebase());
        cancelButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = UUID.randomUUID().toString() + ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getContext().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(1080, 1080);
        uCrop.withOptions(getCropOptions());
        uCrop.start(getContext(), this);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setShowCropGrid(false);
        return options;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            croppedImageUri = UCrop.getOutput(data);
            if (croppedImageUri != null) {
                Glide.with(this)
                        .load(croppedImageUri)
                        .circleCrop()
                        .into(croppedImageView);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getContext(), cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase() {
        if (croppedImageUri != null) {
            StorageReference profileImageRef = storage.getReference().child("profileImages/" + auth.getCurrentUser().getUid());
            profileImageRef.putFile(croppedImageUri).addOnSuccessListener(taskSnapshot -> {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    updateProfileImageUrl(downloadUrl);
                });
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur lors du téléchargement de l'image", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProfileImageUrl(String downloadUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId();

                        db.collection("users").document(documentId).update("profileImageUrl", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Image de profil mise à jour", Toast.LENGTH_SHORT).show();
                                    passCroppedImageUriToProfileFragment(downloadUrl);
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Erreur lors de la mise à jour de l'URL de l'image", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Erreur lors de la mise à jour de l'URL de l'image", e);
                                });
                    } else {
                        Toast.makeText(getContext(), "Erreur lors de la récupération des informations utilisateur", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erreur lors de la récupération des informations utilisateur", task.getException());
                    }
                });
            } else {
                Toast.makeText(getContext(), "Email de l'utilisateur non disponible", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Email de l'utilisateur non disponible");
            }
        } else {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Utilisateur non connecté");
        }
    }

    private void passCroppedImageUriToProfileFragment(String downloadUrl) {
        ProfileEmployerFragment profileFragment = new ProfileEmployerFragment();
        Bundle args = new Bundle();
        args.putString("new_profile_image_url", downloadUrl);
        profileFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, profileFragment)
                .commit();
    }
}
