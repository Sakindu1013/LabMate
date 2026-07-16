package com.example.labmate.activities;

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

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;

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

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            }
            else if (id == R.id.nav_equipment) {
                loadFragment(new EquipmentFragment());
                return true;
            }
            else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            else if (id == R.id.nav_lab) {
                loadFragment(new LabsFragment());
                return true;
            }

            return false;
        });

        if(savedInstanceState == null){
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
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