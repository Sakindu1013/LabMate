package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;

public class LabDetailsActivity extends AppCompatActivity {

    TextView viewName;
    TextView viewInCharge;
    TextView viewlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String labId = getIntent().getStringExtra("LAB_ID");
        String labName = getIntent().getStringExtra("LAB_NAME");
        String labInCharge = getIntent().getStringExtra("LAB_IN_CHARGE");
        String labLocation = getIntent().getStringExtra("LAB_LOCATION");

        viewName = findViewById(R.id.labName);
        viewInCharge = findViewById(R.id.personInCharge);
        viewlocation = findViewById(R.id.actLocation);

        viewName.setText(labName);
        viewInCharge.setText("In Charge: " + labInCharge);
        viewlocation.setText("Location: " + labLocation);
    }
}