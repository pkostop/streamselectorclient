package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.streamselector.stubs.output.ClusterInfo;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.Session;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionInfo;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import fi.iki.elonen.NanoHTTPD;

public class StreamSelectorRestMock extends NanoHTTPD {
    public StreamSelectorRestMock(int port) {
        super(port);
    }


    private static Map<Integer, SessionSourceStreamOutput> sessions =
            new HashMap<Integer, SessionSourceStreamOutput>();
    AtomicInteger counter = new AtomicInteger(1000);
    AtomicInteger port = new AtomicInteger(9001);

    String sessionSourceStream = "{\n" +
            "session_source_service_protocol : rtp,\n" +
            "session_source_service_ip:127.0.0.1,\n" +
            "session_source_service_port:%s,\n" +
            "session_id:%s,\n" +
            "session_encryption_key:null\n" +
            "}";
    String sessionDestinationStream =
            "{session_destination_service_protocol: rtp," +
                    "session_destination_service_ip:127.0.0.1," +
                    "session_destination_service_port:%s," +
                    "session_sdp:\"\"," +
                    "session_decryption_key:\"\"" +
                    "}";

    String loginUserOutput = "{\n" +
            "\"id\":\"%s\"," +
            "\"login\":\"%s\"," +
            "\"name\":\"TEST\"," +
            "\"email\":\"test@gmail.com\"," +
            "\"phone\":\"\"," +
            "\"type\":\"1\"," +
            "\"token\":\"123ABC\"," +
            "\"application_id\":\"1\"," +
            "\"contract_id\":\"1\"," +
            "\"contract_name\":\"ISAFECO-KEMEA\"" +
            "}\n";


    @Override
    public Response serve(IHTTPSession session) {
        if (session.getUri().contains("/sessions/session-source-streams")) {
            int _counter = counter.getAndIncrement();
            int _port = port.incrementAndGet();
            String response = String.format(sessionSourceStream, String.valueOf(_port), String.valueOf(_counter));
            sessions.put(_counter, Util.fromJson(response,
                    SessionSourceStreamOutput.class));
            return newFixedLengthResponse(response);
        } else if (session.getUri().contains("/sessions/session-destination-streams")) {

            return newFixedLengthResponse(Response.Status.OK, "application/json", String.format(sessionDestinationStream, String.valueOf(port.get())));
        } else if (session.getUri().contains("/sessions")) {
            List<Session> sessionList = new ArrayList<>();
            for (SessionSourceStreamOutput sessionSourceStreamOutput : sessions.values()) {
                SessionInfo sessionInfo = new SessionInfo();
                sessionInfo.setId(sessionSourceStreamOutput.getSessionId());
                sessionInfo.setSdp(sessionSourceStreamOutput.getSessionSDP());
                sessionInfo.setCreatedAt(null);
                ClusterInfo clusterInfo = new ClusterInfo();
                clusterInfo.setClusterId(1);
                clusterInfo.setContractId(1);
                Session sess = new Session(sessionInfo, clusterInfo);
                sessionList.add(sess);
            }
            GetSessionsOutput getSessionsOutput = new GetSessionsOutput();
            getSessionsOutput.setSessions(sessionList.toArray(new Session[0]));
            getSessionsOutput.setTotal_sessions(sessionList.size());
            String response = Util.toJson(getSessionsOutput);
            return newFixedLengthResponse(response);
        } else if (session.getUri().contains("/users/login")) {
            String result = String.format(loginUserOutput, "1", "isafeco");
            return newFixedLengthResponse(Response.Status.OK, "application/json", result);
        }
        return super.serve(session);
    }

}

