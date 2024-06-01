package com.example.interimax.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interimax.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewOfferFragment extends Fragment {

    private EditText jobTitleEditText, companyNameEditText, addressEditText;
    private Button publishOfferButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_offer, container, false);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        ImageView closeIcon = view.findViewById(R.id.close_icon);
        publishOfferButton = view.findViewById(R.id.publish_offer_button);

        LinearLayout jobTitleSection = view.findViewById(R.id.section_job_title);
        jobTitleEditText = createEditText(view);

        jobTitleSection.setOnClickListener(v -> showEditText(jobTitleEditText));

        // Repeat for other sections...

        closeIcon.setOnClickListener(v -> closeFragment());

        publishOfferButton.setOnClickListener(v -> publishOffer());

        return view;
    }

    private EditText createEditText(View view) {
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setVisibility(View.GONE);
        ((ViewGroup) view.findViewById(R.id.form_container)).addView(editText);
        return editText;
    }

    private void showEditText(EditText editText) {
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
    }

    private void closeFragment() {
        // Close the fragment
        getParentFragmentManager().popBackStack();
    }

    private void publishOffer() {
        String jobTitle = jobTitleEditText.getText().toString().trim();
        String companyName = companyNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(jobTitle) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> job = new HashMap<>();
        job.put("employerId", auth.getCurrentUser().getUid());
        job.put("jobTitle", jobTitle);
        job.put("companyName", companyName);
        job.put("address", address);
        job.put("publishDate", System.currentTimeMillis());

        db.collection("Jobs")
                .add(job)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Offre publiée avec succès", Toast.LENGTH_SHORT).show();
                    closeFragment();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors de la publication de l'offre", Toast.LENGTH_SHORT).show();
                });
    }

    private void enablePublishButton() {
        boolean isJobTitleFilled = !TextUtils.isEmpty(jobTitleEditText.getText().toString().trim());
        boolean isCompanyNameFilled = !TextUtils.isEmpty(companyNameEditText.getText().toString().trim());
        boolean isAddressFilled = !TextUtils.isEmpty(addressEditText.getText().toString().trim());

        publishOfferButton.setEnabled(isJobTitleFilled && isCompanyNameFilled && isAddressFilled);
    }
}
