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
import com.example.labmate.activities.AddLabActivity;
import com.example.labmate.activities.BorrowEquipmentActivity;
import com.example.labmate.adapters.LabAdapter;
import com.example.labmate.models.Lab;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LabsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Lab> labList;
    private LabAdapter adapter;
    private FirebaseFirestore db;
    private Button buttonAddLab;

    public LabsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_labs, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", 0);
        String role = prefs.getString("role", "");
        String name = prefs.getString("name","");
        boolean isAdmin = "Admin".equals(role);

        buttonAddLab = view.findViewById(R.id.manage_labs);
        buttonAddLab.setVisibility(View.GONE);

        if ("Admin".equals(role)){
            buttonAddLab.setVisibility(View.VISIBLE);
        }

        buttonAddLab.setOnClickListener(v -> {
            Intent addLabIntent = new Intent(requireContext(), AddLabActivity.class);
            startActivity(addLabIntent);
        });

        recyclerView = view.findViewById(R.id.labRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        labList = new ArrayList<>();
        adapter = new LabAdapter(getContext(), labList, isAdmin);

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        loadLabs();
        return view;
    }

    public void loadLabs(){

        db.collection("labs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    labList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){

                        Lab lab = new Lab(doc.getString("labName"), doc.getString("personInCharge"), doc.getString("location"));
                        labList.add(lab);
                    }
                    labList.sort((a,b) ->
                            a.getName().compareToIgnoreCase(b.getName()));
                    adapter.notifyDataSetChanged();
                });
    }
}