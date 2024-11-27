package org.kemea.isafeco.client.net;

import static org.kemea.isafeco.client.utils.Util.readFile;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.ReturnCode;

import org.kemea.isafeco.client.utils.AppLogger;

public class RTPStreamer {
    FFmpegSession ffmpegSession;
    Context context;
    public static final String CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA =
            "-loglevel debug -f android_camera -i 0:0 -c:v mpeg2video -b:v 256k -r:v 15 -flush_packets 1 -vf scale=320:240  -f rtp \"%s\"  %s";
    public static final String CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA_WITH_PREVIEW =
            "-loglevel debug -f android_camera -i 0:0 -map 0:v -c:v mpeg2video -b:v 256k -r:v 15 -flush_packets 1 -vf scale=320:240 -f tee \"[f=rtp]%s|[f=rtp]rtp://127.0.0.1:9095\" ";

    static final String SDP_FILE_OPTION = "-sdp_file %s";

    public RTPStreamer(Context context) {
        this.context = context;
    }

    public void startStreaming(String destinationAddress, String sdpFile, String outputFile) {
        if (destinationAddress == null || "".equalsIgnoreCase(destinationAddress))
            throw new IllegalArgumentException("Destination Address for streaming not found. Go to settings tab to set a destination address.");
        if (sdpFile != null)
            sdpFile = String.format(SDP_FILE_OPTION, sdpFile);
        String ffmpegCommand = String.format(
                CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA_WITH_PREVIEW,
                destinationAddress
        );
        AppLogger.getLogger().e(ffmpegCommand);
        ffmpegSession = FFmpegKit.executeAsync(ffmpegCommand, getfFmpegSessionCompleteCallback(sdpFile), getLogCallback(), null);
    }

    public void stopStreaming() {
        if (ffmpegSession != null && ffmpegSession.getStartTime() != null) {
            FFmpegKit.cancel(ffmpegSession.getSessionId());
        }
    }

    @NonNull
    private FFmpegSessionCompleteCallback getfFmpegSessionCompleteCallback(String sdpFile) {
        return sessionCompleted -> {
            if (ReturnCode.isSuccess(sessionCompleted.getReturnCode())) {
                AppLogger.getLogger().e("FFMPEGKit Success!!!");
            } else {
                AppLogger.getLogger().e("FFMPEGKit Error!!!");
                Toast.makeText(context, String.format("RTP Streaming Failed: %s", sessionCompleted.getOutput()), Toast.LENGTH_LONG).show();
            }
            try {
                AppLogger.getLogger().e("--------------SDP FILE---------------------");
                AppLogger.getLogger().e(new String(readFile(sdpFile)));
                AppLogger.getLogger().e("-------------------------------------------");
            } catch (Exception e) {
                AppLogger.getLogger().e(e.getMessage());
            }
            AppLogger.getLogger().e(sessionCompleted.getLogsAsString());
        };
    }

    @NonNull
    private static LogCallback getLogCallback() {
        return new LogCallback() {
            @Override
            public void apply(final com.arthenica.ffmpegkit.Log log) {
                AppLogger.getLogger().e(log.getMessage());
            }
        };
    }
}
