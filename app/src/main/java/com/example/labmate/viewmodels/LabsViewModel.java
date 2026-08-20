package com.example.labmate.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labmate.repositories.LabRepository;
import com.example.labmate.states.LabsState;
import com.example.labmate.utils.UserSession;

import java.util.Comparator;
import java.util.List;

public class LabsViewModel extends AndroidViewModel {

    private final LabRepository labRepository;
    private final UserSession userSession;

    private final MutableLiveData<LabsState> labsState =
            new MutableLiveData<>(LabsState.idle());

    public LabsViewModel(
            @NonNull Application application
    ) {
        super(application);

        labRepository = new LabRepository();

        userSession =
                new UserSession(application);
    }

    public LiveData<LabsState> getLabsState() {
        return labsState;
    }

    public void loadLabs() {

        labsState.setValue(
                LabsState.loading()
        );

        boolean isAdmin =
                userSession.isAdmin();

        labRepository.getLabs(

                labs -> {

                    labs.sort(
                            Comparator.comparing(
                                    lab -> lab.getName() != null
                                            ? lab.getName()
                                            : "",
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    );

                    labsState.setValue(
                            LabsState.success(
                                    labs,
                                    isAdmin
                            )
                    );
                },

                e -> labsState.setValue(
                        LabsState.error(
                                "Failed to load labs."
                        )
                )
        );
    }
}