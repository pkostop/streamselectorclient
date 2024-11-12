package org.kemea.isafeco.client;

import android.annotation.SuppressLint;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import org.kemea.isafeco.client.net.RTPStreamer;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Util;

public class CameraRecordingService extends Service {

    public static final String CAMERA_RECORDING_CHANNEL = "CameraRecordingChannel";
    RTPStreamer rtpStreamer;
    ApplicationProperties applicationProperties=null;
    String sdpFilePath=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        applicationProperties = new ApplicationProperties(this.getFilesDir().getAbsolutePath());
        sdpFilePath = String.format("%s/stream.sdp", getFilesDir());

        try {
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
            }
            Notification notification=new NotificationCompat.Builder(this, CAMERA_RECORDING_CHANNEL).
                    setContentTitle("ISAFECO Camera streaming").build();
            startForeground(100,notification,type);

            String streamingAddress = applicationProperties.getProperty(ApplicationProperties.PROP_RTP_STREAMING_ADDRESS);
            rtpStreamer = new RTPStreamer();
            rtpStreamer.startStreaming(streamingAddress, sdpFilePath, getFilesDir() + "/output.mpg");
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    e instanceof ForegroundServiceStartNotAllowedException
            ) {
                AppLogger.getLogger().e(Util.stacktrace(e));
                Toast.makeText(this, "Cannot Start Service", Toast.LENGTH_LONG).show();
            }

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
