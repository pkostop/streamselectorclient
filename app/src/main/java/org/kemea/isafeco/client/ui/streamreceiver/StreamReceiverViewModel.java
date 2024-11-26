package org.kemea.isafeco.client.ui.streamreceiver;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StreamReceiverViewModel extends ViewModel {
    public StreamReceiverViewModel(MutableLiveData<String> rtpStreamReceiveAddress) {
        this.rtpStreamReceiveAddress = rtpStreamReceiveAddress;
    }

    private final MutableLiveData<String> rtpStreamReceiveAddress;

    public MutableLiveData<String> getRtpStreamReceiveAddress() {
        return rtpStreamReceiveAddress;
    }



}
