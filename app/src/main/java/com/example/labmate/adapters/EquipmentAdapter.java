package com.example.labmate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.EditEquipmentActivity;
import com.example.labmate.R;
import com.example.labmate.activities.EquipmentDetailsActivity;
import com.example.labmate.models.Equipment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Equipment> equipments;
    private boolean isAdmin;

    public EquipmentAdapter(Context context, ArrayList<Equipment> equipments, boolean isAdmin){
        this.context = context;
        this.equipments = equipments;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.item_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Equipment equipment = equipments.get(position);
        holder.name.setText(equipment.getName());
        holder.type.setText("Type: " + equipment.getType());
        holder.state.setText("State: " + equipment.getState());
        holder.lab.setText("Lab: " + equipment.getLab());

        if (isAdmin){
            holder.edit.setVisibility(View.VISIBLE);
        } else {
            holder.edit.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent equipmentIntent = new Intent(context, EquipmentDetailsActivity.class);
            equipmentIntent.putExtra("EQUIP_ID", equipment.getId());
            equipmentIntent.putExtra("EQUIP_NAME", equipment.getName());
            equipmentIntent.putExtra("EQUIP_TYPE", equipment.getType());
            equipmentIntent.putExtra("EQUIP_STATE", equipment.getState());
            equipmentIntent.putExtra("EQUIP_LAB", equipment.getLab());
            context.startActivity(equipmentIntent);
        });

        holder.edit.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EditEquipmentActivity.class);
            editIntent.putExtra("EQUIP_ID", equipment.getId());
            editIntent.putExtra("EQUIP_NAME", equipment.getName());
            editIntent.putExtra("EQUIP_TYPE", equipment.getType());
            editIntent.putExtra("EQUIP_STATE", equipment.getState());
            editIntent.putExtra("EQUIP_LAB", equipment.getLab());
            context.startActivity(editIntent);
        });
    }

    @Override
    public int getItemCount(){
        return equipments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, type, state, lab;
        MaterialButton edit;

        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.equipmentName);
            type = itemView.findViewById(R.id.equipmentType);
            state = itemView.findViewById(R.id.equipmentState);
            lab = itemView.findViewById(R.id.equipmentLab);

            edit = itemView.findViewById(R.id.editEquipButton);

            edit.setVisibility(View.GONE);
        }
    }
}
