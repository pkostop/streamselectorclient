package org.kemea.isafeco.client.ui.streamreceiver;

import static androidx.media3.exoplayer.SimpleExoPlayer.*;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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

import org.kemea.isafeco.client.R;

public class StreamReceiverFragment extends Fragment {
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private NavController navController;

    private static final String TAG = "LiveStreamFragment";
    private String rtspUrl = "rtsp://192.168.1.9:8554/live.stream";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout containing the PlayerView
        Log.d(TAG, ">>> Player onCreateView()");

        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        playerView = view.findViewById(R.id.player_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Retrieve RTSP URL from arguments
        if (getArguments() != null) {
            rtspUrl = getArguments().getString("RTSP_URL", "");
            //FIXME: DELETE THE HARDCODE URL BELOW:
            rtspUrl = "rtsp://192.168.1.9:8554/live.stream";
        }

        if (rtspUrl == null || rtspUrl.isEmpty()) {
            Toast.makeText(requireContext(), "No RTSP URL provided", Toast.LENGTH_SHORT).show();
            return;
        }

        //call init to initialize ExoPlayer
        initializePlayer();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer() {

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
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(rtspUrl));

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
        if (exoPlayer != null) {
            exoPlayer.clearVideoSurface();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
    public void handleOnBackPressed() {
        navController.navigateUp();
    }
}
