package com.example.interimax;

import android.content.Context;
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

import com.example.interimax.adapters.OfferAdapter;
import com.example.interimax.adapters.SavedOffersAdapter;
import com.example.interimax.databinding.FragmentSavedOffersBinding;
import com.example.interimax.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedOffersFragment extends Fragment {

    private FragmentSavedOffersBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private OfferAdapter adapter;
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
        adapter = new OfferAdapter(getContext(), savedOffersList);

        binding.recyclerViewSavedOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSavedOffers.setAdapter(adapter);
        adapter.setOnClickListener(new OfferAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Offer model) {
                Intent offerIntent = new Intent(getContext(), OfferActivity.class);
                offerIntent.putExtra("offer", model);
                startActivity(offerIntent);
            }
        });

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
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savedOffersList.clear();
                        List<String> offersId = (List<String>) task.getResult().get("savedOffers");
                        if(!offersId.isEmpty()) {
                            db.collection("Job")
                                    .whereIn(FieldPath.documentId(), offersId)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> offerTask) {
                                            savedOffersList = offerTask.getResult().toObjects(Offer.class);
                                            adapter = new OfferAdapter(getContext(), savedOffersList);

                                            binding.recyclerViewSavedOffers.setLayoutManager(new LinearLayoutManager(getContext()));
                                            binding.recyclerViewSavedOffers.setAdapter(adapter);
                                            adapter.setOnClickListener(new OfferAdapter.OnClickListener() {
                                                @Override
                                                public void onClick(int position, Offer model) {
                                                    Intent offerIntent = new Intent(getContext(), OfferActivity.class);
                                                    offerIntent.putExtra("offer", model);
                                                    startActivity(offerIntent);
                                                }
                                            });
                                            adapter.notifyDataSetChanged();
                                            Log.d("DEBUG", savedOffersList.toString());
                                        }
                                    });
                        }
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
