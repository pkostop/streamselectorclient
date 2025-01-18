package org.kemea.isafeco.client.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppLogger {
    static final AppLogger instance = new AppLogger();
    List<String> logs;
    public static final int LOG_SIZE = 500;

    public static final AppLogger getLogger() {
        return instance;
    }

    private AppLogger() {
        logs = Collections.synchronizedList(new ArrayList<String>());
    }

    public void i(String msg) {
        Log.i(AppLogger.class.getName(), msg);
        if (logs.size() > LOG_SIZE)
            logs.clear();
        logs.add(msg);
    }

    public void e(String msg) {
        if (msg == null)
            return;
        Log.e(AppLogger.class.getName(), msg);
        if (logs.size() > LOG_SIZE)
            logs.clear();
        logs.add(msg);
    }

    public void e(Throwable t) {
        Log.e(AppLogger.class.getName(), Util.stacktrace(t));
        if (logs.size() > LOG_SIZE)
            logs.clear();
        logs.add(Util.stacktrace(t));
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
