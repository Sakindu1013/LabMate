package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.labmate.fragments.EquipmentFragment;
import com.example.labmate.fragments.HomeFragment;
import com.example.labmate.fragments.LabsFragment;
import com.example.labmate.fragments.ProfileFragment;
import com.example.labmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.example.labmate.utils.UserSession;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;

    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        userSession = new UserSession(this);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            Fragment fragment = null;

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            }
            else if (id == R.id.nav_equipment) {
                fragment = new EquipmentFragment();
            }
            else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            else if (id == R.id.nav_lab) {
                fragment = new LabsFragment();
            }

            if (fragment != null) {

                loadFragment(fragment);
                return true;
            }

            return false;
        });

        if(savedInstanceState == null){
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        Fragment currentFragment =
                                getSupportFragmentManager()
                                        .findFragmentById(
                                                R.id.fragmentContainer
                                        );

                        if (currentFragment instanceof HomeFragment) {

                            new MaterialAlertDialogBuilder(
                                    DashboardActivity.this
                            )
                                    .setTitle("Logout")
                                    .setMessage(
                                            "Are you sure you want to logout?"
                                    )
                                    .setNegativeButton(
                                            "Cancel",
                                            null
                                    )
                                    .setPositiveButton(
                                            "Logout",
                                            (dialog, which) -> {

                                                userSession.clear();

                                                FirebaseAuth
                                                        .getInstance()
                                                        .signOut();

                                                Intent intent =
                                                        new Intent(
                                                                DashboardActivity.this,
                                                                LoginActivity.class
                                                        );

                                                intent.setFlags(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                );

                                                startActivity(intent);
                                                finish();
                                            }
                                    )
                                    .show();

                        } else {

                            bottomNavigation.setSelectedItemId(
                                    R.id.nav_home
                            );
                        }
                    }
                }
        );
    }

    private void loadFragment(Fragment fragment){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragmentContainer,
                        fragment
                )
                .commit();

    }

}