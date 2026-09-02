package com.example.labmate.repositories;

import com.example.labmate.models.Lab;
import com.example.labmate.models.EquipmentSummary;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabRepository {

    private final FirebaseFirestore db;

    public LabRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // ---------------------------------------------------------
    // GET ALL LABS
    // ---------------------------------------------------------

    public void getLabs(
            OnLabsSuccess onSuccess,
            OnFailure onFailure
    ) {

        db.collection("labs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Lab> labs = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {

                        Lab lab = new Lab(
                                document.getId(),
                                document.getString("labName"),
                                document.getString("personInCharge"),
                                document.getString("location")
                        );

                        labs.add(lab);
                    }

                    onSuccess.onSuccess(labs);
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    // ---------------------------------------------------------
    // ADD LAB
    // ---------------------------------------------------------

    public void addLab(
            String name,
            String inCharge,
            String location,
            String createdBy,
            String createdByRole,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        Map<String, Object> lab = new HashMap<>();

        lab.put("labName", name);
        lab.put("personInCharge", inCharge);
        lab.put("location", location);
        lab.put("createdAt", System.currentTimeMillis());
        lab.put("createdBy", createdBy);
        lab.put("createdByRole", createdByRole);

        db.collection("labs")
                .add(lab)
                .addOnSuccessListener(
                        documentReference -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    // ---------------------------------------------------------
    // UPDATE LAB
    // ---------------------------------------------------------

    public void updateLab(
            String labId,
            String name,
            String inCharge,
            String location,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        if (labId == null || labId.isEmpty()) {

            onFailure.onFailure(
                    new IllegalArgumentException(
                            "Lab ID missing."
                    )
            );

            return;
        }

        db.collection("labs")
                .document(labId)
                .update(
                        "labName", name,
                        "personInCharge", inCharge,
                        "location", location
                )
                .addOnSuccessListener(
                        unused -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    // ---------------------------------------------------------
    // DELETE LAB
    // ---------------------------------------------------------

    public void deleteLab(
            String labId,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        if (labId == null || labId.isEmpty()) {

            onFailure.onFailure(
                    new IllegalArgumentException(
                            "Lab ID missing."
                    )
            );

            return;
        }

        db.collection("labs")
                .document(labId)
                .delete()
                .addOnSuccessListener(
                        unused -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    // ---------------------------------------------------------
    // GET EQUIPMENT SUMMARY FOR A LAB
    // ---------------------------------------------------------

    public void getEquipmentSummary(
            String labName,
            OnEquipmentSummarySuccess onSuccess,
            OnFailure onFailure
    ) {

        db.collection("equipment")
                .whereEqualTo("lab", labName)
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            List<EquipmentSummary> summaries = new ArrayList<>();
                            Map<String, EquipmentSummary> map = new HashMap<>();

                            int totalEquipment = queryDocumentSnapshots.size();

                            for (DocumentSnapshot document : queryDocumentSnapshots) {

                                String type = document.getString("type");
                                String state = document.getString("state");

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

                            summaries.addAll(map.values());

                            summaries.sort(
                                    (a, b) ->
                                            a.getType()
                                                    .compareToIgnoreCase(
                                                            b.getType()
                                                    )
                            );

                            onSuccess.onSuccess(
                                    summaries,
                                    totalEquipment
                            );
                        }
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    // ---------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------

    public interface OnLabsSuccess {

        void onSuccess(List<Lab> labs);
    }

    public interface OnEquipmentSummarySuccess {

        void onSuccess(
                List<EquipmentSummary> summaries,
                int totalEquipment
        );
    }

    public interface OnSuccess {

        void onSuccess();
    }

    public interface OnFailure {

        void onFailure(Exception e);
    }
}