package org.kemea.isafeco.client;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.arthenica.ffmpegkit.FFmpegSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.kemea.isafeco.client.databinding.ActivityMainBinding;
import org.kemea.isafeco.client.utils.ApplicationProperties;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };
    private ActivityMainBinding binding;
    public ApplicationProperties applicationProperties;

    private FFmpegSession ffmpegSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationProperties = new ApplicationProperties(this.getFilesDir().getAbsolutePath());
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings, R.id.navigation_logs)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        requestPermissions();
    }


    private void requestPermissions() {
        ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> permissions) {
                for (String key : permissions.keySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(key) && !permissions.get(key)) {
                        Toast.makeText(MainActivity.this,
                                String.format("Permission %s request denied", key),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}