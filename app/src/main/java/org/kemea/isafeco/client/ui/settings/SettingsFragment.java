package org.kemea.isafeco.client.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.kemea.isafeco.client.MainActivity;
import org.kemea.isafeco.client.R;
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
        applicationProperties = ((MainActivity) requireActivity()).getApplicationProperties();
        final EditText editTextRtpStreamingAddress = binding.rtpStreamDestinationAddress;
        final EditText editTextStreamSelectorAddress = binding.streamSelectorAddress;
        final EditText editTextStreamSelectorUsername = binding.streamSelectorUsername;
        final EditText editTextStreamSelectorPassword = binding.streamSelectorPassword;
        final EditText editMetricsUrl = binding.metricsMonitoringUrl;
        final Spinner orgSpinner = binding.userorg;
        orgSpinner.setAdapter(ArrayAdapter.createFromResource(this.getContext(), R.array.organizations, android.R.layout.simple_spinner_item));
        ((ArrayAdapter) orgSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (applicationProperties.getProperty(ApplicationProperties.PROP_USER_ORG) != null) {
            orgSpinner.setSelection(Integer.parseInt(applicationProperties.getProperty(ApplicationProperties.PROP_USER_ORG)));
        }
        if (applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS) != null)
            editTextRtpStreamingAddress.setText(applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS));
        if (applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS) != null)
            editTextStreamSelectorAddress.setText(applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS));
        if (applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME) != null)
            editTextStreamSelectorUsername.setText(applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME));
        if (applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD) != null)
            editTextStreamSelectorPassword.setText(applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD));
        if (applicationProperties.getProperty(ApplicationProperties.PROP_METRICS_URL) != null)
            editMetricsUrl.setText(applicationProperties.getProperty(ApplicationProperties.PROP_METRICS_URL));

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applicationProperties.setProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS, editTextRtpStreamingAddress.getText().toString());
                applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS, editTextStreamSelectorAddress.getText().toString());
                applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME, editTextStreamSelectorUsername.getText().toString());
                applicationProperties.setProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD, editTextStreamSelectorPassword.getText().toString());
                applicationProperties.setProperty(ApplicationProperties.PROP_METRICS_URL, editMetricsUrl.getText().toString());
                if (orgSpinner.getSelectedItemId() >= 0)
                    applicationProperties.setProperty(ApplicationProperties.PROP_USER_ORG, String.valueOf(orgSpinner.getSelectedItemId()));

                applicationProperties.save();
                Toast.makeText(getActivity(), "Saved!!!", Toast.LENGTH_LONG).show();
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