package com.example.interimax.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.interimax.R;
import com.example.interimax.models.Offer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class NewOfferFragment extends Fragment implements EditTextDialogFragment.EditTextDialogListener {

    private TextView jobTitleText, workSpaceText, companyNameText, addressText, requiredExperienceText, workingHoursText, descriptionText, villeText;
    private Button publishOfferButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public static NewOfferFragment newInstance() {
        return new NewOfferFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_offer, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        jobTitleText = view.findViewById(R.id.job_title_label);
        workSpaceText = view.findViewById(R.id.workspace_label);
        companyNameText = view.findViewById(R.id.company_name_label);
        addressText = view.findViewById(R.id.address_label);
        requiredExperienceText = view.findViewById(R.id.experience_label);
        workingHoursText = view.findViewById(R.id.hours_label);
        descriptionText = view.findViewById(R.id.description_label);
        villeText = view.findViewById(R.id.city_label);

        publishOfferButton = view.findViewById(R.id.publish_offer_button);

        view.findViewById(R.id.close_icon).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.job_title_edit).setOnClickListener(v -> showEditDialog("Intitulé du poste*", R.id.job_title_label));
        view.findViewById(R.id.workspace_edit).setOnClickListener(v -> showEditDialog("Espace de travail*", R.id.workspace_label));
        view.findViewById(R.id.company_name_edit).setOnClickListener(v -> showEditDialog("Nom de l'entreprise*", R.id.company_name_label));
        view.findViewById(R.id.address_edit).setOnClickListener(v -> showEditDialog("Adresse*", R.id.address_label));
        view.findViewById(R.id.experience_edit).setOnClickListener(v -> showEditDialog("Expérience requise*", R.id.experience_label));
        view.findViewById(R.id.hours_edit).setOnClickListener(v -> showEditDialog("Horaires*", R.id.hours_label));
        view.findViewById(R.id.description_edit).setOnClickListener(v -> showEditDialog("Description", R.id.description_label));
        view.findViewById(R.id.city_edit).setOnClickListener(v -> showEditDialog("Ville*", R.id.city_label));

        publishOfferButton.setOnClickListener(v -> publishOffer());

        return view;
    }

    private void showEditDialog(String hint, int viewId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        EditTextDialogFragment editTextDialog = EditTextDialogFragment.newInstance(hint, viewId);
        editTextDialog.setEditTextDialogListener(this);
        editTextDialog.show(fragmentManager, "fragment_edit_text_dialog");
    }

    private void publishOffer() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String employerId = currentUser.getUid();
        String jobTitle = jobTitleText.getText().toString();
        String companyName = companyNameText.getText().toString();
        String address = addressText.getText().toString();
        String workSpace = workSpaceText.getText().toString();
        String requiredExperience = requiredExperienceText.getText().toString();
        String workingHours = workingHoursText.getText().toString();
        String description = descriptionText.getText().toString();
        String city = villeText.getText().toString();

        if (jobTitle.isEmpty() || companyName.isEmpty() || address.isEmpty() || city.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        Offer offer = new Offer(employerId, jobTitle, companyName, description, null, 0, 0, new GeoPoint(0, 0), city);

        db.collection("Jobs")
                .add(offer)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Offre publiée avec succès", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors de la publication de l'offre", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Erreur lors de la publication de l'offre", e);
                });
    }

    @Override
    public void onSaveClick(String inputText, int viewId) {
        TextView textView = getView().findViewById(viewId);
        textView.setText(inputText);
        checkRequiredFields();
    }

    private void checkRequiredFields() {
        String jobTitle = jobTitleText.getText().toString();
        String companyName = companyNameText.getText().toString();
        String address = addressText.getText().toString();
        String city = villeText.getText().toString();

        publishOfferButton.setEnabled(!jobTitle.isEmpty() && !companyName.isEmpty() && !address.isEmpty() && !city.isEmpty());
    }
}
