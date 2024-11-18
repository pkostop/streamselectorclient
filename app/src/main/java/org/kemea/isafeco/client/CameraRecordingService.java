package org.kemea.isafeco.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
            rtpStreamer = new RTPStreamer();
        } catch (Exception e) {
            AppLogger.getLogger().e(Util.stacktrace(e));
            Toast.makeText(this, "Cannot Start Service", Toast.LENGTH_LONG).show();
        }

    }

    @Nullable
    private void startStreaming() throws Exception {
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

    @NonNull
    private void trasmitToStreamSelector() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StreamSelectorClient streamSelectorClient = new StreamSelectorClient(applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS));
                    String userName = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
                    String password = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
                    SessionSourceStreamOutput sessionSourceStreamOutput = null;
                    sessionSourceStreamOutput = streamSelectorClient.postSessionsSessionSourceStreams(100L, userName, password, "");
                    String streamingAddress = String.format("rtp://%s:%s", sessionSourceStreamOutput.getSessionSourceServiceIp(), sessionSourceStreamOutput.getSessionSourceServicePort());
                    rtpStreamer.startStreaming(streamingAddress, sdpFilePath, getFilesDir() + "/output.mpg");
                } catch (Exception e) {
                    AppLogger.getLogger().e(e);
                }

            }
        }).start();

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
        Notification notification = new NotificationCompat.Builder(this, CAMERA_RECORDING_CHANNEL).
                setContentTitle(ISAFECO_VIDEO_STREAMING_APPLICATION_IS_RECORDING).setChannelId(notificationChannel.getId()).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(new Integer(CHANNEL_ID), notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
        } else {
            startForeground(new Integer(CHANNEL_ID), notification);
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
