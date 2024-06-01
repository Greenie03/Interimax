package com.example.interimax.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interimax.R;
import com.example.interimax.fragments.ApplicationsFragment;
import com.example.interimax.fragments.CVFragment;
import com.example.interimax.fragments.HomeFragment;
import com.example.interimax.fragments.LDMFragment;
import com.example.interimax.fragments.MessagesFragment;
import com.example.interimax.fragments.NotificationsFragment;
import com.example.interimax.fragments.SavedOffersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        } else if (itemId == R.id.nav_applications) {
            fragment = new ApplicationsFragment();
            Log.d("Navigation", "Applications selected");
        } else if (itemId == R.id.nav_cover_letters) {
            fragment = new LDMFragment();
            Log.d("Navigation", "LDM selected");
        } else if (itemId == R.id.nav_logout) {
            handleLogout();
            return;
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment)
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
}
