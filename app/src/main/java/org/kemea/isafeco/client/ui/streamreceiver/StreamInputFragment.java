package org.kemea.isafeco.client.ui.streamreceiver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.kemea.isafeco.client.R;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorClient;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.Session;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.UserLogin;
import org.kemea.isafeco.client.utils.Util;
import org.kemea.isafeco.client.utils.Validator;

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
        (new UserLogin()).logUser(requireContext(), requireActivity());
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginOutput loginOutput = null;
                try {

                    GetSessionsOutput getSessionsOutput = streamSelectorClient.getSessions(50, 0, null, null, null, null);
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (listView != null) {

                                ArrayAdapter<Session> arrayAdapter = new ArrayAdapter<Session>(requireContext(), R.layout.sessions_spinner, getSessionsOutput.getSessions()) {
                                    @NonNull
                                    @Override
                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                                        TextView textView = v.findViewById(R.id.itemText);
                                        textView.setText(sessionDescription(getSessionsOutput.getSessions()[position]));
                                        return textView;
                                    }
                                };
                                listView.setAdapter(arrayAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("RTSP_URL", "");
                                        NavHostFragment.findNavController(StreamInputFragment.this)
                                                .navigate(R.id.action_streamInputFragment_to_liveStreamFragment, bundle);
                                    }
                                });
                            }
                        }
                    });
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

    @NonNull
    private static String sessionDescription(Session x) {
        return String.format("(session id) %s, (created at) %s, (status) %s, (cluster id) %s, (contract id) %s", nvl(x.getSessionInfo().getId()), nvl(x.getSessionInfo().getCreatedAt()), nvl(x.getSessionInfo().getStatus()), nvl(x.getClusterInfo().getClusterId()), nvl(x.getClusterInfo().getContractId()));
    }

    public static String nvl(String val) {
        return !Util.isEmpty(val) ? val : "-";
    }

    public static String nvl(Object val) {
        return val != null ? String.valueOf(val) : "-";
    }


}