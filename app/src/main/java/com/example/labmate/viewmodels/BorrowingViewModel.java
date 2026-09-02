package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.Borrowing;
import com.example.labmate.models.BorrowingDisplayItem;
import com.example.labmate.repositories.BorrowingRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.repositories.UserRepository;
import com.example.labmate.models.Equipment;
import com.example.labmate.models.User;
import com.example.labmate.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BorrowingViewModel extends ViewModel {

    private final BorrowingRepository borrowingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<String> message = new MutableLiveData<>();

    public BorrowingViewModel() {

        borrowingRepository = new BorrowingRepository();
        equipmentRepository = new EquipmentRepository();
        userRepository = new UserRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // ============================================================
    // LOAD ACTIVE BORROWINGS
    // ============================================================

    public void loadActiveBorrowings(
            String userId,
            String role,
            ActiveBorrowingsListener listener
    ) {

        if (userId == null || userId.trim().isEmpty()) {

            message.setValue(
                    "User information is missing."
            );

            return;
        }

        if (role == null || role.trim().isEmpty()) {

            message.setValue(
                    "User role information is missing."
            );

            return;
        }

        EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess =
                snapshot -> {

                    ArrayList<Borrowing> borrowings =
                            convertBorrowings(snapshot);

                    // Load equipment and user information
                    // before sending the list to the adapter.
                    loadBorrowingDetails(
                            borrowings,
                            0,
                            () -> listener.onLoaded(borrowings)
                    );
                };

        EquipmentRepository.OnFailure onFailure =
                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load borrowings."
                );

        if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)
                || Constants.ROLE_STAFF.equalsIgnoreCase(role)) {

            borrowingRepository.getActiveBorrowings(
                    onSuccess,
                    onFailure
            );

        } else if (Constants.ROLE_STUDENT.equalsIgnoreCase(role)) {

            borrowingRepository.getActiveBorrowingsByUserId(
                    userId,
                    onSuccess,
                    onFailure
            );

        } else {

            message.setValue(
                    "Invalid user role."
            );
        }
    }

    // ============================================================
    // LOAD USER BORROWING HISTORY
    // ============================================================

    public void loadUserBorrowings(
            String userId,
            UserBorrowingsListener listener
    ) {

        if (userId == null || userId.trim().isEmpty()) {

            message.setValue(
                    "User information is missing."
            );

            return;
        }

        borrowingRepository.getBorrowingsByUserId(
                userId,

                snapshot -> {

                    ArrayList<Borrowing> borrowings =
                            convertBorrowings(snapshot);

                    listener.onLoaded(borrowings);
                },

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load borrowing history."
                )
        );
    }

    // ============================================================
    // LOAD BORROWING HISTORY
    // ============================================================

    public void loadBorrowingHistory(
            String userId,
            String role,
            BorrowingHistoryListener listener
    ) {

        if (userId == null || userId.trim().isEmpty()) {

            message.setValue(
                    "User information is missing."
            );

            return;
        }

        if (role == null || role.trim().isEmpty()) {

            message.setValue(
                    "User role information is missing."
            );

            return;
        }

        EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess =
                snapshot -> {

                    ArrayList<Borrowing> borrowings =
                            convertBorrowings(snapshot);

                    loadBorrowingDetails(
                            borrowings,
                            0,
                            () -> listener.onLoaded(borrowings)
                    );
                };

        EquipmentRepository.OnFailure onFailure =
                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load borrowing history."
                );

        if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)
                || Constants.ROLE_STAFF.equalsIgnoreCase(role)) {

            borrowingRepository.getAllBorrowings(
                    onSuccess,
                    onFailure
            );

        } else if (Constants.ROLE_STUDENT.equalsIgnoreCase(role)) {

            borrowingRepository.getBorrowingsByUserId(
                    userId,
                    onSuccess,
                    onFailure
            );

        } else {

            message.setValue(
                    "Invalid user role."
            );
        }
    }

    private void loadBorrowingDetails(
            ArrayList<Borrowing> borrowings,
            int index,
            Runnable onComplete
    ) {

        if (index >= borrowings.size()) {

            onComplete.run();
            return;
        }

        Borrowing borrowing = borrowings.get(index);

        loadEquipmentDetails(
                borrowing,

                () -> loadUserDetails(
                        borrowing,

                        () -> loadBorrowingDetails(
                                borrowings,
                                index + 1,
                                onComplete
                        )
                )
        );
    }

    // ============================================================
    // CHECKOUT
    // ============================================================

    public void checkout(
            String borrowingId,
            String equipmentId
    ) {

        if (borrowingId == null || borrowingId.trim().isEmpty()) {

            message.setValue(
                    "Borrowing information is missing."
            );

            return;
        }

        if (equipmentId == null || equipmentId.trim().isEmpty()) {

            message.setValue(
                    "Equipment information is missing."
            );

            return;
        }

        borrowingRepository.getById(
                borrowingId,

                borrowing -> {

                    if (borrowing == null || !borrowing.exists()) {

                        message.setValue(
                                "Borrowing record not found."
                        );

                        return;
                    }

                    String status = borrowing.getString("status");

                    if (!Constants.BORROWING_ACTIVE.equals(status)) {

                        message.setValue(
                                "This equipment is not waiting for checkout."
                        );

                        return;
                    }

                    if (borrowing.getTimestamp("borrowedAt") != null) {

                        message.setValue(
                                "This equipment has already been checked out."
                        );

                        return;
                    }

                    performCheckout(
                            borrowingId,
                            equipmentId
                    );
                },

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to load borrowing."
                )
        );
    }

    private void performCheckout(
            String borrowingId,
            String equipmentId
    ) {

        borrowingRepository.checkout(
                borrowingId,

                () -> updateEquipment(
                        equipmentId
                ),

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to checkout equipment."
                )
        );
    }

    private void updateEquipment(
            String equipmentId
    ) {

        equipmentRepository.updateState(
                equipmentId,
                Constants.STATE_BORROWED,

                () -> message.setValue(
                        "Equipment checked out successfully."
                ),

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Borrowing was updated, but equipment state could not be updated."
                )
        );
    }

    // ============================================================
    // CONVERT FIRESTORE DATA
    // ============================================================

    private ArrayList<Borrowing> convertBorrowings(
            QuerySnapshot snapshot
    ) {

        ArrayList<Borrowing> borrowingList = new ArrayList<>();

        for (DocumentSnapshot document : snapshot.getDocuments()) {

            Borrowing borrowing =
                    document.toObject(Borrowing.class);

            if (borrowing == null) {
                continue;
            }

            borrowing.setId(document.getId());

            borrowingList.add(borrowing);
        }

        return borrowingList;
    }

    private void loadEquipmentDetails(
            Borrowing borrowing,
            Runnable onComplete
    ) {

        if (borrowing.getEquipmentId() == null
                || borrowing.getEquipmentId().trim().isEmpty()) {

            onComplete.run();
            return;
        }

        equipmentRepository.getById(
                borrowing.getEquipmentId(),

                equipment -> {

                    if (equipment != null && equipment.exists()) {

                        borrowing.setEquipmentName(
                                equipment.getString("equipmentName")
                        );

                        borrowing.setEquipmentModel(
                                equipment.getString("equipmentModel")
                        );

                        borrowing.setLab(
                                equipment.getString("lab")
                        );

                        borrowing.setEquipmentQrId(
                                equipment.getString("qrId")
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
            Borrowing borrowing,
            Runnable onComplete
    ) {

        if (borrowing.getUserId() == null
                || borrowing.getUserId().trim().isEmpty()) {

            onComplete.run();
            return;
        }

        userRepository.getById(
                borrowing.getUserId(),

                user -> {

                    if (user != null && user.exists()) {

                        borrowing.setUserName(
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

    public void loadBorrowingDisplayItems(
            String userId,
            String role,
            BorrowingDisplayListener listener
    ) {

        if (userId == null || userId.trim().isEmpty()) {

            message.setValue(
                    "User information is missing."
            );

            return;
        }

        if (role == null || role.trim().isEmpty()) {

            message.setValue(
                    "User role information is missing."
            );

            return;
        }

        BorrowingHistoryListener historyListener =
                borrowings -> {

                    if (borrowings.isEmpty()) {

                        listener.onLoaded(
                                new ArrayList<>()
                        );

                        return;
                    }

                    ArrayList<BorrowingDisplayItem> displayItems =
                            new ArrayList<>();

                    loadDisplayItem(
                            borrowings,
                            0,
                            displayItems,
                            listener,
                            role
                    );
                };

        loadBorrowingHistory(
                userId,
                role,
                historyListener
        );
    }

    private void loadDisplayItem(
            ArrayList<Borrowing> borrowings,
            int index,
            ArrayList<BorrowingDisplayItem> displayItems,
            BorrowingDisplayListener listener,
            String role
    ) {

        if (index >= borrowings.size()) {

            listener.onLoaded(displayItems);

            return;
        }

        Borrowing borrowing = borrowings.get(index);

        equipmentRepository.getById(
                borrowing.getEquipmentId(),

                equipmentDocument -> {

                    if (equipmentDocument == null
                            || !equipmentDocument.exists()) {

                        loadDisplayItem(
                                borrowings,
                                index + 1,
                                displayItems,
                                listener,
                                role
                        );

                        return;
                    }

                    Equipment equipment =
                            equipmentDocument.toObject(Equipment.class);

                    if (equipment == null) {

                        loadDisplayItem(
                                borrowings,
                                index + 1,
                                displayItems,
                                listener,
                                role
                        );

                        return;
                    }

                    BorrowingDisplayItem item =
                            new BorrowingDisplayItem();

                    item.setBorrowingId(borrowing.getId());

                    item.setEquipmentName(
                            equipment.getEquipmentName()
                    );

                    item.setEquipmentModel(
                            equipment.getEquipmentModel()
                    );

                    item.setEquipmentQrId(
                            equipment.getQrId()
                    );

                    item.setCreatedAt(
                            borrowing.getCreatedAt()
                    );

                    item.setBorrowedAt(
                            borrowing.getBorrowedAt()
                    );

                    item.setReturnedAt(
                            borrowing.getReturnedAt()
                    );

                    item.setStatus(
                            borrowing.getStatus()
                    );

                    // Admin and Staff need to see who borrowed it.
                    if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)
                            || Constants.ROLE_STAFF.equalsIgnoreCase(role)) {

                        userRepository.getById(
                                borrowing.getUserId(),

                                userDocument -> {

                                    if (userDocument != null
                                            && userDocument.exists()) {

                                        User user =
                                                userDocument.toObject(User.class);

                                        if (user != null) {

                                            item.setUserName(
                                                    user.getName()
                                            );
                                        }
                                    }

                                    displayItems.add(item);

                                    loadDisplayItem(
                                            borrowings,
                                            index + 1,
                                            displayItems,
                                            listener,
                                            role
                                    );
                                },

                                e -> {

                                    item.setUserName(
                                            "Unknown User"
                                    );

                                    displayItems.add(item);

                                    loadDisplayItem(
                                            borrowings,
                                            index + 1,
                                            displayItems,
                                            listener,
                                            role
                                    );
                                }
                        );

                    } else {

                        displayItems.add(item);

                        loadDisplayItem(
                                borrowings,
                                index + 1,
                                displayItems,
                                listener,
                                role
                        );
                    }
                },

                e -> {

                    loadDisplayItem(
                            borrowings,
                            index + 1,
                            displayItems,
                            listener,
                            role
                    );
                }
        );
    }

    // ============================================================
    // LISTENERS
    // ============================================================

    public interface ActiveBorrowingsListener {

        void onLoaded(
                ArrayList<Borrowing> borrowings
        );
    }

    public interface UserBorrowingsListener {

        void onLoaded(
                ArrayList<Borrowing> borrowings
        );
    }

    public interface BorrowingHistoryListener {

        void onLoaded(
                ArrayList<Borrowing> borrowings
        );
    }

    public interface BorrowingDisplayListener {

        void onLoaded(
                ArrayList<BorrowingDisplayItem> items
        );
    }
}