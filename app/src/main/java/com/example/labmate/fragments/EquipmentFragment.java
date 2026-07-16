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
import com.example.labmate.adapters.EquipmentSummaryAdapter;
import com.example.labmate.models.EquipmentSummary;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class EquipmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<EquipmentSummary> equipmentSummaryList;
    private EquipmentSummaryAdapter adapter;
    private FirebaseFirestore db;
    private Button buttonAddEquip;

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

        equipmentSummaryList = new ArrayList<>();
        adapter = new EquipmentSummaryAdapter(getContext(), equipmentSummaryList);

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        loadEquipmentSummary();

        return view;
    }

    public void loadEquipmentSummary(){

        db.collection("equipments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    equipmentSummaryList.clear();

                    HashMap<String, EquipmentSummary> map = new HashMap<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){

                        String type = doc.getString("type");
                        String state = doc.getString("state");

                        if (type == null) continue;

                        EquipmentSummary summary = map.get(type);

                        if (summary == null){
                            summary = new EquipmentSummary(type);
                            map.put(type, summary);
                        }

                        summary.increaseTotal();

                        if (state == null) continue;

                        switch (state){
                            case "In Lab":
                                summary.increaseInLab();
                                break;

                            case "Borrowed":
                                summary.increaseBorrowed();
                                break;

                            case "Under Maintenance":
                                summary.increaseMaintenance();
                                break;

                            case "Removed":
                                summary.increaseRemoved();
                                break;
                        }
                    }

                    equipmentSummaryList.addAll(map.values());
                    equipmentSummaryList.sort((a,b) ->
                            a.getType().compareToIgnoreCase(b.getType()));
                    adapter.notifyDataSetChanged();
                });
    }
}