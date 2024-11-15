package org.kemea.isafeco.client.utils;

import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

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


}
