package org.kemea.isafeco.client.net;

import org.kemea.isafeco.client.streamselector.stubs.input.LoginInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionDestinationStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.Util;

import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class StreamSelectorRestMock extends NanoHTTPD {
    public StreamSelectorRestMock(int port) {
        super(port);
    }

    /*
        private static Map<Integer, SessionSourceStreamOutput> sessions =
                new HashMap<Integer, SessionSourceStreamOutput>();
        AtomicInteger counter = new AtomicInteger(1000);
        AtomicInteger port = new AtomicInteger(9094);
      */
    String sessionSourceStream = "{\n" +
            "session_source_service_protocol : udp,\n" +
            "session_source_service_ip:127.0.0.1,\n" +
            "session_source_service_port:%s,\n" +
            "session_id:%s,\n" +
            "session_encryption_key:null\n" +
            "}";
    String sessionDestinationStream =
            "{\"session_destination_service_protocol\": \"http\"" +
                    "\"session_destination_service_ip\":\"127.0.0.1\"" +
                    "\"session_destination_service_port\":\"%s\"" +
                    "\"session_sdp:\"\"" +
                    "\"session_decryption_key:\"\"";

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
        String request = null;
        try {
            request = new String(NetUtil.parseInputStream(session.getInputStream()));
            AppLogger.getLogger().i(request);
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
        }
        if (session.getUri().contains("/sessions/session-source-streams")) {
            //        int _counter = counter.getAndIncrement();
            //        int _port = port.getAndIncrement();
            //String response = String.format(sessionSourceStream, String.valueOf(9094), String.valueOf(1));
            //        sessions.put(_counter, Util.fromJson(response,
            //                SessionSourceStreamOutput.class));
            String response = "OK";
            Response _response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", response);
            _response.setGzipEncoding(false); // Disable GZIP compression
            _response.addHeader("Connection", "close");
            _response.addHeader("content-length", String.valueOf(response.length()));
            return _response;
        } else if (session.getUri().contains("/sessions/session-destination-streams")) {
            SessionDestinationStreamInput sessionDestinationStreamInput =
                    Util.fromJson(request, SessionDestinationStreamInput.class);
            //      SessionSourceStreamOutput sessionSourceStreamOutput = sessions.get(sessionDestinationStreamInput.getSessionId());
            //    if (sessionSourceStreamOutput == null)
            //        return newFixedLengthResponse("{\"code\":\"404\", \"message\":\"NOT FOUND\"}");
            return newFixedLengthResponse(Response.Status.OK, "application/json", String.format(sessionDestinationStream, String.valueOf(9094)));
        } else if (session.getUri().contains("/sessions")) {
            List<GetSessionsOutput> getSessionsOutputs = new ArrayList<GetSessionsOutput>();
/*
            for (SessionSourceStreamOutput sessionSourceStreamOutput : sessions.values()) {
                GetSessionsOutput getSessionsOutput = new GetSessionsOutput();
                getSessionsOutputs.add(getSessionsOutput);
                SessionInfo sessionInfo = new SessionInfo();
                sessionInfo.setId(sessionSourceStreamOutput.getSessionId());
                sessionInfo.setSdp(sessionSourceStreamOutput.getSessionSDP());
                getSessionsOutput.setSessions(new Session[]{
                        new Session(sessionInfo, new ClusterInfo(1, 1))
                });

            }*/
            return newFixedLengthResponse(Response.Status.OK, "application/json", Util.toJson(getSessionsOutputs.toArray(new GetSessionsOutput[0])));
        } else if (session.getUri().contains("/users/login")) {
            LoginInput loginInput = Util.fromJson(request, LoginInput.class);
            String result = String.format(loginUserOutput, "1", loginInput.getLogin());
            return newFixedLengthResponse(Response.Status.OK, "application/json", result);
        }
        return super.serve(session);
    }

}

