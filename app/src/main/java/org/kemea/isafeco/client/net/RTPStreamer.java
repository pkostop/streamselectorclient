package org.kemea.isafeco.client.net;

import static org.kemea.isafeco.client.utils.Util.readFile;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.ReturnCode;

import org.kemea.isafeco.client.utils.AppLogger;

public class RTPStreamer {
    public static final String LOCAL_STREAMING_ADDRESS_FFMPEG = "udp://127.0.0.1:9090";
    public static final String LOCAL_STREAMING_ADDRESS_VLC = "udp://@127.0.0.1:9090";
    FFmpegSession ffmpegSession;
    Context context;
    private static final String CMD_FFMPEG_RTPSTREAM_FROM_BACKCAMERA_WITH_PREVIEW =
            "-f android_camera -i 0:0 -s 176x144 -map 0:v -c:v libx265 -an -ssrc %s -f rtp %s";
    private static String FFMPEG_CMD_CONVERT_STREAM_TO_MPEGTS = " -loglevel debug -protocol_whitelist file,crypto,data,udp,rtp -probesize 5000000 -analyzeduration 10000000 -i %s -f mpegts %s";

    public static final String PLAYBACK_SDP = "v=0\n" +
            "o=- 0 0 IN IP4 %s\n" +
            "s=RTP Stream\n" +
            "c=IN IP4 %s\n" +
            "t=0 0\n" +
            "m=video %s RTP/AVP 96\n" +
            "a=rtpmap:96 H265/90000";

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

    public FFmpegSession convertStreamToMpegts(String sdpFilePath) {
        String ffmpegCmd = String.format(FFMPEG_CMD_CONVERT_STREAM_TO_MPEGTS, sdpFilePath, RTPStreamer.LOCAL_STREAMING_ADDRESS_FFMPEG);
        return runFfmpegCommand(ffmpegCmd);
    }

    public static FFmpegSession runFfmpegCommand(String ffmpegCommand) {
        FFmpegSessionCompleteCallback fFmpegSessionCompleteCallback = session -> {
            Log.i("FFMPEG-CONVERT", session.getOutput());
        };
        LogCallback logCallback = log -> AppLogger.getLogger().i(String.format("%s, %s", log.getSessionId(), log.getMessage()));
        return FFmpegKit.executeAsync(ffmpegCommand, fFmpegSessionCompleteCallback, logCallback, null);
    }
}
