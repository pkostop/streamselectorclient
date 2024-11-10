package org.kemea.isafeco.client.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.kemea.isafeco.client.MainActivity;
import org.kemea.isafeco.client.databinding.FragmentSettingsBinding;
import org.kemea.isafeco.client.utils.ApplicationProperties;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    ApplicationProperties applicationProperties;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        applicationProperties = ((MainActivity) getActivity()).getApplicationProperties();
        final EditText editTextRtpStreamingAddress = binding.rtpStreamDestinationAddress;
        if (applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS) != null)
            editTextRtpStreamingAddress.setText(applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS));
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applicationProperties.setProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS, editTextRtpStreamingAddress.getText().toString());
                applicationProperties.save();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}