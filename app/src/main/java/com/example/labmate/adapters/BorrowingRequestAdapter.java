package com.example.labmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.utils.Constants;
import com.google.android.material.button.MaterialButton;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BorrowingRequestAdapter
        extends RecyclerView.Adapter<
        BorrowingRequestAdapter.ViewHolder> {

    public interface RequestActionListener {

        void onApprove(BorrowingRequest request);

        void onReject(BorrowingRequest request);
    }

    private final ArrayList<BorrowingRequest> requestList;
    private final boolean showActions;
    private final boolean showUserName;
    private final RequestActionListener listener;

    public BorrowingRequestAdapter(
            ArrayList<BorrowingRequest> requestList,
            boolean showActions,
            boolean showUserName,
            RequestActionListener listener
    ) {

        this.requestList = requestList;
        this.showActions = showActions;
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
                                R.layout.item_borrowing_request,
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

        BorrowingRequest request =
                requestList.get(position);

        holder.requestId.setText(
                request.getEquipmentName() != null
                        ? request.getEquipmentName()
                        : "Unknown Equipment"
        );

        holder.equipmentId.setText(
                request.getEquipmentModel() != null
                        ? "Model: " + request.getEquipmentModel()
                        : "Model: Unknown"
        );

        holder.equipmentQrId.setText(
                request.getEquipmentQrId() != null
                        ? "QR ID: " + request.getEquipmentQrId()
                        : "QR ID: Unknown"
        );

        if (showUserName) {

            holder.userId.setVisibility(View.VISIBLE);

            holder.userId.setText(
                    request.getUserName() != null
                            ? "Requested by: " + request.getUserName()
                            : "Requested by: Unknown User"
            );

        } else {

            holder.userId.setVisibility(View.GONE);
        }

        if (request.getRequestedAt() != null) {

            holder.requestedAt.setText(
                    "Requested: "
                            + request.getRequestedAt()
                            .toDate()
                            .toString()
            );

        } else {

            holder.requestedAt.setText(
                    "Requested: Unknown"
            );
        }

        String status =
                request.getStatus();

        holder.requestStatus.setText(
                "Status: "
                        + (
                        status != null
                                ? status
                                : "Unknown"
                )
        );

        boolean canManageRequest =
                showActions
                        && Constants.REQUEST_PENDING.equalsIgnoreCase(
                        status
                );

        if (canManageRequest) {

            holder.approveButton.setVisibility(
                    View.VISIBLE
            );

            holder.rejectButton.setVisibility(
                    View.VISIBLE
            );

            holder.approveButton.setOnClickListener(
                    v -> {

                        if (listener != null) {
                            listener.onApprove(request);
                        }
                    }
            );

            holder.rejectButton.setOnClickListener(
                    v -> {

                        if (listener != null) {
                            listener.onReject(request);
                        }
                    }
            );


        } else {

            holder.approveButton.setVisibility(
                    View.GONE
            );

            holder.rejectButton.setVisibility(
                    View.GONE
            );

            holder.approveButton.setOnClickListener(
                    null
            );

            holder.rejectButton.setOnClickListener(
                    null
            );
        }
    }

    @Override
    public int getItemCount() {

        return requestList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView requestId;
        TextView equipmentId;
        TextView equipmentQrId;
        TextView userId;
        TextView requestedAt;
        TextView requestStatus;

        MaterialButton approveButton;
        MaterialButton rejectButton;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            requestId =
                    itemView.findViewById(
                            R.id.requestId
                    );

            equipmentId =
                    itemView.findViewById(
                            R.id.requestEquipmentId
                    );

            equipmentQrId =
                    itemView.findViewById(
                            R.id.requestEquipmentQrId
                    );

            userId =
                    itemView.findViewById(
                            R.id.requestUserId
                    );

            requestedAt =
                    itemView.findViewById(
                            R.id.requestedAt
                    );

            requestStatus =
                    itemView.findViewById(
                            R.id.requestStatus
                    );

            approveButton =
                    itemView.findViewById(
                            R.id.btn_approve
                    );

            rejectButton =
                    itemView.findViewById(
                            R.id.btn_reject
                    );
        }
    }
}