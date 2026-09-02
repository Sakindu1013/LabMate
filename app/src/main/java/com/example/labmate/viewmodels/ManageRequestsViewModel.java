package com.example.labmate.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labmate.models.Borrowing;
import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.repositories.BorrowingRepository;
import com.example.labmate.repositories.BorrowingRequestRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.repositories.UserRepository;
import com.example.labmate.utils.Constants;
import com.example.labmate.utils.UserSession;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageRequestsViewModel extends AndroidViewModel {

    private final BorrowingRequestRepository requestRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final UserSession userSession;

    private final MutableLiveData<ArrayList<BorrowingRequest>> requests =
            new MutableLiveData<>(
                    new ArrayList<>()
            );

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();

    public ManageRequestsViewModel(
            @NonNull Application application
    ) {

        super(application);

        requestRepository = new BorrowingRequestRepository();
        equipmentRepository = new EquipmentRepository();
        borrowingRepository = new BorrowingRepository();
        userRepository = new UserRepository();
        userSession = new UserSession(application);
    }

    // ============================================================
    // LIVE DATA
    // ============================================================

    public LiveData<ArrayList<BorrowingRequest>> getRequests() {

        return requests;
    }

    public LiveData<Boolean> getLoading() {

        return loading;
    }

    public LiveData<String> getErrorMessage() {

        return errorMessage;
    }

    // ============================================================
    // LOAD REQUESTS
    // ============================================================

    public void loadRequests() {

        loading.setValue(true);

        errorMessage.setValue(null);

        String userId = userSession.getUserId();

        String role = userSession.getRole();

        if (userId == null || userId.trim().isEmpty()) {

            loading.setValue(false);

            errorMessage.setValue(
                    "User not found."
            );

            return;
        }

        if (Constants.ROLE_STUDENT.equalsIgnoreCase(role)) {

            loadStudentRequests(userId);

            return;
        }

        if (Constants.ROLE_STAFF.equalsIgnoreCase(role)
                || Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {

            loadAllRequests();

            return;
        }

        loading.setValue(false);

        errorMessage.setValue(
                "Invalid user role."
        );
    }

    // ============================================================
    // LOAD STUDENT REQUESTS
    // ============================================================

    private void loadStudentRequests(
            String userId
    ) {

        requestRepository.getRequestsByUser(
                userId,

                this::convertRequests,

                e -> {

                    loading.setValue(false);

                    errorMessage.setValue(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Failed to load borrowing requests."
                    );
                }
        );
    }

    // ============================================================
    // LOAD ALL REQUESTS
    // ============================================================

    private void loadAllRequests() {

        requestRepository.getAllRequests(
                this::convertRequests,

                e -> {

                    loading.setValue(false);

                    errorMessage.setValue(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Failed to load borrowing requests."
                    );
                }
        );
    }

    // ============================================================
    // CONVERT FIRESTORE DOCUMENTS
    // ============================================================

    private void convertRequests(
            QuerySnapshot snapshot
    ) {

        ArrayList<BorrowingRequest> requestList =
                new ArrayList<>();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            BorrowingRequest request =
                    document.toObject(
                            BorrowingRequest.class
                    );

            if (request == null) {
                continue;
            }

            request.setId(
                    document.getId()
            );

            requestList.add(request);
        }

        loadRequestDetails(
                requestList,
                0
        );
    }

    private void loadRequestDetails(
            ArrayList<BorrowingRequest> requestList,
            int index
    ) {

        if (index >= requestList.size()) {

            requests.setValue(requestList);

            loading.setValue(false);

            return;
        }

        BorrowingRequest request =
                requestList.get(index);

        loadEquipmentDetails(
                request,

                () -> loadUserDetails(
                        request,

                        () -> loadRequestDetails(
                                requestList,
                                index + 1
                        )
                )
        );
    }

    private void loadEquipmentDetails(
            BorrowingRequest request,
            Runnable onComplete
    ) {

        if (request.getEquipmentId() == null
                || request.getEquipmentId().trim().isEmpty()) {

            onComplete.run();
            return;
        }

        equipmentRepository.getById(
                request.getEquipmentId(),

                equipment -> {

                    if (equipment != null
                            && equipment.exists()) {

                        request.setEquipmentName(
                                equipment.getString(
                                        "equipmentName"
                                )
                        );

                        request.setEquipmentModel(
                                equipment.getString(
                                        "equipmentModel"
                                )
                        );

                        request.setEquipmentQrId(
                                equipment.getString(
                                        "qrId"
                                )
                        );
                    }

                    onComplete.run();
                },

                e -> {

                    // Continue even if equipment details
                    // cannot be loaded.
                    onComplete.run();
                }
        );
    }

    private void loadUserDetails(
            BorrowingRequest request,
            Runnable onComplete
    ) {

        if (request.getUserId() == null
                || request.getUserId().trim().isEmpty()) {

            onComplete.run();
            return;
        }

        userRepository.getById(
                request.getUserId(),

                user -> {

                    if (user != null
                            && user.exists()) {

                        request.setUserName(
                                user.getString("name")
                        );
                    }

                    onComplete.run();
                },

                e -> {

                    // Continue even if user details
                    // cannot be loaded.
                    onComplete.run();
                }
        );
    }

    // ============================================================
    // STAFF / ADMIN - APPROVE REQUEST
    // ============================================================

    public void approveRequest(
            String requestId
    ) {

        if (requestId == null
                || requestId.trim().isEmpty()) {

            errorMessage.setValue(
                    "Request information is missing."
            );

            return;
        }

        requestRepository.getById(
                requestId,

                request -> {

                    if (request == null) {

                        errorMessage.setValue(
                                "Borrowing request not found."
                        );

                        return;
                    }

                    /*
                     * Only pending requests can be approved.
                     */
                    if (!Constants.REQUEST_PENDING.equals(
                            request.getStatus()
                    )) {

                        errorMessage.setValue(
                                "This request has already been processed."
                        );

                        return;
                    }

                    validateEquipmentForApproval(
                            requestId,
                            request
                    );
                },

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load borrowing request."
                )
        );
    }

    // ============================================================
    // VALIDATE EQUIPMENT BEFORE APPROVAL
    // ============================================================

    private void validateEquipmentForApproval(
            String requestId,
            BorrowingRequest request
    ) {

        String equipmentId =
                request.getEquipmentId();

        if (equipmentId == null
                || equipmentId.trim().isEmpty()) {

            errorMessage.setValue(
                    "Equipment information is missing."
            );

            return;
        }

        equipmentRepository.getById(
                equipmentId,

                equipment -> {

                    if (equipment == null
                            || !equipment.exists()) {

                        rejectUnavailableRequest(
                                requestId,
                                "Equipment not found."
                        );

                        return;
                    }

                    String state =
                            equipment.getString("state");

                    /*
                     * The equipment must still physically
                     * be in the lab at the moment the
                     * Staff/Admin approves the request.
                     */
                    if (!Constants.STATE_IN_LAB.equals(
                            state
                    )) {

                        rejectUnavailableRequest(
                                requestId,
                                "This equipment is no longer available. "
                                        + "The request has been rejected."
                        );

                        return;
                    }

                    createBorrowing(
                            requestId,
                            request
                    );
                },

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load equipment."
                )
        );
    }

    // ============================================================
    // CREATE BORROWING AFTER REQUEST APPROVAL
    // ============================================================

    private void createBorrowing(
            String requestId,
            BorrowingRequest request
    ) {

        Timestamp now =
                Timestamp.now();

        /*
         * The equipment has NOT been physically
         * checked out yet.
         *
         * Therefore:
         *
         * createdAt  = now
         * borrowedAt = null
         * returnedAt  = null
         * status      = Active
         */
        Borrowing borrowing =
                new Borrowing(
                        request.getEquipmentId(),
                        request.getUserId(),
                        now,
                        null,
                        null,
                        Constants.BORROWING_ACTIVE
                );

        borrowingRepository.add(
                borrowing,

                borrowingId ->
                        reserveEquipment(
                                requestId,
                                request.getEquipmentId()
                        ),

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to create borrowing record."
                )
        );
    }

    // ============================================================
    // RESERVE EQUIPMENT
    // ============================================================

    private void reserveEquipment(
            String requestId,
            String equipmentId
    ) {

        equipmentRepository.updateState(
                equipmentId,
                Constants.STATE_RESERVED,

                () -> acceptRequestAndRejectOthers(
                        requestId,
                        equipmentId
                ),

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to reserve equipment."
                )
        );
    }

    // ============================================================
    // ACCEPT REQUEST + REJECT OTHER REQUESTS
    // ============================================================

    private void acceptRequestAndRejectOthers(
            String requestId,
            String equipmentId
    ) {

        requestRepository.getPendingRequestsForEquipment(
                equipmentId,

                snapshot -> {

                    ArrayList<String> otherRequestIds =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        if (!document.getId().equals(
                                requestId
                        )) {

                            otherRequestIds.add(
                                    document.getId()
                            );
                        }
                    }

                    updateAcceptedRequest(
                            requestId,
                            otherRequestIds
                    );
                },

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to process other requests."
                )
        );
    }

    // ============================================================
    // UPDATE ACCEPTED REQUEST
    // ============================================================

    private void updateAcceptedRequest(
            String requestId,
            ArrayList<String> otherRequestIds
    ) {

        requestRepository.updateStatus(
                requestId,
                Constants.REQUEST_ACCEPTED,

                () -> rejectOtherRequests(
                        otherRequestIds,

                        () -> {

                            errorMessage.setValue(
                                    "Borrowing request approved successfully."
                            );

                            loadRequests();
                        }
                ),

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to accept request."
                )
        );
    }

    // ============================================================
    // REJECT OTHER REQUESTS
    // ============================================================

    private void rejectOtherRequests(
            ArrayList<String> requestIds,
            Runnable onComplete
    ) {

        if (requestIds.isEmpty()) {

            onComplete.run();

            return;
        }

        rejectNextRequest(
                requestIds,
                0,
                onComplete
        );
    }

    private void rejectNextRequest(
            ArrayList<String> requestIds,
            int index,
            Runnable onComplete
    ) {

        if (index >= requestIds.size()) {

            onComplete.run();

            return;
        }

        requestRepository.updateStatus(
                requestIds.get(index),
                Constants.REQUEST_REJECTED,

                () -> rejectNextRequest(
                        requestIds,
                        index + 1,
                        onComplete
                ),

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to reject another request."
                )
        );
    }

    // ============================================================
    // STAFF / ADMIN - REJECT REQUEST
    // ============================================================

    public void rejectRequest(
            String requestId
    ) {

        if (requestId == null
                || requestId.trim().isEmpty()) {

            errorMessage.setValue(
                    "Request information is missing."
            );

            return;
        }

        requestRepository.updateStatus(
                requestId,
                Constants.REQUEST_REJECTED,

                () -> {

                    errorMessage.setValue(
                            "Borrowing request rejected."
                    );

                    loadRequests();
                },

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to reject request."
                )
        );
    }

    // ============================================================
    // AUTOMATICALLY REJECT UNAVAILABLE REQUEST
    // ============================================================

    private void rejectUnavailableRequest(
            String requestId,
            String message
    ) {

        requestRepository.updateStatus(
                requestId,
                Constants.REQUEST_REJECTED,

                () -> {

                    errorMessage.setValue(
                            message
                    );

                    loadRequests();
                },

                e -> errorMessage.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to reject request."
                )
        );
    }
}