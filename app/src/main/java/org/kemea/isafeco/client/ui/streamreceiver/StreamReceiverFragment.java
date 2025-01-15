package org.kemea.isafeco.client.ui.streamreceiver;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;

import org.kemea.isafeco.client.R;
import org.kemea.isafeco.client.net.NetUtil;
import org.kemea.isafeco.client.net.RTCPClient;
import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorService;
import org.kemea.isafeco.client.streamselector.stubs.output.Session;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionDestinationStreamOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.Util;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StreamReceiverFragment extends Fragment {
    public static final String KEEP_ALIVE_TIMER = "keepAliveTimer";
    private SurfaceView sessionSurfaceView;
    private LibVLC libVLC;
    MediaPlayer mediaPlayer = null;
    private NavController navController;
    private StreamSelectorService streamSelectorService;
    private static final String TAG = "LiveStreamFragment";
    private FFmpegSession convert2MpegtsFFmpegSession = null;
    Timer timer;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, ">>> Player onCreateView()");
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        Button button = view.findViewById(R.id.stop_button);
        button.setOnClickListener(v -> {
            stopPlayback();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_liveStreamFragment_to_streamInputFragment, new Bundle());
        });
        //playerView = view.findViewById(R.id.player_view);
        sessionSurfaceView = view.findViewById(R.id.sessionSurfaceView);
        return view;
    }

    private void stopPlayback() {
        StreamReceiverFragment.this.requireActivity().runOnUiThread(() -> stopVlcPlayer());
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (convert2MpegtsFFmpegSession != null)
            FFmpegKit.cancel(convert2MpegtsFFmpegSession.getSessionId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            navController = Navigation.findNavController(view);
            Session session = (Session) getArguments().get("SESSION");
            if (session == null)
                Toast.makeText(requireContext(), "Error! StreamSelector Session not found!", Toast.LENGTH_LONG).show();
            startStreamingSession(session);
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
            Toast.makeText(requireContext(), String.format("Error: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    private void startStreamingSession(Session session) {
        streamSelectorService = new StreamSelectorService(requireContext());
        new Thread(() -> establishStreaming(session)).start();
    }

    private void establishStreaming(Session session) {
        final SessionDestinationStreamOutput dest = streamSelectorService.postSessionsSessionDestinationStreams(session.getId());
        if (dest == null) return;
        timer = new Timer(KEEP_ALIVE_TIMER, true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RTCPClient.sendRTCPRR(dest.getSessionDestinationServiceIp(), dest.getSessionDestinationServicePort(), session != null ? session.getId().intValue() : 0);
            }
        }, 0, 10000);
        String localhost = NetUtil.getLocalHostIPAddress();
        File sdpFile = Util.writeSDPToTempFile(String.format(RTPStreamer.PLAYBACK_SDP, localhost, localhost, RTCPClient.PLAYBACK_LOCAL_PORT), requireContext());

        RTPStreamer rtpStreamer = new RTPStreamer(requireContext());
        convert2MpegtsFFmpegSession = rtpStreamer.convertStreamToMpegts(sdpFile.getAbsolutePath());

        StreamReceiverFragment.this.requireActivity().runOnUiThread(() -> initVlcPlayer(RTPStreamer.LOCAL_STREAMING_ADDRESS_VLC));

    }


    private void initVlcPlayer(String url) {
        ArrayList<String> options = new ArrayList<>();
        options.add("--network-caching=300");
        options.add("--verbose=2");
        libVLC = new LibVLC(requireContext(), options);
        mediaPlayer = new MediaPlayer(libVLC);
        IVLCVout ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(sessionSurfaceView);
        ivlcVout.attachViews();
        ivlcVout.setWindowSize(sessionSurfaceView.getWidth(), sessionSurfaceView.getHeight());
        Media media = new Media(libVLC, Uri.parse(url));
        mediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_BEST_FIT);
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.setEventListener(event -> {
            if (event == null)
                return;

            if (event.type == MediaPlayer.Event.EncounteredError) {
                AppLogger.getLogger().e("Libvlc Error occurred in media player");
            }

        });
        mediaPlayer.play();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopVlcPlayer();
    }

    private void stopVlcPlayer() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            if (libVLC != null) {
                libVLC.release();
            }
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
        }

    }

    public void handleOnBackPressed() {
        navController.navigateUp();
    }

}
