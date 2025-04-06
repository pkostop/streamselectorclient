package org.kemea.isafeco.client.net;

import android.net.TrafficStats;

import org.kemea.isafeco.client.utils.AppLogger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ApplicationMonitoringUtil {

    public static long getUsedHeapMemory() {
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return usedMemory / (1024 * 1024);
    }

    public static long getTransmittedBytes() {
        return TrafficStats.getUidTxBytes(android.os.Process.myUid()) / (1024 * 1024);
    }

    public static String getDeviceIPAddress() {
        Enumeration<NetworkInterface> networkInterfaceEnum = null;
        try {
            networkInterfaceEnum = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        if (networkInterfaceEnum == null) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                AppLogger.getLogger().e(e);
            }
        }
        while (networkInterfaceEnum.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnum.nextElement();
            try {
                if (networkInterface.isLoopback()) continue;
                if (networkInterface.getName() != null && networkInterface.getName().startsWith("dummy"))
                    continue;
            } catch (SocketException e) {
                AppLogger.getLogger().e(e);
            }
            if (networkInterface.getInetAddresses() != null) {
                InetAddress inetAddress = networkInterface.getInetAddresses().nextElement();
                return inetAddress != null ? inetAddress.getHostAddress() : null;
            }
        }
        return null;
    }
}
