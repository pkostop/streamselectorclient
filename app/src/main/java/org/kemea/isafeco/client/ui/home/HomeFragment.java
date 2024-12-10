package org.kemea.isafeco.client.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.kemea.isafeco.client.CameraRecordingService;
import org.kemea.isafeco.client.databinding.FragmentHomeBinding;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.UserLogin;
import org.kemea.isafeco.client.utils.Validator;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String PREVIEW_RTP_ADDRESS = "rtp://127.0.0.1:9095";
    private FragmentHomeBinding binding;
    MediaPlayer mediaPlayer = null;
    LibVLC libVLC = null;
    ApplicationProperties applicationProperties;
    Intent intent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.cameraSwitch.setOnCheckedChangeListener(new CameraButtonChangeListener());
        applicationProperties = new ApplicationProperties(getActivity().getFilesDir().getAbsolutePath());
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class CameraButtonChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean rec) {
            Context context = HomeFragment.this.getActivity().getApplicationContext();
            if (!(new Validator()).validateStreamSelectorProperties(applicationProperties, requireContext()))
                return;
            if (rec) {
                (new UserLogin()).logUser(requireContext(), requireActivity());
                String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
                String streamSelectorAddress = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
                if (streamingAddress == null && streamSelectorAddress == null) {
                    Toast.makeText(getActivity(), "ERROR, missing streaming destination address. Go to settings tab to set one.", Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                    return;
                }

                intent = new Intent(HomeFragment.this.getActivity(), CameraRecordingService.class);
                context.startForegroundService(intent);
                startPreview();
            } else {
                context.stopService(intent);
                stopPreview();
            }
        }

        private void stopPreview() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            if (libVLC != null) {
                libVLC.release();
            }
        }

        private void startPreview() {
            ArrayList<String> options = new ArrayList<>();
            options.add("--network-caching=300"); // Reduce latency
            libVLC = new LibVLC(getContext(), options);
            mediaPlayer = new MediaPlayer(libVLC);
            IVLCVout ivlcVout = mediaPlayer.getVLCVout();
            ivlcVout.setVideoView(binding.surfaceView);
            ivlcVout.attachViews();
            ivlcVout.setWindowSize(900, 1600);
            Media media = new Media(libVLC, Uri.parse(PREVIEW_RTP_ADDRESS));
            mediaPlayer.setMedia(media);
            mediaPlayer.setAspectRatio("9:16");
            media.release();
            mediaPlayer.play();
        }
    }
}