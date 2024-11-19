package org.kemea.isafeco.client.ui.streamreceiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.kemea.isafeco.client.MainActivity;
import org.kemea.isafeco.client.VideoPlayerActivity;
import org.kemea.isafeco.client.databinding.FragmentSelectStreamBinding;
import org.kemea.isafeco.client.ui.settings.SettingsViewModel;
import org.kemea.isafeco.client.utils.ApplicationProperties;

public class StreamReceiverFragment extends Fragment {
    private FragmentSelectStreamBinding binding;
    ApplicationProperties applicationProperties;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel dashboardViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSelectStreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        applicationProperties = ((MainActivity) getActivity()).getApplicationProperties();
        final EditText remoteRtpAddress  = binding.rtpStreamReceiveAddress;
        if (applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS) != null)
            remoteRtpAddress.setText(applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS));

        binding.saveSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applicationProperties.setProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS, remoteRtpAddress.getText().toString());
                applicationProperties.save();

                String rtspUrl = remoteRtpAddress.getText().toString();
                //popup a text to validate input
                //Toast.makeText(getActivity(),"Got: " + remoteRtpAddress.getText(), Toast.LENGTH_LONG).show();

                // Check if the RTSP URL is valid
                if (rtspUrl.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a valid RTSP URL.", Toast.LENGTH_LONG).show();
                    return;
                }
                // Start a new activity to play the RTSP stream
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("RTSP_URL", rtspUrl); // Pass the RTSP URL to the player activity
                startActivity(intent);
            }
        });

        return root;
    }
}
