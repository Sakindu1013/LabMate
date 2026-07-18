package com.example.labmate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.activities.EquipmentDetailsActivity;
import com.example.labmate.models.EquipmentSummary;

import java.util.ArrayList;

public class EquipmentSummaryAdapter extends RecyclerView.Adapter<EquipmentSummaryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EquipmentSummary> equipments;
    private String labName;

    public EquipmentSummaryAdapter(Context context, ArrayList<EquipmentSummary> equipments, String labName){
        this.context = context;
        this.equipments = equipments;
        this.labName = labName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.item_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        EquipmentSummary equipment = equipments.get(position);

        holder.type.setText(equipment.getType());
        holder.total.setText("Total: " + equipment.getTotal());
        holder.inLab.setText("In Lab: " + equipment.getInLab());
        holder.borrowed.setText("Borrowed: " + equipment.getBorrowed());
        holder.maintenance.setText("Under Maintenance: " + equipment.getMaintenance());
        holder.removed.setText("Removed: " + equipment.getRemoved());

        holder.itemView.setOnClickListener(v -> {
            Intent equipmentIntent = new Intent(context, EquipmentDetailsActivity.class);
            equipmentIntent.putExtra("TYPE",equipment.getType());
            equipmentIntent.putExtra("TOTAL",equipment.getTotal());
            equipmentIntent.putExtra("IN_LAB",equipment.getInLab());
            equipmentIntent.putExtra("BORROWED",equipment.getBorrowed());
            equipmentIntent.putExtra("MAINTENANCE",equipment.getMaintenance());
            equipmentIntent.putExtra("REMOVED",equipment.getRemoved());
            equipmentIntent.putExtra("LAB_NAME", labName);
            context.startActivity(equipmentIntent);
        });
    }

    @Override
    public int getItemCount(){
        return equipments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView type, total, inLab, borrowed, maintenance, removed;

        public ViewHolder(View itemView){
            super(itemView);

            type = itemView.findViewById(R.id.equipmentType);
            total = itemView.findViewById(R.id.equipmentTotal);
            inLab = itemView.findViewById(R.id.equipmentInLab);
            borrowed = itemView.findViewById(R.id.equipmentBorrowed);
            maintenance = itemView.findViewById(R.id.equipmentMaintenance);
            removed = itemView.findViewById(R.id.equipmentRemoved);
        }
    }
}
