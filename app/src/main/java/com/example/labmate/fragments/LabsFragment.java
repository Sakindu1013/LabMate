package com.example.labmate.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.labmate.R;
import com.example.labmate.activities.AddLabActivity;
import com.example.labmate.adapters.LabAdapter;
import com.example.labmate.models.Lab;
import com.example.labmate.utils.Constants;
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

        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String role = prefs.getString(Constants.KEY_ROLE, "");
        boolean isAdmin = Constants.ROLE_ADMIN.equalsIgnoreCase(role);

        buttonAddLab = view.findViewById(R.id.manage_labs);
        buttonAddLab.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

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

                        Lab lab = new Lab(doc.getId(), doc.getString("labName"), doc.getString("personInCharge"), doc.getString("location"));
                        labList.add(lab);
                    }
                    labList.sort((a,b) ->
                            a.getName().compareToIgnoreCase(b.getName()));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading equipment", e);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLabs();
    }
}