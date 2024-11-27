package org.kemea.isafeco.client.ui.streamreceiver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.kemea.isafeco.client.R;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorClient;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Util;
import org.kemea.isafeco.client.utils.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamInputFragment extends Fragment {

    private EditText rtpStreamAddressInput;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_stream, container, false);
/*
        rtpStreamAddressInput = view.findViewById(R.id.rtp_stream_receive_address);
        View saveSourceButton = view.findViewById(R.id.save_source_button);
*/
        listView = view.findViewById(R.id.streamsList);
        getStreamSelectorStreams();
/*
        saveSourceButton.setOnClickListener(v -> {
            String rtspAddress = rtpStreamAddressInput.getText().toString().trim();

            if (rtspAddress.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a valid RTSP address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to LiveStreamFragment and pass the RTSP URL
            Bundle bundle = new Bundle();
            bundle.putString("RTSP_URL", rtspAddress);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_streamInputFragment_to_liveStreamFragment, bundle);
        });
*/
        return view;
    }

    private void getStreamSelectorStreams() {
        if (getContext() == null) {
            AppLogger.getLogger().e("No context found!!!");
            return;
        }
        ApplicationProperties applicationProperties = new ApplicationProperties(getContext().getFilesDir().getAbsolutePath());
        (new Validator()).validateStreamSelectorProperties(applicationProperties, requireContext());
        StreamSelectorClient streamSelectorClient = new StreamSelectorClient(
                applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS),
                applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_API_KEY));
        String userName = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
        String password = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginOutput loginOutput = null;
                try {
                    loginOutput = streamSelectorClient.logUser(userName, password, deviceId);
                    if (loginOutput == null)
                        throw new RuntimeException("Login to StreamSelector returned  null. Login failed.");
                    GetSessionsOutput getSessionsOutput = streamSelectorClient.getSessions(50, 0, null, null, loginOutput.getContractId(), null);
                    if (listView != null) {
                        List<String> sessionsDesc = Arrays.stream(getSessionsOutput.getSessions()).map(x -> String.format("%s %s", x.getSessionInfo().getId(), x.getSessionInfo().getCreatedAt())).collect(Collectors.toList());
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.sessions_spinner, sessionsDesc);
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        });
                    }
                } catch (Exception e) {
                    AppLogger.getLogger().e(Util.stacktrace(e));
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), String.format("Error: %s ", e.getMessage()), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();


    }
}
