package com.example.interimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnItemSelectedListener {
    public DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDrawer();
        setupBottomNavigationView();

        // Initialize and add the initial fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.navigation_home);
        }
        // Setup OnBackPressedDispatcher
        setupOnBackPressedDispatcher();

    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.navbar);

    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /*private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });
    }*/
    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        handleNavigationItemSelected(item);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
            Log.d("handleNavigationItemSelected", "Home selected");
        } else if (itemId == R.id.navigation_message) {
            fragment = new MessagesFragment();
            Log.d("handleNavigationItemSelected", "Messages selected");
        } else if (itemId == R.id.navigation_bookmark) {
            fragment = new SavedOffersFragment();
            Log.d("handleNavigationItemSelected", "Saved selected");
        } else if (itemId == R.id.navigation_notification) {
            fragment = new NotificationsFragment();
            Log.d("handleNavigationItemSelected", "Other selected");
        } else if (itemId == R.id.nav_cvs) {
            fragment = new CVFragment();
            Log.d("handleNavigationItemSelected", "CVs selected");
        } else if (itemId == R.id.nav_applications) {
            fragment = new ApplicationsFragment();
            Log.d("handleNavigationItemSelected", "Candidatures selected");
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
                    // If you have a custom back navigation behavior, add it here
                    // If not, call the default behavior
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
