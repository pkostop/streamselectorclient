package org.kemea.isafeco.client.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import org.kemea.isafeco.client.CameraRecordingService;
import org.kemea.isafeco.client.databinding.FragmentHomeBinding;
import org.kemea.isafeco.client.utils.ApplicationProperties;

import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    PreviewView viewFinder;
    ApplicationProperties applicationProperties;
    Intent intent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewFinder = binding.viewFinder;
        binding.cameraSwitch.setOnCheckedChangeListener(new CameraButtonChangeListener());
        applicationProperties = new ApplicationProperties(getActivity().getFilesDir().getAbsolutePath());
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> p = ProcessCameraProvider.getInstance(getActivity());
        p.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider processCameraProvider = p.get();
                    Preview preview = (new Preview.Builder()).build();
                    preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
                    processCameraProvider.unbindAll();
                    Camera camera = processCameraProvider.bindToLifecycle(getActivity(), CameraSelector.DEFAULT_BACK_CAMERA, preview);

                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }


    class CameraButtonChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean rec) {
            Context context = HomeFragment.this.getActivity().getApplicationContext();
            if (rec) {
                String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
                String streamSelectorAddress = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
                if (streamingAddress == null && streamSelectorAddress == null) {
                    Toast.makeText(getActivity(), "ERROR, missing streaming destination address. Go to settings tab to set one.", Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                    return;
                }

                intent = new Intent(HomeFragment.this.getActivity(), CameraRecordingService.class);
                context.startForegroundService(intent);
            } else {
                context.stopService(intent);
            }
        }
    }
}