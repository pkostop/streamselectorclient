package org.kemea.isafeco.client.ui.streamreceiver;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.kemea.isafeco.client.R;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorService;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.Session;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.Util;

public class StreamInputFragment extends Fragment {

    private EditText rtpStreamAddressInput;
    ListView listView;
    StreamSelectorService streamSelectorService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_stream, container, false);
        streamSelectorService = new StreamSelectorService(requireActivity());
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
        new Thread(new Runnable() {
            GetSessionsOutput getSessionsOutput = null;

            @Override
            public void run() {
                try {
                    getSessionsOutput = streamSelectorService.getSessions(100, 0, null, null, null, 2L);
                } catch (Exception e) {
                    AppLogger.getLogger().e(Util.stacktrace(e));
                    Util.toast(StreamInputFragment.this.getActivity(), String.format("Error: %s", e.getMessage()));
                }
                if (getSessionsOutput != null) {
                    requireActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (listView != null) {
                                ArrayAdapter<Session> arrayAdapter = createSessionsListViewAdapter(getSessionsOutput);
                                listView.setAdapter(arrayAdapter);
                                listView.setOnItemClickListener(new SessionClickListener(requireActivity()));
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @NonNull
    private ArrayAdapter<Session> createSessionsListViewAdapter(GetSessionsOutput getSessionsOutput) {
        return new ArrayAdapter<Session>(requireContext(), R.layout.sessions_spinner, getSessionsOutput.getSessions()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                TextView textView = v.findViewById(R.id.itemText);
                textView.setText(sessionDescription(getSessionsOutput.getSessions()[position]));
                return textView;
            }
        };
    }

    @NonNull
    private static String sessionDescription(Session x) {
        return String.format("(session id) %s, (created at) %s, (status) %s, (cluster id) %s, (contract id) %s", nvl(x.getId()), nvl(x.getCreatedAt()), nvl(x.getStatus()), nvl(x.getClusterId()), nvl(x.getContractId()));
    }

    public static String nvl(String val) {
        return !Util.isEmpty(val) ? val : "-";
    }

    public static String nvl(Object val) {
        return val != null ? String.valueOf(val) : "-";
    }

    class SessionClickListener implements AdapterView.OnItemClickListener {
        Activity activity;

        public SessionClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Session session = (Session) adapterView.getAdapter().getItem(i);
            Bundle bundle = new Bundle();
            bundle.putSerializable("SESSION", session);
            NavHostFragment.findNavController(StreamInputFragment.this)
                    .navigate(R.id.action_streamInputFragment_to_liveStreamFragment, bundle);
        }
    }
}