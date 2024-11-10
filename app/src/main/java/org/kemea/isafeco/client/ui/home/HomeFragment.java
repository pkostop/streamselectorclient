package org.kemea.isafeco.client.ui.home;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.kemea.isafeco.client.MainActivity;
import org.kemea.isafeco.client.databinding.FragmentHomeBinding;
import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.utils.ApplicationProperties;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    PreviewView viewFinder;
    MediaPlayer mediaPlayer;
    RTPStreamer rtpStreamer;
    String sdpFilePath = null;
    ApplicationProperties applicationProperties = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewFinder = binding.viewFinder;


        applicationProperties = ((MainActivity) getActivity()).getApplicationProperties();
        sdpFilePath = String.format("%s/stream.sdp", getActivity().getFilesDir());
        binding.cameraSwitch.setOnCheckedChangeListener(new CameraButtonChangeListener());

        return root;
    }

    private void startCamera(String videoFile) {
        //mediaPlayer.setDisplay();
        // mediaPlayer.setDataSource();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class CameraButtonChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean rec) {
            if (rec) {
                String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
                if (streamingAddress == null || "".equalsIgnoreCase(streamingAddress)) {
                    Toast.makeText(getActivity(), "ERROR, missing streaming destination address. Go to settings tab to set one.", Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                    return;
                }
                rtpStreamer = new RTPStreamer();
                rtpStreamer.startStreaming(streamingAddress, sdpFilePath, getActivity().getFilesDir() + "/output.mpg");
            } else {
                if (rtpStreamer != null)
                    rtpStreamer.stopStreaming();
            }
        }
    }
}