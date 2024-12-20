package org.kemea.isafeco.client.net;

import static org.kemea.isafeco.client.utils.Util.readFile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.ReturnCode;

import org.kemea.isafeco.client.utils.AppLogger;

public class RTPStreamer {
    public static final String PREVIEW_MPEGTS_ADDRESS = "udp://@127.0.0.1:9095";
    FFmpegSession ffmpegSession;
    FFmpegSession rtpToHlsSession = null;
    Context context;
    private static final String CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA_WITH_PREVIEW =
            "-loglevel debug -f android_camera -i 0:0 -s 176x144 -map 0:v -c:v libx264 -an -ssrc %s -f rtp %s";


    static final String SDP_FILE_OPTION = "-sdp_file %s";

    public RTPStreamer(Context context) {
        this.context = context;
    }

    public void startStreaming(String destinationAddress, String sdpFile, Long sessionId) {
        if (destinationAddress == null || "".equalsIgnoreCase(destinationAddress))
            throw new IllegalArgumentException("Destination Address for streaming not found. Go to settings tab to set a destination address.");
        String sdpOption = null;
        if (sdpFile != null)
            sdpOption = String.format(SDP_FILE_OPTION, sdpFile);
        String ffmpegCommand = String.format(
                CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA_WITH_PREVIEW,
                sessionId,
                destinationAddress
        );
        AppLogger.getLogger().e(ffmpegCommand);
        ffmpegSession = FFmpegKit.executeAsync(ffmpegCommand, getfFmpegSessionCompleteCallback(sdpFile), getLogCallback(), null);
    }

    public void stopStreaming() {
        if (rtpToHlsSession != null && rtpToHlsSession.getStartTime() != null) {
            FFmpegKit.cancel(rtpToHlsSession.getSessionId());
        }
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
                AppLogger.getLogger().e(sessionCompleted.getOutput());
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

    public FFmpegSession receiveStream(String protocol, String host, int port) {
        String originAddress = String.format("%s://%s:%s", protocol, host, port);
        String FFMPEG_CMD_RECEIVE_STREAM = "ffmpeg -i %s -c:v copy -f rtp_mpegts -rtcpport %s -flags +send_rtcp udp://127.0.0.1:9011\n";
        return runFfmpegCommand(String.format(FFMPEG_CMD_RECEIVE_STREAM, originAddress, port));
    }

    public static FFmpegSession runFfmpegCommand(String ffmpegCommand) {
        FFmpegSessionCompleteCallback fFmpegSessionCompleteCallback = session -> {
            AppLogger.getLogger().i(session.getOutput());
        };
        LogCallback logCallback = log -> AppLogger.getLogger().i(String.format("%s, %s", log.getSessionId(), log.getMessage()));
        return FFmpegKit.executeAsync(ffmpegCommand, fFmpegSessionCompleteCallback, logCallback, null);
    }
}
