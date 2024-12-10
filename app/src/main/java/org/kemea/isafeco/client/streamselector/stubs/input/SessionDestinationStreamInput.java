package org.kemea.isafeco.client.streamselector.stubs.input;

import com.google.gson.annotations.SerializedName;

public class SessionDestinationStreamInput {

    @SerializedName(value = "session_id")
    Long sessionId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

}
