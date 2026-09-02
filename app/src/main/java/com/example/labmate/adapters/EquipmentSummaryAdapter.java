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

    private final Context context;
    private final ArrayList<EquipmentSummary> equipmentList;
    private final String labName;

    public EquipmentSummaryAdapter(
            Context context,
            ArrayList<EquipmentSummary> equipmentList,
            String labName) {

        this.context = context;
        this.equipmentList = equipmentList;
        this.labName = labName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_equipment,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        EquipmentSummary equipment = equipmentList.get(position);

        holder.type.setText(equipment.getType());

        holder.total.setText(
                "Total: " + equipment.getTotal()
        );

        holder.inLab.setText(
                "In Lab: " + equipment.getInLab()
        );

        holder.borrowed.setText(
                "Borrowed: " + equipment.getBorrowed()
        );

        holder.maintenance.setText(
                "Under Maintenance: " + equipment.getMaintenance()
        );

        holder.removed.setText(
                "Removed: " + equipment.getRemoved()
        );

        holder.reserved.setText(
                "Reserved: " + equipment.getReserved()
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    EquipmentDetailsActivity.class
            );

            intent.putExtra(
                    "TYPE",
                    equipment.getType()
            );

            intent.putExtra(
                    "LAB_NAME",
                    labName
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return equipmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView total;
        TextView inLab;
        TextView borrowed;
        TextView maintenance;
        TextView removed;
        TextView reserved;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            type = itemView.findViewById(
                    R.id.equipmentType
            );

            total = itemView.findViewById(
                    R.id.equipmentTotal
            );

            inLab = itemView.findViewById(
                    R.id.equipmentInLab
            );

            borrowed = itemView.findViewById(
                    R.id.equipmentBorrowed
            );

            maintenance = itemView.findViewById(
                    R.id.equipmentMaintenance
            );

            removed = itemView.findViewById(
                    R.id.equipmentRemoved
            );

            reserved = itemView.findViewById(
                    R.id.equipmentReserved
            );
        }
    }
}