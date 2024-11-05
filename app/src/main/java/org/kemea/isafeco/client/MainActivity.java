package org.kemea.isafeco.client;

import static androidx.camera.core.CameraXThreads.TAG;

import android.Manifest;
import android.net.rtp.RtpStream;
import android.net.sip.SipManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.ReturnCode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.video.OutputFileOptions;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.kemea.isafeco.client.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private ActivityMainBinding binding;
    private FFmpegSession ffmpegSession;

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
/*
                    Recorder.Builder b=new Recorder.Builder();
                    b.setQualitySelector(QualitySelector.from(Quality.HIGHEST));
                    Recorder recorder=b.build();
                    VideoCapture videoCapture=VideoCapture.withOutput(recorder);
*/
                    //Camera camera=processCameraProvider.bindToLifecycle(MainActivity.this, CameraSelector.DEFAULT_BACK_CAMERA, preview);
                    startStreaming();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void startStreaming() {
        String serverAddress = "rtp://192.168.2.5:9094";

        String sdpFile=String.format("%s/stream.sdp", getFilesDir());
        String ffmpegCommand = String.format(
                "-loglevel debug -f android_camera -i 0:0 -c:v mpeg2video -b:v 512k -r:v 15 -flush_packets 1 -vf scale=320:240  -f rtp \"%s\" -sdp_file %s",
                serverAddress, sdpFile
        );

        ffmpegSession = FFmpegKit.executeAsync(ffmpegCommand, getfFmpegSessionCompleteCallback(sdpFile), getLogCallback(), null
    );

    }

    @NonNull
    private static LogCallback getLogCallback() {
        return new LogCallback() {
            @Override
            public void apply(final com.arthenica.ffmpegkit.Log log) {
                Log.e(MainActivity.class.getName(), log.getMessage());
            }
        };
    }

    @NonNull
    private FFmpegSessionCompleteCallback getfFmpegSessionCompleteCallback(String sdpFile) {
        return sessionCompleted -> {
            if (ReturnCode.isSuccess(sessionCompleted.getReturnCode())) {
                Log.e(MainActivity.class.getName(), "FFMPEGKit Success!!!");
            } else {
                Log.e(MainActivity.class.getName(), "FFMPEGKit Error!!!");
            }
            try {
                Log.e(MainActivity.class.getName(), "-----------------------------------");
                Log.e(MainActivity.class.getName(), new String(readFile(sdpFile)));
                Log.e(MainActivity.class.getName(), "-----------------------------------");
            } catch (Exception e) {
                Log.e(MainActivity.class.getName(),  e.getMessage() );
            }
            Log.e(MainActivity.class.getName(), sessionCompleted.getLogsAsString());
        };
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

    private byte[] readFile(String path) throws Exception{
        FileInputStream fis=new FileInputStream(path);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buffer=new byte[512];
        while(fis.read(buffer,0,512)>=0){
            baos.write(buffer);
        }
        fis.close();
        return baos.toByteArray();
    }
}