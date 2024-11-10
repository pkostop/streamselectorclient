package org.kemea.isafeco.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;

public class Util {

    public static final String APPLICATION_PROPERTIES_FILE = "application.properties";

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


}
