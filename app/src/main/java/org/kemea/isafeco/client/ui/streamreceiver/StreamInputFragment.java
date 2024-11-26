package org.kemea.isafeco.client.ui.streamreceiver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.inappmessaging.model.Button;

import org.kemea.isafeco.client.R;

public class StreamInputFragment extends Fragment {

    private EditText rtpStreamAddressInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_stream, container, false);

        rtpStreamAddressInput = view.findViewById(R.id.rtp_stream_receive_address);
        View saveSourceButton = view.findViewById(R.id.save_source_button);

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

        return view;
    }
}
