package com.example.interimax;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.fragments.ApplicationsEmployerFragment;
import com.example.interimax.CVFragment;
import com.example.interimax.HomeFragment;
import com.example.interimax.LDMFragment;
import com.example.interimax.MessagesFragment;
import com.example.interimax.NotificationsFragment;
import com.example.interimax.OfferCreatedFragment;
import com.example.interimax.ProfileEmployerFragment;
import com.example.interimax.ProfileFragment;
import com.example.interimax.SavedOffersFragment;
import com.example.interimax.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    public DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseStorage.getInstance().getReference("pfp/bigshaq.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Log.d("bigshaq", task.getResult().toString());
            }
        });

        initializeViews();
        setupDrawer();
        setupBottomNavigationView();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "Current user ID: " + userId);
            getUserInfo(userId);
        } else {
            Log.e(TAG, "No user is currently logged in.");
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.navigation_home);
        }

        setupOnBackPressedDispatcher();
    }

    private void getUserInfo(String userId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser.getEmail();
        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    String firstName = document.getString("firstname");
                    String lastName = document.getString("lastname");
                    String role = document.getString("role");
                    String profileImageUrl = document.getString("profileImageUrl");
                    View headerView = navigationView.getHeaderView(0);
                    TextView navUsername = headerView.findViewById(R.id.nav_header_fullname);
                    TextView navRole = headerView.findViewById(R.id.nav_header_role);
                    ImageView navProfileImage = headerView.findViewById(R.id.nav_header_image);

                    Log.d(TAG, "First name: " + firstName + ", Last name: " + lastName + ", Role: " + role);
                    if (firstName != null && lastName != null) {
                        navUsername.setText(String.format("%s %s", firstName, lastName));
                    } else {
                        navUsername.setText("Anonyme");
                        Log.e(TAG, "User full name is null.");
                        }

                        if (role != null) {
                            navRole.setText(role);
                        } else {
                            navRole.setText("Rôle inconnu");
                            Log.e(TAG, "User role is null.");
                        }

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(MainActivity.this).load(profileImageUrl).into(navProfileImage);
                        } else {
                            navProfileImage.setImageResource(R.drawable.default_profile_image); // Image par défaut
                            Log.e(TAG, "Profile image URL is null or empty.");
                        }
                    } else {
                        Log.e(TAG, "Error getting user details", task.getException());
                    }

            });
        }
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Initialiser les éléments du menu pour les utilisateurs anonymes
        updateMenuForAnonymousUser();
    }

    private void updateMenuForAnonymousUser() {
        MenuItem profileItem = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            profileItem.setTitle("Voir Profil");
            logoutItem.setTitle("Login");
        } else {
            profileItem.setTitle("Profil");
            logoutItem.setTitle("Déconnexion");
        }
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        handleNavigationItemSelected(item);
        // Fermer le tiroir de navigation si un élément est sélectionné
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            if (itemId == R.id.navigation_home || itemId == R.id.search_field || itemId == R.id.nav_profile || itemId == R.id.nav_settings) {
                // Permettre l'accès à la page d'accueil, à la recherche, à voir profil et aux réglages
                if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment(); // Profil générique pour utilisateur anonyme
                } else if (itemId == R.id.nav_settings) {
                    fragment = new SettingsFragment(); // Fragment des réglages
                } else {
                    fragment = new HomeFragment(); // ou un fragment de recherche si nécessaire
                }
                Log.d("Navigation", "Allowed navigation for anonymous user");
            } else if (itemId == R.id.nav_logout) {
                // Gérer l'option de déconnexion pour les utilisateurs anonymes (connexion ou inscription)
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            } else {
                // Bloquer l'accès à d'autres pages
                Toast.makeText(this, "Connectez-vous pour accéder à cette fonctionnalité", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (itemId == R.id.navigation_home) {
                fragment = new HomeFragment();
                Log.d("Navigation", "Home selected");
            } else if (itemId == R.id.navigation_message) {
                fragment = new MessagesFragment();
                Log.d("Navigation", "Messages selected");
            } else if (itemId == R.id.navigation_bookmark) {
                fragment = new SavedOffersFragment();
                Log.d("Navigation", "Saved selected");
            } else if (itemId == R.id.navigation_notification) {
                fragment = new NotificationsFragment();
                Log.d("Navigation", "Notifications selected");
            } else if (itemId == R.id.nav_cvs) {
                fragment = new CVFragment();
                Log.d("Navigation", "CVs selected");
            } else if (itemId == R.id.nav_profile) {
                loadUserProfileFragment(currentUser);
                return;// Return here to avoid loading fragment twice
            } else if (itemId == R.id.nav_applications) {
                loadUserApplicationsFragment(currentUser);
                return;
            } else if (itemId == R.id.nav_cover_letters) {
                fragment = new LDMFragment();
                Log.d("Navigation", "LDM selected");
            } else if (itemId == R.id.publish_offer_button) {
                fragment = new OfferCreatedFragment();
                Log.d("New Offer", "Offer created");
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
                return;
            }
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .addToBackStack(null) // Ensure the fragment is added to the back stack
                .commit();
    }

    private void handleLogout() {
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupOnBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            }
        });
    }
    private void loadUserProfileFragment(FirebaseUser currentUser) {
        String email = currentUser.getEmail();
        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    String role = document.getString("role");
                    if ("Employeur".equals(role)) {
                        loadFragment(new ProfileEmployerFragment());
                    } else {
                        // loadFragment(ProfileFragment()); // Assuming you have a ProfileFragment for other roles
                        Toast.makeText(this, "Rôle inconnu ou non supporté", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Erreur lors de la récupération du rôle", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserApplicationsFragment(FirebaseUser currentUser) {
        String email = currentUser.getEmail();
        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    String role = document.getString("role");
                    if ("Candidat".equals(role)) {
                        Intent intent = new Intent(MainActivity.this, ApplicationsActivity.class);
                        startActivity(intent);
                    } else if ("Employeur".equals(role)) {
                        Intent intent = new Intent(MainActivity.this, AllMyOffersActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Rôle inconnu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Erreur lors de la récupération des informations utilisateur", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
