package com.example.interimax;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.interimax.R;
import com.example.interimax.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewOfferFragment extends Fragment implements EditTextDialogFragment.EditTextDialogListener {

    private EditText jobTitleText, workSpaceText, companyNameText, addressText, requiredExperienceText, workingHoursText, descriptionText, villeText;
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
        requiredExperienceText = view.findViewById(R.id.salary_label);
        workingHoursText = view.findViewById(R.id.hours_label);
        descriptionText = view.findViewById(R.id.description_label);
        villeText = view.findViewById(R.id.city_label);

        publishOfferButton = view.findViewById(R.id.publish_offer_button);

        view.findViewById(R.id.close_icon).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        /*view.findViewById(R.id.job_title_edit).setOnClickListener(v -> showEditDialog("Intitulé du poste*", R.id.job_title_label));
        view.findViewById(R.id.workspace_edit).setOnClickListener(v -> showEditDialog("Espace de travail*", R.id.workspace_label));
        view.findViewById(R.id.company_name_edit).setOnClickListener(v -> showEditDialog("Nom de l'entreprise*", R.id.company_name_label));
        view.findViewById(R.id.address_edit).setOnClickListener(v -> showEditDialog("Adresse*", R.id.address_label));
        view.findViewById(R.id.experience_edit).setOnClickListener(v -> showEditDialog("Expérience requise*", R.id.salary_label));
        view.findViewById(R.id.hours_edit).setOnClickListener(v -> showEditDialog("Horaires*", R.id.hours_label));
        view.findViewById(R.id.description_edit).setOnClickListener(v -> showEditDialog("Description", R.id.description_label));
        view.findViewById(R.id.city_edit).setOnClickListener(v -> showEditDialog("Ville*", R.id.city_label));
*/
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
        int salary = Integer.valueOf(requiredExperienceText.getText().toString());
        int workingHours = Integer.valueOf(workingHoursText.getText().toString());
        String description = descriptionText.getText().toString();
        String city = villeText.getText().toString();

        if (jobTitle.isEmpty() || companyName.isEmpty() || address.isEmpty() || city.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String url = (String) task.getResult().get("profileImageUrl");

                Offer offer = new Offer(null, jobTitle, companyName, jobTitle, description, workingHours, salary,getLocationFromAddress(address, city), city, 0, url, employerId);
                onOfferPublishedSuccessfully();
                /*db.collection("Job")
                        .add(offer)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Offre publiée avec succès", Toast.LENGTH_SHORT).show();

                            //requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Erreur lors de la publication de l'offre", Toast.LENGTH_SHORT).show();
                            Log.e("FirestoreError", "Erreur lors de la publication de l'offre", e);
                        });*/
            }
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
    private void onOfferPublishedSuccessfully() {
        OfferCreatedFragment offerCreatedFragment = new OfferCreatedFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, offerCreatedFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private GeoPoint getLocationFromAddress(String address, String city){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocationName(address + ", " + city,1);
            if(addresses != null && !addresses.isEmpty()){
                Address a = addresses.get(0);
                return new GeoPoint(a.getLatitude(), a.getLongitude());
            }else{
                return new GeoPoint(0, 0);
            }
        }catch (IOException e){
            Log.e("NEW OFFER FRAGMENT", e.getMessage(), e);
            return new GeoPoint(0, 0);
        }
    }
}
