package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.utils.AppLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NetUtil {
    public static byte[] post(String _url, String message, Map<String, String> headers, String charset, int readTimeout,
                              int connectTimeout) throws Exception {
        URL url = new URL(_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("charset", charset);
        conn.setRequestProperty("Content-Length", Integer.toString(message.length()));
        for (String key : headers.keySet()) {
            conn.setRequestProperty(key, (String) headers.get(key));
        }
        conn.setUseCaches(false);
        conn.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
        conn.connect();
        return parseInputStream(conn.getInputStream());
    }

    public static byte[] get(String url, Map<String, String> headers, int connectTimeout, int readTimeout) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) _url.openConnection();
        urlConnection.setConnectTimeout(connectTimeout);
        urlConnection.setReadTimeout(readTimeout);
        for (String key : headers.keySet()) {
            urlConnection.setRequestProperty(key, headers.get(key));
        }
        urlConnection.connect();
        byte[] response = parseInputStream(urlConnection.getInputStream());
        if (urlConnection.getResponseCode() != 200 && urlConnection.getResponseCode() != 204)
            throw new RuntimeException(String.format("Error calling %s, error: %s, response: %s", url, urlConnection.getResponseCode(), new String(parseInputStream(urlConnection.getInputStream()))));
        return response;
    }

    public static byte[] parseInputStream(InputStream is) throws Exception {
        if (is == null)
            return null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;

        try {
            bis = new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[16384];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) > -1)
                baos.write(buffer, 0, bytesRead);
            return baos.toByteArray();
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
            return null;
        } finally {
            if (baos != null)
                try {
                    baos.close();
                } catch (Exception e) {
                    AppLogger.getLogger().e(e);
                }
            if (bis != null)
                bis.close();
        }
    }

}
