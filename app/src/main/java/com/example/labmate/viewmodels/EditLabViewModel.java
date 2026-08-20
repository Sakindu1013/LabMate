package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.repositories.LabRepository;
import com.example.labmate.states.EditLabState;

public class EditLabViewModel extends ViewModel {

    private final LabRepository labRepository;

    private final MutableLiveData<EditLabState> state =
            new MutableLiveData<>(
                    EditLabState.idle()
            );

    public EditLabViewModel() {

        labRepository =
                new LabRepository();
    }

    public LiveData<EditLabState> getState() {
        return state;
    }

    public void updateLab(
            String labId,
            String name,
            String inCharge,
            String location
    ) {

        state.setValue(
                EditLabState.loading()
        );

        labRepository.updateLab(
                labId,
                name,
                inCharge,
                location,

                () -> state.setValue(
                        EditLabState.updateSuccess(
                                "Lab Updated Successfully"
                        )
                ),

                e -> state.setValue(
                        EditLabState.error(
                                e.getMessage()
                        )
                )
        );
    }

    public void deleteLab(
            String labId
    ) {

        state.setValue(
                EditLabState.loading()
        );

        labRepository.deleteLab(
                labId,

                () -> state.setValue(
                        EditLabState.deleteSuccess(
                                "Lab Deleted Successfully"
                        )
                ),

                e -> state.setValue(
                        EditLabState.error(
                                e.getMessage()
                        )
                )
        );
    }
}