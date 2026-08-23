package com.example.labmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.activities.AddEquipmentActivity;
import com.example.labmate.activities.RemoveEquipmentActivity;
import com.example.labmate.adapters.EquipmentSummaryAdapter;
import com.example.labmate.models.EquipmentSummary;
import com.example.labmate.utils.UserSession;
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
    private Button buttonRemoveEquip;
    private TextView equipmentTotal;

    public EquipmentFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_equipment,
                container,
                false
        );

        UserSession session = new UserSession(requireContext());
        boolean isAdmin = session.isAdmin();

        // Buttons
        buttonAddEquip = view.findViewById(R.id.manage_equipment);
        buttonRemoveEquip = view.findViewById(R.id.remove_equipment);

        buttonAddEquip.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        buttonRemoveEquip.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        buttonAddEquip.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    AddEquipmentActivity.class
            );
            startActivity(intent);
        });

        buttonRemoveEquip.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    RemoveEquipmentActivity.class
            );
            startActivity(intent);
        });

        // Total equipment
        equipmentTotal = view.findViewById(R.id.equipmentTotal);

        // RecyclerView
        recyclerView = view.findViewById(R.id.labRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        equipmentSummaryList = new ArrayList<>();

        // null labName = show equipment from all laboratories
        adapter = new EquipmentSummaryAdapter(
                requireContext(),
                equipmentSummaryList,
                null
        );

        recyclerView.setAdapter(adapter);

        // Firestore
        db = FirebaseFirestore.getInstance();

        loadEquipmentSummary();

        return view;
    }

    private void loadEquipmentSummary() {

        db.collection("equipment")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    equipmentSummaryList.clear();

                    int totalEquipment =
                            queryDocumentSnapshots.size();

                    equipmentTotal.setText(
                            "Total Equipment: " + totalEquipment
                    );

                    HashMap<String, EquipmentSummary> map =
                            new HashMap<>();

                    for (DocumentSnapshot doc :
                            queryDocumentSnapshots) {

                        String type = doc.getString("type");
                        String state = doc.getString("state");

                        if (type == null) {
                            continue;
                        }

                        EquipmentSummary summary = map.get(type);

                        if (summary == null) {
                            summary = new EquipmentSummary(type);
                            map.put(type, summary);
                        }

                        summary.increaseTotal();

                        if (state == null) {
                            continue;
                        }

                        switch (state) {

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

                            case "Reserved":
                                summary.increaseReserved();
                                break;
                        }
                    }

                    equipmentSummaryList.addAll(map.values());

                    equipmentSummaryList.sort((a, b) ->
                            a.getType().compareToIgnoreCase(
                                    b.getType()
                            )
                    );

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e(
                                "EquipmentFragment",
                                "Error loading equipment",
                                e
                        )
                );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (db != null) {
            loadEquipmentSummary();
        }
    }
}