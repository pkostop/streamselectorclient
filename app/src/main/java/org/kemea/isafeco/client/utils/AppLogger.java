package org.kemea.isafeco.client.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppLogger {
    static final AppLogger instance = new AppLogger();
    List<String> logs;

    public static final AppLogger getLogger() {
        return instance;
    }

    private AppLogger() {
        logs = Collections.synchronizedList(new ArrayList<String>());
    }

    public void i(String msg) {
        Log.i(AppLogger.class.getName(), msg);
        logs.add(msg);
    }

    public void e(String msg) {
        Log.e(AppLogger.class.getName(), msg);
        logs.add(msg);
    }

    public void e(Throwable t) {
        Log.e(AppLogger.class.getName(), Util.stacktrace(t));
        logs.add(Util.stacktrace(t));
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
