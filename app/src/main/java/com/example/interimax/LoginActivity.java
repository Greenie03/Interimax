package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Active la flèche de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Utiliser OnBackPressedDispatcher pour gérer le bouton de retour
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Gérer l'action de retour ici pour revenir à l'activité précédente
                navigateUpOrBack();
            }
        });
        // Ajouter un listener sur le TextView "Registrez-vous"
        TextView textViewRegister = findViewById(R.id.tvRegister);
        textViewRegister.setOnClickListener(view -> {
            // Démarrer l'activité RegisterActivity
            Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intentRegister);
        });
        // Ajouter un listener sur le TextView "Plus tard"
        TextView textViewAnonyme = findViewById(R.id.tvAnonyme);
        textViewRegister.setOnClickListener(view -> {
            // Démarrer l'activité MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Revenir à l'activité précédente lorsqu'on clique sur le bouton de retour
            navigateUpOrBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void navigateUpOrBack() {
        if (NavUtils.getParentActivityName(this) != null) {
            NavUtils.navigateUpFromSameTask(this);
        } else {
            Log.d("navigateUpOrBack","finish()");
            finish();
        }
    }


}