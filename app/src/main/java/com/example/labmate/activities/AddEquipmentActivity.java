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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
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
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;
    private ImageView equipmentQR;
    private TextView equipmentIdText;

    private String qrId;

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

        equipmentQR = findViewById(R.id.equipmentQR);
        equipmentIdText = findViewById(R.id.equipmentIdText);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", 0);
        String role = prefs.getString("role", "");
        String username = prefs.getString("name","");

        AutoCompleteTextView typeDropdown = findViewById(R.id.actType);
        String[] types = getResources().getStringArray(R.array.equipment_types);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, types);
        typeDropdown.setAdapter(typeAdapter);

        AutoCompleteTextView stateDropdown = findViewById(R.id.actState);
        String[] states = getResources().getStringArray(R.array.equipment_states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, states);
        stateDropdown.setAdapter(stateAdapter);

        actLab = findViewById(R.id.actLab);
        db = FirebaseFirestore.getInstance();
        labNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labNames);
        actLab.setAdapter(adapter);

        EditText equipmentName, equipmentModel;
        AutoCompleteTextView equipmentLab, equipmentState;
        equipmentName = findViewById(R.id.equipmentName);
        equipmentModel = findViewById(R.id.equipmentModel);
        equipmentLab = actLab;

        equipmentLab.setOnItemClickListener((parent, view, position, id) -> {
            equipmentLab.clearFocus();
        });

        typeDropdown.setOnItemClickListener((parent, view, position, id) -> {
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

                db.collection("equipments")
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
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void generateEquipmentId(){
        db.collection("equipments")
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