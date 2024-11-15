package org.kemea.isafeco.client.streamselector.stubs.input;

import com.google.gson.annotations.SerializedName;

public class SessionDestinationStreamInput {

	@SerializedName(value = "session_id")
	Integer sessionId;

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

}
