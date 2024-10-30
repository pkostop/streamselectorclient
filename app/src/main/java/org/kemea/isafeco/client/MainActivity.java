package org.kemea.isafeco.client;

import android.Manifest;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.kemea.isafeco.client.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        requestPermissions();

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> p=ProcessCameraProvider.getInstance(this);
        p.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider processCameraProvider=p.get();
                    Preview preview=(new Preview.Builder()).build();
                    preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
                    processCameraProvider.unbindAll();

                    Recorder.Builder b=new Recorder.Builder();
                    b.setQualitySelector(QualitySelector.from(Quality.HIGHEST));
                    Recorder recorder=b.build();
                    VideoCapture videoCapture=VideoCapture.withOutput(recorder);

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(1280, 720)) // Example resolution
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this), MainActivity.this::analyzeImage);

                    processCameraProvider.bindToLifecycle(MainActivity.this, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy image) {
        image.close();
    }

    private void requestPermissions(){
        ActivityResultLauncher activityResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> permissions) {
                for (String key:permissions.keySet()) {
                    if(Arrays.asList(REQUIRED_PERMISSIONS).contains(key)&&!permissions.get(key)){
                        Toast.makeText(MainActivity.this,
                                String.format("Permission %s request denied",key),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                startCamera();
            }
        });

        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }
}