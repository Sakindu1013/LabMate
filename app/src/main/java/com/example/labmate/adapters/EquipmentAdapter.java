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
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private Context context;
    private ArrayList<Equipment> equipmentList;

    public EquipmentAdapter(Context context, ArrayList<Equipment> equipmentList){
        this.context = context;
        this.equipmentList = equipmentList;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.item_detailed_equipment, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position){
        Equipment equipment = equipmentList.get(position);

        holder.name.setText(equipment.getEquipmentName());
        holder.model.setText("Model: " + equipment.getEquipmentModel());
        holder.lab.setText("Laboratory: " + equipment.getLab());
        holder.state.setText("State: " + equipment.getState());
        holder.qrId.setText("QR ID: " + equipment.getQrId());

        holder.itemView.setOnLongClickListener(v -> {
            showQRCode(equipment);
            return true;
        });

        holder.edit.setOnClickListener(v -> {
            Intent editEquipmentIntent = new Intent(context, EditEquipmentActivity.class);
            editEquipmentIntent.putExtra("EQUIPMENT_NAME", equipment.getEquipmentName());
            editEquipmentIntent.putExtra("EQUIPMENT_MODEL", equipment.getEquipmentModel());
            editEquipmentIntent.putExtra("QR_ID", equipment.getQrId());
            editEquipmentIntent.putExtra("LABORATORY", equipment.getLab());
            editEquipmentIntent.putExtra("STATE", equipment.getState());
            context.startActivity(editEquipmentIntent);
        });

    }

    @Override
    public int getItemCount(){
        return equipmentList.size();
    }

    public class EquipmentViewHolder extends RecyclerView.ViewHolder{

        TextView name, model, lab, state, qrId;
        MaterialButton edit;

        public EquipmentViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.equipmentName);
            model = itemView.findViewById(R.id.equipmentModel);
            lab = itemView.findViewById(R.id.equipmentLab);
            state = itemView.findViewById(R.id.equipmentState);
            qrId = itemView.findViewById(R.id.equipmentQR);
            edit = itemView.findViewById(R.id.edit_equipment);
        }
    }

    public void showQRCode(Equipment equipment){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_qr);

        ImageView qrImage = dialog.findViewById(R.id.qrImage);
        TextView qrText = dialog.findViewById(R.id.qrText);

        String qrValue = equipment.getQrId();
        Bitmap bitmap = generateQRCode(qrValue);

        qrImage.setImageBitmap(bitmap);
        qrText.setText("QR ID: " + qrValue);

        dialog.show();
    }

    private Bitmap generateQRCode(String qrValue) {

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

        try {
            return barcodeEncoder.encodeBitmap(qrValue, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
