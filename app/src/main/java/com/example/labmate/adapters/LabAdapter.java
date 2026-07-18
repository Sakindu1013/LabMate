package com.example.labmate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.activities.EditLabActivity;
import com.example.labmate.R;
import com.example.labmate.activities.LabDetailsActivity;
import com.example.labmate.models.Lab;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Lab> labs;
    private boolean isAdmin;

    public LabAdapter(Context context, ArrayList<Lab> labs, boolean isAdmin){
        this.context = context;
        this.labs = labs;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.item_lab, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Lab lab = labs.get(position);
        holder.name.setText(lab.getName());
        holder.inCharge.setText("In Charge: " + lab.getInCharge());
        holder.location.setText("Location: " + lab.getLocation());

        holder.edit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent labIntent = new Intent(context, LabDetailsActivity.class);
            labIntent.putExtra("LAB_NAME", lab.getName());
            labIntent.putExtra("LAB_IN_CHARGE", lab.getInCharge());
            labIntent.putExtra("LAB_LOCATION", lab.getLocation());
            context.startActivity(labIntent);
        });

        holder.edit.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EditLabActivity.class);
            editIntent.putExtra("LAB_ID", lab.getLabId());
            editIntent.putExtra("LAB_NAME", lab.getName());
            editIntent.putExtra("LAB_IN_CHARGE", lab.getInCharge());
            editIntent.putExtra("LAB_LOCATION", lab.getLocation());
            context.startActivity(editIntent);
        });
    }

    @Override
    public int getItemCount(){
        return labs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, inCharge, location;
        MaterialButton edit;

        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.labName);
            inCharge = itemView.findViewById(R.id.labInCharge);
            location = itemView.findViewById(R.id.labLocation);
            edit = itemView.findViewById(R.id.editLabButton);

            edit.setVisibility(View.GONE);
        }
    }
}
