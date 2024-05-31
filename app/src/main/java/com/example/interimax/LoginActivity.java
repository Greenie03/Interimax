package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

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
            Log.d("LoginActivity", "Register clicked");
            // Démarrer l'activité RegisterActivity
            Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intentRegister);
        });

        // Ajouter un listener sur le TextView "Plus tard"
        TextView textViewAnonyme = findViewById(R.id.tvAnonyme);
        textViewAnonyme.setOnClickListener(view -> {
            // Démarrer l'activité MainActivity
            Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentLogin);
        });

        // Ajouter un listener sur le bouton de connexion
        Button buttonLogin = findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (!result.isEmpty()) {
                            for (QueryDocumentSnapshot document : result) {
                                Log.d("LoginActivity", "Connexion réussie pour l'utilisateur: " + document.getId());
                                // Démarrer l'activité MainActivity
                                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intentMain);
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("LoginActivity", "Erreur lors de la connexion: ", task.getException());
                        Toast.makeText(this, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show();
                    }
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
            Log.d("navigateUpOrBack", "finish()");
            finish();
        }
    }
}
