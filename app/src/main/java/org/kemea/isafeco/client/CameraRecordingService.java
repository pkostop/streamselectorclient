package org.kemea.isafeco.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorClient;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Util;

public class CameraRecordingService extends Service {

    public static final String CAMERA_RECORDING_CHANNEL = "CameraRecordingChannel";
    public static final String ISAFECO_CLIENT_NOTIFICATION_CHANNEL = "IsafecoClientNotificationChannel";
    public static final String ISAFECO_VIDEO_STREAMING_APPLICATION_IS_RECORDING = "ISAFECO Video Streaming Application is recording";
    RTPStreamer rtpStreamer;

    ApplicationProperties applicationProperties = null;
    String sdpFilePath = null;
    static final String CHANNEL_ID = "100";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        applicationProperties = new ApplicationProperties(this.getFilesDir().getAbsolutePath());
        sdpFilePath = String.format("%s/stream.sdp", getFilesDir());
        try {
            NotificationChannel notificationChannel = getNotificationChannel();
            createNotification(notificationChannel);
            startStreaming();
        } catch (Exception e) {
            AppLogger.getLogger().e(Util.stacktrace(e));
            Toast.makeText(this, String.format("Cannot Start Service: %s", e.getMessage()), Toast.LENGTH_LONG).show();
        }

    }

    private void startStreaming() throws Exception {
        rtpStreamer = new RTPStreamer(getApplicationContext());
        String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
        if (streamingAddress != null && !"".equalsIgnoreCase(streamingAddress)) {
            rtpStreamer.startStreaming(streamingAddress, sdpFilePath, getFilesDir() + "/output.mpg");
            return;
        }
        streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
        if (streamingAddress != null && !"".equalsIgnoreCase(streamingAddress)) {
            trasmitToStreamSelector();
        }

    }

    private void trasmitToStreamSelector() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiKey = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_API_KEY);
                    String address = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
                    StreamSelectorClient streamSelectorClient = new StreamSelectorClient(address, apiKey);
                    String userName = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
                    String password = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
                    SessionSourceStreamOutput sessionSourceStreamOutput = streamSelectorClient.postSessionsSessionSourceStreams(100L, userName, password, "");
                    String streamingAddress = String.format("rtp://%s:%s", sessionSourceStreamOutput.getSessionSourceServiceIp(), sessionSourceStreamOutput.getSessionSourceServicePort());
                    showToast(String.format("Streaming to %s", streamingAddress));
                    rtpStreamer.startStreaming(streamingAddress, sdpFilePath, getFilesDir() + "/output.mpg");
                } catch (Exception e) {
                    AppLogger.getLogger().e(e);
                }

            }
        }).start();

    }

    void showToast(String msg) {
        if (getApplicationContext() != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

            });

        }
    }

    protected NotificationChannel getNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (notificationChannel == null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, ISAFECO_CLIENT_NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(ISAFECO_VIDEO_STREAMING_APPLICATION_IS_RECORDING);
            notificationManager.createNotificationChannel(channel);
        }
        return notificationChannel;
    }

    protected void createNotification(NotificationChannel notificationChannel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CAMERA_RECORDING_CHANNEL);
        builder.setContentTitle(ISAFECO_VIDEO_STREAMING_APPLICATION_IS_RECORDING);
        if (notificationChannel != null)
            builder.setChannelId(notificationChannel.getId());
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(Integer.parseInt(CHANNEL_ID), notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
        } else {
            startForeground(Integer.parseInt(CHANNEL_ID), notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rtpStreamer != null) {
            rtpStreamer.stopStreaming();
        }

    }
}
