package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.repositories.LabRepository;
import com.example.labmate.states.AddLabState;

public class AddLabViewModel extends ViewModel {

    private final LabRepository labRepository;

    private final MutableLiveData<AddLabState> state =
            new MutableLiveData<>(
                    AddLabState.idle()
            );

    public AddLabViewModel() {

        labRepository =
                new LabRepository();
    }

    public LiveData<AddLabState> getState() {
        return state;
    }

    public void addLab(
            String name,
            String inCharge,
            String location,
            String createdBy,
            String createdByRole
    ) {

        state.setValue(
                AddLabState.loading()
        );

        labRepository.addLab(
                name,
                inCharge,
                location,
                createdBy,
                createdByRole,

                () -> state.setValue(
                        AddLabState.success(
                                "Laboratory Added Successfully"
                        )
                ),

                e -> state.setValue(
                        AddLabState.error(
                                e.getMessage()
                        )
                )
        );
    }
}