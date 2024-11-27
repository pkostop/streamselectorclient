package org.kemea.isafeco.client.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    public static final String FILE_NAME = "application.properties";
    public static final String PROP_RTP_STREAMING_ADDRESS = "RTP_STREAMING_ADDRESS";
    public static final String PROP_STREAM_SELECTOR_ADDRESS = "STREAM_SELECTOR_ADDRESS";
    public static final String PROP_STREAM_SELECTOR_USERNAME = "STREAM_SELECTOR_USERNAME";
    public static final String PROP_STREAM_SELECTOR_PASSWORD = "STREAM_SELECTOR_PASSWORD";
    public static final String PROP_STREAM_SELECTOR_API_KEY = "STREAM_SELECTOR_API_KEY";
    Properties properties = new Properties();
    String filePath = null;

    public ApplicationProperties(String filePath) {
        this.filePath = String.format("%s/%s", filePath, FILE_NAME);
        if (!load()) {
            setProperty(PROP_RTP_STREAMING_ADDRESS, "");
            setProperty(PROP_STREAM_SELECTOR_ADDRESS, "");
            save();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void save() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            properties.store(fos, "");
        } catch (IOException e) {
            AppLogger.getLogger().e(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    AppLogger.getLogger().e(e);
                }
            }
        }
    }

    private boolean load() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            properties.load(fis);
            return true;
        } catch (IOException e) {
            AppLogger.getLogger().e(e);
            return false;
        }
    }
}
