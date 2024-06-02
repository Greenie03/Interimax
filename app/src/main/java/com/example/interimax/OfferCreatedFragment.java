package com.example.interimax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.interimax.R;

public class OfferCreatedFragment extends Fragment {

    public static OfferCreatedFragment newInstance() {
        return new OfferCreatedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_created, container, false);

        Button returnHomeButton = view.findViewById(R.id.button_return_home);
        returnHomeButton.setOnClickListener(v -> returnToHome());

        return view;
    }

    private void returnToHome() {
        Fragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}