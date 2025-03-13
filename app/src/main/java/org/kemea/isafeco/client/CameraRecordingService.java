package org.kemea.isafeco.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorService;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Util;

public class CameraRecordingService extends Service {

    public static final String CAMERA_RECORDING_CHANNEL = "CameraRecordingChannel";
    public static final String ISAFECO_CLIENT_NOTIFICATION_CHANNEL = "IsafecoClientNotificationChannel";
    public static final String ISAFECO_VIDEO_STREAMING_APPLICATION_IS_RECORDING = "ISAFECO Video Streaming Application is recording";
    RTPStreamer rtpStreamer;
    StreamSelectorService streamSelectorService;

    ApplicationProperties applicationProperties = null;
    String sdpFilePath = null;
    static final String CHANNEL_ID = "100";
    private Long sessionId;

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
            streamSelectorService = new StreamSelectorService(getApplicationContext());
            rtpStreamer = new RTPStreamer(getApplicationContext());
            createNotification(getNotificationChannel());
            startStreaming();
        } catch (Exception e) {
            AppLogger.getLogger().e(Util.stacktrace(e));
            Toast.makeText(this, String.format("Cannot Start Service: %s", e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLowMemory() {
        AppLogger.getLogger().e("Memory Low");
    }

    @Override
    public void onTrimMemory(int level) {
        AppLogger.getLogger().e(String.format("Trim Memory level: %s", level));
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        if (TRIM_MEMORY_RUNNING_CRITICAL == level) {
            rtpStreamer.stopStreaming();
            Toast.makeText(getApplicationContext(), String.format("Low memory, total heap: %skb-, streaming is stopped", memoryInfo.getTotalPss()), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        AppLogger.getLogger().e("Unbinding Foreground Service");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        AppLogger.getLogger().e("Rebinding Foreground Service");
    }

    private void startStreaming() throws Exception {
        if (rtpStreamer == null || applicationProperties == null)
            return;

        String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
        if (!Util.isEmpty(streamingAddress)) {
            trasmitToStreamSelector();
            return;
        }
        streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
        if (!Util.isEmpty(streamingAddress)) {
            rtpStreamer.startStreaming(streamingAddress, sdpFilePath, 100L);
        }
    }

    public static final String SDP = "\n" +
            "o=- 0 0 IN IP4 127.0.0.1\n" +
            "s=%s\n" +
            "c=IN IP4 127.0.0.1\n" +
            "t=0 0\n" +
            "a=tool:libavformat LIBAVFORMAT_VERSION\n" +
            "m=video 0 RTP/AVP 96\n" +
            "a=rtpmap:96 H265/90000\n";

    private void trasmitToStreamSelector() throws Exception {
        if (streamSelectorService == null || rtpStreamer == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String organizationUnit = null;
                    if (applicationProperties.getProperty(ApplicationProperties.PROP_USER_ORG) != null)
                        organizationUnit = getResources().getStringArray(R.array.organizations)[Integer.parseInt(applicationProperties.getProperty(ApplicationProperties.PROP_USER_ORG))];
                    SessionSourceStreamOutput sessionSourceStreamOutput = streamSelectorService.postSessionsSessionSourceStreams(1L, String.format(SDP, organizationUnit != null ? organizationUnit : "ALL_FORCES"));
                    sessionId = sessionSourceStreamOutput.getSessionId();
                    String streamingAddress = String.format("%s://%s:%s?pkt_size=1316", sessionSourceStreamOutput.getSessionSourceServiceProtocol(), sessionSourceStreamOutput.getSessionSourceServiceIp(), sessionSourceStreamOutput.getSessionSourceServicePort());
                    AppLogger.getLogger().e(String.format("Streaming to %s", streamingAddress));
                    rtpStreamer.startStreaming(streamingAddress, sdpFilePath, sessionSourceStreamOutput.getSessionId());
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
        if (sessionId != null) {
            new Thread(() -> {
                try {
                    streamSelectorService.sessionClose(sessionId);
                } catch (Exception e) {
                    AppLogger.getLogger().e(e.getMessage());
                }
            });
        }
        if (rtpStreamer != null) {
            rtpStreamer.stopStreaming();
        }

    }
}
