package com.example.labmate.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddEquipmentActivity extends AppCompatActivity {

    private AutoCompleteTextView actLab;
    private ArrayList<String> labNames;
    private ArrayAdapter<String> labAdapter;
    private FirebaseFirestore db;
    private ImageView equipmentQR;
    private TextView equipmentIdText;
    private String qrId;
    private AutoCompleteTextView typeDropdown;
    private ArrayList<String> equipmentTypes;
    private ArrayAdapter<String> typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        equipmentQR = findViewById(R.id.equipmentQR);
        equipmentIdText = findViewById(R.id.equipmentIdText);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", 0);
        String role = prefs.getString("role", "");
        String username = prefs.getString("name","");

        typeDropdown = findViewById(R.id.actType);
        equipmentTypes = new ArrayList<>();
        typeAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, equipmentTypes);
        typeDropdown.setAdapter(typeAdapter);
        loadEquipmentTypes();

        AutoCompleteTextView stateDropdown = findViewById(R.id.actState);
        String[] states = getResources().getStringArray(R.array.equipment_states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, states);
        stateDropdown.setAdapter(stateAdapter);

        actLab = findViewById(R.id.actLab);
        labNames = new ArrayList<>();
        labAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labNames);
        actLab.setAdapter(labAdapter);

        EditText equipmentName, equipmentModel;
        AutoCompleteTextView equipmentLab;
        equipmentName = findViewById(R.id.equipmentName);
        equipmentModel = findViewById(R.id.equipmentModel);
        equipmentLab = actLab;

        equipmentLab.setOnItemClickListener((parent, view, position, id) -> {
            equipmentLab.clearFocus();
        });

        typeDropdown.setOnItemClickListener((parent, view, position, id) -> {

            String selected = parent.getItemAtPosition(position).toString();
            if (selected.equals("+ Add New Type")){
                showAppTypeDialog();
            }
            typeDropdown.clearFocus();
        });

        stateDropdown.setOnItemClickListener((parent, view, position, id) -> {
            stateDropdown.clearFocus();
        });

        Button btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                equipmentName.setText("");
                equipmentModel.setText("");
                equipmentLab.setText(null);
                typeDropdown.setText(null);
                stateDropdown.setText(null);
            }
        });

        Button button_add_equipment = (Button) findViewById(R.id.btn_add_equipment);
        button_add_equipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = equipmentName.getText().toString().trim().replaceAll("\\s+", " ");
                String model = equipmentModel.getText().toString().trim().replaceAll("\\s+", " ");
                String lab = equipmentLab.getText().toString();
                String type = typeDropdown.getText().toString();
                String state = stateDropdown.getText().toString();

                if (name.isEmpty() || model.isEmpty() || lab.isEmpty() || type.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> equipment = new HashMap<>();
                equipment.put("equipmentName", name);
                equipment.put("equipmentModel", model);
                equipment.put("lab", lab);
                equipment.put("type", type);
                equipment.put("state", state);
                equipment.put("createdAt", System.currentTimeMillis());
                equipment.put("createdBy", username);
                equipment.put("createdByRole", role);
                equipment.put("qrId", qrId);

                db.collection("equipment")
                        .add(equipment)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getApplicationContext(), "Equipment Added Successfully", Toast.LENGTH_LONG).show();

                            equipmentName.setText("");
                            equipmentModel.setText("");
                            equipmentLab.setText(null);
                            typeDropdown.setText(null);
                            stateDropdown.setText(null);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            }
        });

        loadLabs();
        generateEquipmentId();
    }

    private void showAppTypeDialog() {

        EditText input = new EditText(this);
        input.setHint("Equipment Type");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Equipment Type")
                .setCancelable(false)
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {

                    String newType = input.getText().toString().trim();
                    if (!newType.isEmpty()){
                        saveNewType(newType);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void saveNewType(String newType) {

        Map<String, Object> data = new HashMap<>();
        data.put("typeName", newType);

        db.collection("equipmentTypes")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Type Added Successfully", Toast.LENGTH_LONG).show();

                    loadEquipmentTypes();
                    typeDropdown.setText(newType, false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadEquipmentTypes() {
        db.collection("equipmentTypes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    equipmentTypes.clear();

                    for (DocumentSnapshot doc : snapshot){

                        String type = doc.getString("typeName");
                        if (type != null){
                            equipmentTypes.add(type);
                        }
                    }

                    equipmentTypes.add("+ Add New Type");
                    typeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void loadLabs(){

        db.collection("labs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    labNames.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){
                        String labName = doc.getString("labName");

                        if (labName != null){
                            labNames.add(labName);
                        }
                    }
                    labAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void generateEquipmentId(){
        db.collection("equipment")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int count = snapshot.size() + 1;
                    qrId = String.format("EQ%06d", count);

                    Bitmap qrBitmap = generateQRCode(qrId);

                    equipmentQR.setImageBitmap(qrBitmap);
                    equipmentIdText.setText(qrId);

                });
    }

    private Bitmap generateQRCode(String text){
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 400, 400);

            Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);

            for (int x = 0; x < 400; x++){
                for (int y = 0; y < 400; y++){

                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;

        } catch (Exception e){
            return null;
        }
    }
}