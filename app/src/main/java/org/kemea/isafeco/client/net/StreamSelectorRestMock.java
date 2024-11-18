package org.kemea.isafeco.client.net;

import fi.iki.elonen.NanoHTTPD;

public class StreamSelectorRestMock extends NanoHTTPD {
    public StreamSelectorRestMock(int port) {
        super(port);
    }

    String sessionSourceStream = "{\n" +
            "session_source_service_protocol : udp,\n" +
            "session_source_service_ip:127.0.0.1,\n" +
            "session_source_service_port:9094,\n" +
            "session_id:1000,\n" +
            "session_encryption_key:null\n" +
            "}";

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getUri().contains("/sessions/session-source-streams")) {
            return newFixedLengthResponse(sessionSourceStream);
        }
        return super.serve(session);
    }

}

