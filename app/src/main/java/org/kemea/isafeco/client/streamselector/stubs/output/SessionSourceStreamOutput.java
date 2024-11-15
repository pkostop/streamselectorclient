package org.kemea.isafeco.client.streamselector.stubs.output;

import com.google.gson.annotations.SerializedName;

public class SessionSourceStreamOutput {
	@SerializedName("session_source_service_protocol")
	String sessionSourceServiceProtocol;
	@SerializedName("session_source_service_ip")
	String sessionSourceServiceIp;
	@SerializedName("session_source_service_port")
	Integer sessionSourceServicePort;
	@SerializedName("session_id")
	Integer sessionId;
	@SerializedName("session_encryption_key")
	String sessionEncryptionKey;
	@SerializedName("session_sdp")
	String sessionSDP;

	public String getSessionSourceServiceProtocol() {
		return sessionSourceServiceProtocol;
	}

	public void setSessionSourceServiceProtocol(String sessionSourceServiceProtocol) {
		this.sessionSourceServiceProtocol = sessionSourceServiceProtocol;
	}

	public String getSessionSourceServiceIp() {
		return sessionSourceServiceIp;
	}

	public void setSessionSourceServiceIp(String sessionSourceServiceIp) {
		this.sessionSourceServiceIp = sessionSourceServiceIp;
	}

	public Integer getSessionSourceServicePort() {
		return sessionSourceServicePort;
	}

	public void setSessionSourceServicePort(Integer sessionSourceServicePort) {
		this.sessionSourceServicePort = sessionSourceServicePort;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionEncryptionKey() {
		return sessionEncryptionKey;
	}

	public void setSessionEncryptionKey(String sessionEncryptionKey) {
		this.sessionEncryptionKey = sessionEncryptionKey;
	}

	public String getSessionSDP() {
		return sessionSDP;
	}

	public void setSessionSDP(String sessionSDP) {
		this.sessionSDP = sessionSDP;
	}

}
