package org.kemea.isafeco.client.ui.home;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
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
import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Validator;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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

    private File writeSDPToTempFile(String sdpContent, Context context) {
        try {
            // Get the cache directory (temporary storage directory for the app)
            File tempFile = File.createTempFile("stream", ".sdp", context.getCacheDir());

            // Write the SDP content to the file
            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                writer.write(sdpContent);
                writer.flush();
            }

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    class CameraButtonChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean rec) {
            try {
                if (!(new Validator()).validateStreamSelectorProperties(applicationProperties, requireContext()))
                    return;
                if (rec) {
                    //startPreview();
                    intent = new Intent(HomeFragment.this.getActivity(), CameraRecordingService.class);
                    requireContext().startForegroundService(intent);
                } else {
                    requireContext().stopService(intent);
                    //stopPreview();
                }
            } catch (Exception e) {
                AppLogger.getLogger().e(e);
                Toast.makeText(requireContext(), String.format("Error: %s", e.getMessage()), Toast.LENGTH_LONG).show();
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
    }

    public void startPreview() throws InterruptedException {
        ArrayList<String> options = new ArrayList<>();
        options.add(":network-caching=300");
        options.add("--verbose=2");
        options.add("--no-audio");
        libVLC = new LibVLC(requireContext(), options);
        mediaPlayer = new MediaPlayer(libVLC);
        IVLCVout ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(binding.surfaceView);
        ivlcVout.attachViews();
        ivlcVout.setWindowSize(binding.surfaceView.getWidth(), binding.surfaceView.getHeight());
        Media media = new Media(libVLC, Uri.parse(RTPStreamer.PREVIEW_MPEGTS_ADDRESS));
        mediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_BEST_FIT);
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event == null)
                    return;

                if (event.type == MediaPlayer.Event.EncounteredError) {
                    AppLogger.getLogger().e("Libvlc Error occurred in media player");
                }

            }
        });
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