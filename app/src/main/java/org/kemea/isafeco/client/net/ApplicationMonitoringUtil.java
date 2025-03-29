package org.kemea.isafeco.client.net;

import android.net.TrafficStats;

public class ApplicationMonitoringUtil {

    public static long getUsedHeapMemory() {
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return usedMemory / (1024 * 1024);
    }

    public static long getTransmittedBytes() {
        return TrafficStats.getUidTxBytes(android.os.Process.myUid()) / (1024 * 1024);
    }
}
