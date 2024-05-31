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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etFirstname, etLastname, etEmail, etPassword, etPhoneNumber, etAddress, etCountry;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

        // Configuration du Switch pour le type de compte
        androidx.appcompat.widget.SwitchCompat switchAccountType = findViewById(R.id.switchAccountType);

        // Configuration du bouton de validation du formulaire
        Button buttonRegister = findViewById(R.id.btnRegister);
        buttonRegister.setOnClickListener(view -> {
            String firstName = ((EditText) findViewById(R.id.etFirstName)).getText().toString();
            String lastName = ((EditText) findViewById(R.id.etLastName)).getText().toString();
            String dob = ((EditText) findViewById(R.id.etDob)).getText().toString();
            String country = ((EditText) findViewById(R.id.etCountry)).getText().toString();
            String phone = ((EditText) findViewById(R.id.etPhone)).getText().toString();
            String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
            String confirmPassword = ((EditText) findViewById(R.id.etConfirmPassword)).getText().toString();
            String accountType = switchAccountType.isChecked() ? "Candidat" : "Employeur";

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.setCountry(country);
            user.setPhoneNumber(phone);
            user.setRole(accountType);

            db.collection("users").add(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Enregistrement réussi!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("RegisterActivity", "Erreur lors de l'enregistrement", e);
                        Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                    });
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
