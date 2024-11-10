package org.kemea.isafeco.client.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> rtpStreamAddress;

    public SettingsViewModel() {
        rtpStreamAddress = new MutableLiveData<>();

    }

    public LiveData<String> getRtpStreamAddress() {
        return rtpStreamAddress;
    }
}