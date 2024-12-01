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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.kemea.isafeco.client.databinding.ActivityMainBinding;
import org.kemea.isafeco.client.net.StreamSelectorRestMock;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    public ApplicationProperties applicationProperties;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings, R.id.navigation_select_stream, R.id.navigation_logs)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        requestPermissions();
        startStreamSelectorMock();
        initApplicationProperties();
    }

    private void startStreamSelectorMock() {
        StreamSelectorRestMock streamSelectorRestMock = new StreamSelectorRestMock(9094);
        try {
            streamSelectorRestMock.start();
        } catch (IOException e) {
            AppLogger.getLogger().e(e);
        }
    }


    private void requestPermissions() {
        ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> permissions) {
                for (String key : permissions.keySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(key) && permissions.get(key) != null) {
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

    private void initApplicationProperties() {
        applicationProperties = new ApplicationProperties(this.getFilesDir().getAbsolutePath());
        applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS, "http://127.0.0.1:9094");
        applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME, "test");
        applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD, "test");
        applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_API_KEY, "test");
        applicationProperties.save();
    }
}