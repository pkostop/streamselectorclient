package org.kemea.isafeco.client.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class RTCPClient {
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

    // Send RTCP Receiver Reports (RR)
    public static void sendRTCPRR(String ip, int port, int ssrc) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            DatagramSocket socket = new DatagramSocket();

            while (true) {
                // Create RTCP RR packet
                byte[] rrPacket = createRTCPRR(ssrc);

                // Send RTCP RR
                DatagramPacket packet = new DatagramPacket(rrPacket, rrPacket.length, address, port);
                socket.send(packet);
                System.out.println("Sent RTCP RR to " + ip + ":" + port);

                // Sleep for 20 seconds before sending the next packet
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

