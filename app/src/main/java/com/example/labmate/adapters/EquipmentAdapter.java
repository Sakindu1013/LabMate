package com.example.labmate.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.activities.EditEquipmentActivity;
import com.example.labmate.models.Equipment;
import com.example.labmate.utils.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private final Context context;
    private final ArrayList<Equipment> equipmentList;
    private final boolean isAdmin;

    public EquipmentAdapter(
            Context context,
            ArrayList<Equipment> equipmentList) {

        this.context = context;
        this.equipmentList = equipmentList;

        UserSession session = new UserSession(context);
        this.isAdmin = session.isAdmin();
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_detailed_equipment,
                        parent,
                        false
                );

        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EquipmentViewHolder holder,
            int position) {

        Equipment equipment = equipmentList.get(position);

        holder.name.setText(
                equipment.getEquipmentName()
        );

        holder.model.setText(
                "Model: " + equipment.getEquipmentModel()
        );

        holder.lab.setText(
                "Laboratory: " + equipment.getLab()
        );

        holder.state.setText(
                "State: " + equipment.getState()
        );

        holder.qrId.setText(
                "QR ID: " + equipment.getQrId()
        );

        // Only administrators can edit equipment.
        holder.edit.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        // Long press → show QR code
        holder.itemView.setOnLongClickListener(v -> {
            showQRCode(equipment);
            return true;
        });

        // Edit equipment
        holder.edit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    EditEquipmentActivity.class
            );

            intent.putExtra(
                    "EQUIPMENT_NAME",
                    equipment.getEquipmentName()
            );

            intent.putExtra(
                    "EQUIPMENT_MODEL",
                    equipment.getEquipmentModel()
            );

            intent.putExtra(
                    "QR_ID",
                    equipment.getQrId()
            );

            intent.putExtra(
                    "LABORATORY",
                    equipment.getLab()
            );

            intent.putExtra(
                    "STATE",
                    equipment.getState()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return equipmentList.size();
    }

    private void showQRCode(Equipment equipment) {

        Dialog dialog = new Dialog(context);

        dialog.setContentView(
                R.layout.dialog_qr
        );

        ImageView qrImage = dialog.findViewById(
                R.id.qrImage
        );

        TextView qrText = dialog.findViewById(
                R.id.qrText
        );

        String qrValue = equipment.getQrId();

        Bitmap bitmap = generateQRCode(qrValue);

        qrImage.setImageBitmap(bitmap);

        qrText.setText(
                "QR ID: " + qrValue
        );

        dialog.show();
    }

    private Bitmap generateQRCode(String qrValue) {

        if (qrValue == null || qrValue.isEmpty()) {
            return null;
        }

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

        try {
            return barcodeEncoder.encodeBitmap(
                    qrValue,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class EquipmentViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView model;
        TextView lab;
        TextView state;
        TextView qrId;

        MaterialButton edit;

        public EquipmentViewHolder(@NonNull View itemView) {

            super(itemView);

            name = itemView.findViewById(
                    R.id.equipmentName
            );

            model = itemView.findViewById(
                    R.id.equipmentModel
            );

            lab = itemView.findViewById(
                    R.id.equipmentLab
            );

            state = itemView.findViewById(
                    R.id.equipmentState
            );

            qrId = itemView.findViewById(
                    R.id.equipmentQR
            );

            edit = itemView.findViewById(
                    R.id.edit_equipment
            );
        }
    }
}