package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.repositories.LabRepository;
import com.example.labmate.states.LabDetailsState;

public class LabDetailsViewModel extends ViewModel {

    private final LabRepository labRepository;

    private final MutableLiveData<LabDetailsState> state =
            new MutableLiveData<>(
                    LabDetailsState.idle()
            );

    public LabDetailsViewModel() {

        labRepository =
                new LabRepository();
    }

    public LiveData<LabDetailsState> getState() {
        return state;
    }

    public void loadEquipmentSummary(
            String labName,
            boolean showLoading
    ) {

        if (labName == null
                || labName.isEmpty()) {

            state.setValue(
                    LabDetailsState.error(
                            "Laboratory name missing."
                    )
            );

            return;
        }

        if (showLoading) {

            state.setValue(
                    LabDetailsState.loading()
            );
        }

        labRepository.getEquipmentSummary(
                labName,

                (summaries, totalEquipment) ->
                        state.setValue(
                                LabDetailsState.success(
                                        summaries,
                                        totalEquipment
                                )
                        ),

                e -> state.setValue(
                        LabDetailsState.error(
                                e.getMessage()
                        )
                )
        );
    }
}