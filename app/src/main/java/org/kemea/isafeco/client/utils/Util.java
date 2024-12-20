package org.kemea.isafeco.client.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class Util {

    public static byte[] readFile(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        while (fis.read(buffer, 0, 512) >= 0) {
            baos.write(buffer);
        }
        fis.close();
        return baos.toByteArray();
    }

    public static String stacktrace(Throwable throwable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        throwable.printStackTrace(printStream);
        return new String(baos.toByteArray());
    }

    public static <T> String toJson(T entity) {
        return new GsonBuilder().create().toJson(entity);
    }

    public static <T> T fromJson(String json, Class<T> _class) {
        return new GsonBuilder().create().fromJson(json, _class);
    }

    public static boolean isEmpty(String val) {
        return val == null || "".equalsIgnoreCase(val) || "null".equalsIgnoreCase(val);
    }

    public static void toast(Activity activity, String msg) {
        if (activity == null)
            return;
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
        }
    }

    public static File writeSDPToTempFile(String sdpContent, Context context) {
        try {
            // Get the cache directory (temporary storage directory for the app)
            File tempFile = File.createTempFile("stream", ".sdp", context.getCacheDir());

            // Write the SDP content to the file
            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                writer.write(sdpContent);
                writer.flush();
            }

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
