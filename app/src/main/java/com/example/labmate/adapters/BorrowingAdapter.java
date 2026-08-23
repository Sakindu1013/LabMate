package com.example.labmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.models.Borrowing;
import com.example.labmate.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BorrowingAdapter
        extends RecyclerView.Adapter<BorrowingAdapter.ViewHolder> {

    public interface BorrowingActionListener {

        void onCheckout(Borrowing borrowing);
    }

    private final ArrayList<Borrowing> borrowingList;
    private final boolean showCheckout;
    private final boolean showUserName;
    private final BorrowingActionListener listener;

    public BorrowingAdapter(
            ArrayList<Borrowing> borrowingList,
            boolean showCheckout,
            boolean showUserName,
            BorrowingActionListener listener
    ) {

        this.borrowingList = borrowingList;
        this.showCheckout = showCheckout;
        this.showUserName = showUserName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_active_borrowing,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Borrowing borrowing =
                borrowingList.get(position);

        // Equipment information
        holder.equipmentName.setText(
                borrowing.getEquipmentName() != null
                        ? borrowing.getEquipmentName()
                        : "Unknown Equipment"
        );

        holder.equipmentModel.setText(
                borrowing.getEquipmentModel() != null
                        ? "Model: " + borrowing.getEquipmentModel()
                        : "Model: Unknown"
        );

        holder.equipmentQrId.setText(
                borrowing.getEquipmentQrId() != null
                        ? "QR ID: " + borrowing.getEquipmentQrId()
                        : "QR ID: Unknown"
        );

        holder.lab.setText(
                borrowing.getLab() != null
                        ? "Lab: " + borrowing.getLab()
                        : "Lab: Unknown"
        );

        // User information
        if (showUserName) {

            holder.userName.setVisibility(View.VISIBLE);

            holder.userName.setText(
                    borrowing.getUserName() != null
                            ? "Borrowed by: " + borrowing.getUserName()
                            : "Borrowed by: Unknown User"
            );

        } else {

            holder.userName.setVisibility(View.GONE);
        }

        // Created/requested date
        if (borrowing.getCreatedAt() != null) {

            holder.createdAt.setText(
                    "Requested: "
                            + borrowing.getCreatedAt()
                            .toDate()
                            .toString()
            );

        } else {

            holder.createdAt.setText(
                    "Requested: Unknown"
            );
        }

        // Borrowed date
        if (borrowing.getBorrowedAt() != null) {

            holder.borrowedAt.setText(
                    "Borrowed: "
                            + borrowing.getBorrowedAt()
                            .toDate()
                            .toString()
            );

        } else {

            holder.borrowedAt.setText(
                    "Borrowed: Not checked out"
            );
        }

        // Returned date
        if (borrowing.getReturnedAt() != null) {

            holder.returnedAt.setText(
                    "Returned: "
                            + borrowing.getReturnedAt()
                            .toDate()
                            .toString()
            );

        } else {

            holder.returnedAt.setText(
                    "Returned: Not returned"
            );
        }

        // Status
        holder.status.setText(
                "Status: "
                        + borrowing.getStatus()
        );

        // Checkout button
        if (showCheckout
                && Constants.BORROWING_ACTIVE.equals(
                borrowing.getStatus()
        )) {

            holder.checkoutButton.setVisibility(
                    View.VISIBLE
            );

            holder.checkoutButton.setOnClickListener(
                    v -> {

                        if (listener != null) {

                            listener.onCheckout(
                                    borrowing
                            );
                        }
                    }
            );

        } else {

            holder.checkoutButton.setVisibility(
                    View.GONE
            );

            holder.checkoutButton.setOnClickListener(
                    null
            );
        }
    }

    @Override
    public int getItemCount() {

        return borrowingList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView equipmentName;
        TextView equipmentModel;
        TextView equipmentQrId;
        TextView lab;
        TextView userName;
        TextView createdAt;
        TextView borrowedAt;
        TextView returnedAt;
        TextView status;

        MaterialButton checkoutButton;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            equipmentName =
                    itemView.findViewById(
                            R.id.borrowingEquipmentName
                    );

            equipmentModel =
                    itemView.findViewById(
                            R.id.borrowingEquipmentModel
                    );

            equipmentQrId =
                    itemView.findViewById(
                            R.id.borrowingEquipmentQrId
                    );

            lab =
                    itemView.findViewById(
                            R.id.borrowingLab
                    );

            userName =
                    itemView.findViewById(
                            R.id.borrowingUserName
                    );

            createdAt =
                    itemView.findViewById(
                            R.id.borrowingCreatedAt
                    );

            borrowedAt =
                    itemView.findViewById(
                            R.id.borrowingBorrowedAt
                    );

            returnedAt =
                    itemView.findViewById(
                            R.id.borrowingReturnedAt
                    );

            status =
                    itemView.findViewById(
                            R.id.borrowingStatus
                    );

            checkoutButton =
                    itemView.findViewById(
                            R.id.btn_checkout
                    );
        }
    }
}