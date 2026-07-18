package com.example.labmate.dialogs;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.labmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EquipmentPreviewDialog {

    public interface ActionListener{
        void onConfirm();
    }

    public static void show(Context context, String name, String model, String lab, String state, String actionText, ActionListener listener){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_equipment_preview, null);

        TextView equipmentName = view.findViewById(R.id.previewName);
        TextView equipmentModel = view.findViewById(R.id.previewModel);
        TextView equipmentLab = view.findViewById(R.id.previewLab);
        TextView equipmentState = view.findViewById(R.id.previewState);

        equipmentName.setText(name);
        equipmentModel.setText("Model: " + model);
        equipmentLab.setText("Laboratory: " + lab);
        equipmentState.setText("State: " + state);

        new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(actionText,
                        (dialog, which) -> listener.onConfirm())
                .show();
    }
}
