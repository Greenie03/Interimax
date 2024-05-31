package com.example.interimax.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.interimax.activities.LoginActivity;
import com.example.interimax.adapters.SavedOffersAdapter;
import com.example.interimax.databinding.FragmentSavedOffersBinding;
import com.example.interimax.models.Offer;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedOffersFragment extends Fragment {

    private FragmentSavedOffersBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private SavedOffersAdapter adapter;
    private List<Offer> savedOffersList;

    public SavedOffersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedOffersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        savedOffersList = new ArrayList<>();
        adapter = new SavedOffersAdapter(savedOffersList);

        binding.recyclerViewSavedOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSavedOffers.setAdapter(adapter);

        Log.d("auth.getCurrentUser()", String.valueOf(auth.getCurrentUser()));
        if (auth.getCurrentUser() == null) {
            redirectToLogin();
        } else {
            loadSavedOffers();
        }

        binding.chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "All" chip click action
                Snackbar.make(view, "All offers", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadSavedOffers() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("saved_offers")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savedOffersList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            savedOffersList.add(offer);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(binding.getRoot(), "Failed to load offers", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
