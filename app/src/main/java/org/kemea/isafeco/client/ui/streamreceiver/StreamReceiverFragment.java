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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;
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
    private PlayerView playerView;
    private SurfaceView sessionSurfaceView;
    private ExoPlayer exoPlayer;
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
        stopVlcPlayer();
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
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            exoPlayer.play();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopVlcPlayer();
    }

    private void stopExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.clearVideoSurface();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void stopVlcPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                AppLogger.getLogger().e(e);
            }
        }
        if (libVLC != null) {
            libVLC.release();
        }
    }

    public void handleOnBackPressed() {
        navController.navigateUp();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializeExoPlayer(String rtpUrl) {

        if (playerView == null) {
            Log.e(TAG, "*** PlayerView is null. Ensure the layout is inflated correctly.");
            Toast.makeText(requireContext(), "PlayerView not initialized", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Create a DefaultTrackSelector for track selection.
        TrackSelector trackSelector = new DefaultTrackSelector(requireContext());

        // 2. Create a DefaultLoadControl for buffering management (optional, but recommended).
        LoadControl loadControl = new DefaultLoadControl();
        exoPlayer = new ExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl) // Optional
                .build();

        //exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        exoPlayer.clearVideoSurface();
        Log.d(TAG, " >>> PlayerView: " + playerView);

        playerView.setPlayer(exoPlayer);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("ExoPlayer", "Playback error: " + error.getMessage(), error);
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Log.d("ExoPlayer", "Surface size changed: " + width + " x " + height);
            }
        });

        try {
            // Create a MediaItem with the RTSP URL
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(rtpUrl));

            // Set the media item to the player
            exoPlayer.setMediaItem(mediaItem);


            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                    if (events.contains(Player.EVENT_PLAYER_ERROR)) {
                        PlaybackException error = player.getPlayerError();
                        if (error != null) {
                            Log.e(TAG, "Playback error: " + error.getMessage(), error);

                            // Display error to the user
                            TextView errorMessage = requireView().findViewById(R.id.error_message);
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText("Error: " + error.getMessage());
                            Toast.makeText(requireContext(), "Playback error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            // Prepare and start the player
            exoPlayer.prepare();
            exoPlayer.play();
        } catch (Exception e) {
            Log.e(TAG, "Player initialization failed", e);
        }
    }
}
