package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.utils.AppLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

public class NetUtil {
    public static byte[] post(String _url, String message, Map<String, String> headers, String charset, int readTimeout, int connectTimeout) throws Exception {
        URL url = new URL(_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        AppLogger.getLogger().i(message);
        byte[] messageBytes = message.getBytes();
        conn.setRequestProperty("Content-Length", Integer.toString(message.length()));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(messageBytes);
            os.flush();
        }
        byte[] result = null;
        conn.connect();
        int responseCode = conn.getResponseCode();
        AppLogger.getLogger().i(String.valueOf(responseCode));

        try (InputStream inputStream = (responseCode == 200 || responseCode == 204)
                ? conn.getInputStream()
                : conn.getErrorStream()) {
            result = parseInputStream(inputStream);
        }
        conn.disconnect();
        AppLogger.getLogger().i(new String(result));
        if (responseCode != 200 && responseCode != 204) {
            throw new IOException(String.format("Network Error: Http Status %s - %s", responseCode, new String(result)));
        }
        return result;
    }


    public static byte[] get(String url, Map<String, String> headers, int connectTimeout, int readTimeout) throws Exception {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL _url = new URL(url);
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.setInstanceFollowRedirects(true);
            // Set headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // Connect and read response
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            inputStream = (responseCode == 200 || responseCode == 204)
                    ? urlConnection.getInputStream()
                    : urlConnection.getErrorStream();

            byte[] response = parseInputStream(inputStream);

            if (responseCode != 200 && responseCode != 204) {
                throw new IOException(String.format("Error calling %s, error: %d, response: %s",
                        url, responseCode, new String(response)));
            }
            AppLogger.getLogger().i(url);
            AppLogger.getLogger().i(new String(response));
            return response;
        } finally {
            // Close resources
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
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

    public static String getLocalHostIPAddress() {
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostAddress;
    }
}
