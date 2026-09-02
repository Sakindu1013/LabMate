package com.example.labmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.models.User;
import com.example.labmate.utils.Constants;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.UserViewHolder> {

    private final List<User> users = new ArrayList<>();
    private final OnRoleChangeListener listener;

    public ManageUserAdapter(
            OnRoleChangeListener listener
    ) {

        this.listener = listener;
    }

    public void setUsers(
            List<User> newUsers
    ) {

        users.clear();

        if (newUsers != null) {
            users.addAll(newUsers);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_manage_user,
                        parent,
                        false
                );

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position
    ) {

        User user = users.get(position);

        // Name
        holder.tvUserName.setText(
                user.getName() != null
                        ? user.getName()
                        : ""
        );

        // Email
        holder.tvUserEmail.setText(
                user.getEmail() != null
                        ? user.getEmail()
                        : ""
        );

        // Mobile
        String mobile = user.getMobile();

        if (mobile != null && !mobile.trim().isEmpty()) {

            holder.tvUserMobile.setVisibility(View.VISIBLE);

            holder.tvUserMobile.setText(
                    mobile
            );

        } else {

            holder.tvUserMobile.setVisibility(View.GONE);
        }

        // Roles
        String[] roles = {
                Constants.ROLE_STUDENT,
                Constants.ROLE_STAFF,
                Constants.ROLE_ADMIN
        };

        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(
                        holder.itemView.getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        roles
                );

        holder.spinnerRole.setAdapter(roleAdapter);

        // Show current role
        String currentRole = user.getRole();

        if (currentRole != null && !currentRole.isEmpty()) {

            holder.spinnerRole.setText(
                    currentRole,
                    false
            );
        }

        // Role selection
        holder.spinnerRole.setOnItemClickListener(
                (parent, view, selectedPosition, id) -> {

                    String newRole = roles[selectedPosition];

                    if (!newRole.equals(user.getRole())) {

                        listener.onRoleChange(
                                user,
                                newRole
                        );
                    }
                }
        );
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvUserMobile;

        MaterialAutoCompleteTextView spinnerRole;

        public UserViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserMobile = itemView.findViewById(R.id.tvUserMobile);
            spinnerRole = itemView.findViewById(R.id.spinnerRole);
        }
    }

    public interface OnRoleChangeListener {

        void onRoleChange(
                User user,
                String newRole
        );
    }
}