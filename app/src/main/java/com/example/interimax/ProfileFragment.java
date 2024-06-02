package com.example.interimax.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interimax.R;
import com.example.interimax.activities.LoginActivity;
import com.example.interimax.activities.RegisterActivity;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileRole;
    private Button inscriptionButton, loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileRole = view.findViewById(R.id.profile_role);
        inscriptionButton = view.findViewById(R.id.registerbtn);
        loginButton = view.findViewById(R.id.loginbtn);

        // Set profile information for anonymous user
        profileName.setText("Anonyme");
        profileRole.setText("Utilisateur anonyme");

        // Set button click listeners
        inscriptionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
