package org.kemea.isafeco.client;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_player);

        // Get the RTSP URL passed from the previous activity
        String rtspUrl = getIntent().getStringExtra("RTSP_URL");
        videoView = findViewById(R.id.player_view);

        // Set the URI for the VideoView
        videoView.setVideoURI(Uri.parse(rtspUrl));

        // Set a listener for when the video is prepared
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // Optional: loop the video
                videoView.start();  // Start the video
            }
        });
        // Set a listener for errors
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoPlayerActivity.this,
                        "Error occurred while playing video: " + what + ", " + extra,
                        Toast.LENGTH_SHORT).show();
                return true; // Return true if the error is handled
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause(); // Pause the video when activity is paused
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback(); // Release resources when activity is destroyed
        }
    }
}
