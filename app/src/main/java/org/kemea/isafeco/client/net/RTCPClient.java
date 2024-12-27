package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.utils.AppLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class RTCPClient {
    public static final int PLAYBACK_LOCAL_PORT = 56000;

    public static byte[] createRTCPRR(int ssrc) {
        ByteBuffer buffer = ByteBuffer.allocate(32);

        // RTCP Header
        buffer.put((byte) 0x81); // Version 2, no padding, 1 reception report
        buffer.put((byte) 201); // Packet Type: 201 (RR)
        buffer.putShort((short) 7); // Length (in 32-bit words minus one)

        // SSRC of sender
        buffer.putInt(ssrc);

        // Placeholder values for reception report block
        buffer.putInt(0); // SSRC of source being reported
        buffer.putInt(0); // Fraction lost and cumulative number of packets lost
        buffer.putInt(0); // Extended highest sequence number received
        buffer.putInt(0); // Interarrival jitter
        buffer.putInt(0); // Last SR timestamp
        buffer.putInt(0); // Delay since last SR

        return buffer.array();
    }

    public static void sendRTCPRR(String ip, int port, int ssrc) {
        DatagramSocket socket = null;
        byte[] rrPacket = createRTCPRR(ssrc);
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        DatagramPacket packet = new DatagramPacket(rrPacket, rrPacket.length, address, port);
        try {
            socket = new DatagramSocket(PLAYBACK_LOCAL_PORT);
            socket.send(packet);
        } catch (IOException ioe) {
            AppLogger.getLogger().e(ioe.getMessage());
        } finally {
            if (socket != null) {
                socket.disconnect();
                socket.close();
            }
        }
        AppLogger.getLogger().e("Sent RTCP RR to " + ip + ":" + port);
    }

    public static void main(String[] args) {
        // Change these values to match your setup
        String rtcpIp = "10.10.10.74";
        int rtcpPort = 30100;
        int ssrc = 12345678;

        // Start sending RTCP RRs
        sendRTCPRR(rtcpIp, rtcpPort, ssrc);
    }
}

