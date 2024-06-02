package com.example.interimax.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.R;
import com.example.interimax.models.Application;
import com.example.interimax.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etFirstname, etLastname, etEmail, etPassword, etPhoneNumber, etAddress, etCountry, etDob;
    private Button btnRegister;
    private TextView tvEmployeur, tvCandidat;
    private androidx.appcompat.widget.SwitchCompat switchAccountType;
    private List<Application> applicationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Initialiser la liste des applications
        applicationsList = new ArrayList<>();
        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Active la flèche de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Appliquer des marges pour les barres de système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llRegister), (v, insets) -> {
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

        // Ajouter un listener sur le TextView "Connectez-vous"
        TextView textViewLogin = findViewById(R.id.tvLogin);
        textViewLogin.setOnClickListener(view -> {
            // Démarrer l'activité LoginActivity
            Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        });

        // Initialiser les vues
        etFirstname = findViewById(R.id.etFirstName);
        etLastname = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhone);
        etCountry = findViewById(R.id.etCountry);
        etDob = findViewById(R.id.etDob);
        tvEmployeur = findViewById(R.id.tvEmployeur);
        tvCandidat = findViewById(R.id.tvCandidat);
        switchAccountType = findViewById(R.id.switchAccountType);
        btnRegister = findViewById(R.id.btnRegister);

        // Configuration du Switch pour le type de compte
        switchAccountType.setOnCheckedChangeListener((buttonView, isChecked) -> updateToggleTextColors(!isChecked));

        // Configuration du bouton de validation du formulaire
        btnRegister.setOnClickListener(view -> {
            String firstName = etFirstname.getText().toString();
            String lastName = etLastname.getText().toString();
            String dob = etDob.getText().toString();
            String country = etCountry.getText().toString();
            String phone = etPhoneNumber.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = ((EditText) findViewById(R.id.etConfirmPassword)).getText().toString();
            String accountType = switchAccountType.isChecked() ? "Candidat" : "Employeur";

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccount(email, password, firstName, lastName, dob, country, phone, accountType);
        });
    }

    private void createAccount(String email, String password, String firstName, String lastName, String dob, String country, String phone, String accountType) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("RegisterActivity", "createUserWithEmail:success");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User();
                            user.setFirstname(firstName);
                            user.setLastname(lastName);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setCountry(country);
                            user.setPhoneNumber(phone);
                            user.setBirthDate(dob);
                            user.setRole(accountType);

           /* db.collection("users").add(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })*/
                            db.collection("users").document(firebaseUser.getUid()).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("RegisterActivity", "Erreur lors de l'enregistrement", e);
                                        Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void updateToggleTextColors(boolean isEmployeur) {
        if (isEmployeur) {
            tvEmployeur.setTextColor(Color.BLACK);
            tvCandidat.setTextColor(Color.GRAY);
        } else {
            tvEmployeur.setTextColor(Color.GRAY);
            tvCandidat.setTextColor(Color.BLACK);
        }
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