package org.kemea.isafeco.client.ui.home;

import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
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
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Validator;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.util.Arrays;

public class HomeFragment extends Fragment {


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
        applicationProperties = new ApplicationProperties(requireActivity().getFilesDir() != null ? requireActivity().getFilesDir().getAbsolutePath() : "");
        printCameraCapabilities();
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
            try {
                if (!(new Validator()).validateStreamSelectorProperties(applicationProperties, requireContext()))
                    return;
                if (rec) {
                    intent = new Intent(HomeFragment.this.getActivity(), CameraRecordingService.class);
                    requireContext().startForegroundService(intent);
                } else {
                    requireContext().stopService(intent);
                }
            } catch (Exception e) {
                AppLogger.getLogger().e(e);
                Toast.makeText(requireContext(), String.format("Error: %s", e.getMessage()), Toast.LENGTH_LONG).show();
            }
        }
    }


    public void printCameraCapabilities() {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        AppLogger.getLogger().i("------------------------------");
        for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
            for (String supportedType : mediaCodecInfo.getSupportedTypes()) {
                AppLogger.getLogger().i(supportedType);
                MediaCodecInfo.CodecCapabilities codecCapabilities = mediaCodecInfo.getCapabilitiesForType(supportedType);
                if (codecCapabilities != null && codecCapabilities.getVideoCapabilities() != null) {
                    Arrays.asList(codecCapabilities.getVideoCapabilities().getSupportedHeights()).forEach(x -> AppLogger.getLogger().i(x.toString()));
                    Arrays.asList(codecCapabilities.getVideoCapabilities().getSupportedWidths()).forEach(x -> AppLogger.getLogger().i(x.toString()));
                    Arrays.asList(codecCapabilities.getVideoCapabilities().getBitrateRange()).forEach(x -> AppLogger.getLogger().i(x.toString()));
                    Arrays.asList(codecCapabilities.getVideoCapabilities().getSupportedFrameRates()).forEach(x -> AppLogger.getLogger().i(x.toString()));
                }
            }
        }
        AppLogger.getLogger().i("-----------------------------");
    }
}