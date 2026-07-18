package com.example.labmate.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labmate.R;
import com.example.labmate.activities.BorrowEquipmentActivity;
import com.example.labmate.activities.ManageInventoryActivity;
import com.example.labmate.activities.ManageUserActivity;
import com.example.labmate.activities.ReturnEquipmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeFragment extends Fragment {
    private TextView txtName;
    private TextView txtRole;
    private Button buttonBorrow;
    private Button buttonReturn;
    private Button buttonManageInventory;
    private Button buttonManageUsers;

    public HomeFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtName = view.findViewById(R.id.tvName);
        txtRole = view.findViewById(R.id.tvRole);

        buttonBorrow = view.findViewById(R.id.equipment_request);
        buttonReturn = view.findViewById(R.id.equipment_return);
        buttonManageInventory = view.findViewById(R.id.manage_inventory);
        buttonManageUsers = view.findViewById(R.id.manage_users);

        buttonManageInventory.setVisibility(View.GONE);
        buttonManageUsers.setVisibility(View.GONE);

        buttonBorrow.setOnClickListener(v -> {
            Intent borrowIntent = new Intent(requireContext(), BorrowEquipmentActivity.class);
            startActivity(borrowIntent);
        });


        buttonReturn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(requireContext(), ReturnEquipmentActivity.class);
            startActivity(returnIntent);
        });

        buttonManageInventory.setOnClickListener(v -> {
            Intent inventoryIntent = new Intent(requireContext(), ManageInventoryActivity.class);
            startActivity(inventoryIntent);
        });

        buttonManageUsers.setOnClickListener(v -> {
            Intent usersIntent = new Intent(requireContext(), ManageUserActivity.class);
            startActivity(usersIntent);
        });

        loadUserData();
        return view;
    }
    private void loadUserData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
            return;
        }
        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){

                        String name = documentSnapshot.getString("name");
                        String role = documentSnapshot.getString("role");

                        txtName.setText(name != null ? name : "Unknown");
                        txtRole.setText(role != null ? role : "No Role");

                        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", name);
                        editor.putString("role", role);
                        editor.apply();

                        if("Admin".equals(role) || "Academic Staff".equals(role) || "Non-Academic Staff".equals(role)) {
                            buttonManageInventory.setVisibility(View.VISIBLE);
                        }
                        
                        if("Admin".equals(role)) {
                            buttonManageUsers.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load Data", Toast.LENGTH_LONG).show();
                });
    }

}