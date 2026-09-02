package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRScannerActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {

                        if (result.getContents() != null) {

                            Intent intent = new Intent();

                            intent.putExtra(
                                    "QR_ID",
                                    result.getContents()
                            );

                            setResult(
                                    RESULT_OK,
                                    intent
                            );

                        } else {

                            setResult(RESULT_CANCELED);
                        }

                        finish();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanOptions scanOptions = new ScanOptions();

        scanOptions.setDesiredBarcodeFormats(
                ScanOptions.QR_CODE
        );

        scanOptions.setPrompt("Scan Equipment QR Code");

        scanOptions.setBeepEnabled(true);

        // Allow scanner to follow the device orientation
        scanOptions.setOrientationLocked(false);

        barcodeLauncher.launch(scanOptions);
    }
}