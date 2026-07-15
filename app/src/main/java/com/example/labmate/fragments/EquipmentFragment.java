package com.example.labmate.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.labmate.R;
import com.example.labmate.activities.AddEquipmentActivity;
import com.example.labmate.activities.AddLabActivity;
import com.example.labmate.adapters.EquipmentAdapter;
import com.example.labmate.adapters.LabAdapter;
import com.example.labmate.models.Equipment;
import com.example.labmate.models.Lab;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EquipmentFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Equipment> equipmentList;
    EquipmentAdapter adapter;
    FirebaseFirestore db;
    Button buttonAddEquip;

    public EquipmentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_equipment, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", 0);
        String role = prefs.getString("role", "");
        String name = prefs.getString("name","");
        boolean isAdmin = "Admin".equals(role);

        buttonAddEquip = view.findViewById(R.id.manage_equipment);
        buttonAddEquip.setVisibility(View.GONE);

        if ("Admin".equals(role)){
            buttonAddEquip.setVisibility(View.VISIBLE);
        }

        buttonAddEquip.setOnClickListener(v -> {
            Intent addLabIntent = new Intent(requireContext(), AddEquipmentActivity.class);
            startActivity(addLabIntent);
        });

        recyclerView = view.findViewById(R.id.labRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        equipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(getContext(), equipmentList, isAdmin);

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        loadLabs();

        return view;
    }

    public void loadLabs(){

        db.collection("equipments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    equipmentList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){

                        Equipment equipment = new Equipment(doc.getId(), doc.getString("equipmentName"), doc.getString("equipmentModel"), doc.getString("lab"), doc.getString("state"), doc.getString("type"));
                        equipmentList.add(equipment);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}