package org.kemea.isafeco.client.streamselector.stubs.output;

import com.google.gson.annotations.SerializedName;



public class SessionDestinationStreamOutput {

	@SerializedName(value = "session_decryption_key")
	private String sessionDecryptionKey;

	@SerializedName(value = "session_destination_service_ip")
	private String sessionDestinationServiceIp;

	@SerializedName(value = "session_destination_service_port")
	private int sessionDestinationServicePort;

	@SerializedName(value = "session_destination_service_protocol")
	private String sessionDestinationServiceProtocol;

	@SerializedName(value = "session_sdp")
	private String sessionSdp;

	// Getters and Setters
	public String getSessionDecryptionKey() {
		return sessionDecryptionKey;
	}

	public void setSessionDecryptionKey(String sessionDecryptionKey) {
		this.sessionDecryptionKey = sessionDecryptionKey;
	}

	public String getSessionDestinationServiceIp() {
		return sessionDestinationServiceIp;
	}

	public void setSessionDestinationServiceIp(String sessionDestinationServiceIp) {
		this.sessionDestinationServiceIp = sessionDestinationServiceIp;
	}

	public int getSessionDestinationServicePort() {
		return sessionDestinationServicePort;
	}

	public void setSessionDestinationServicePort(int sessionDestinationServicePort) {
		this.sessionDestinationServicePort = sessionDestinationServicePort;
	}

	public String getSessionDestinationServiceProtocol() {
		return sessionDestinationServiceProtocol;
	}

	public void setSessionDestinationServiceProtocol(String sessionDestinationServiceProtocol) {
		this.sessionDestinationServiceProtocol = sessionDestinationServiceProtocol;
	}

	public String getSessionSdp() {
		return sessionSdp;
	}

	public void setSessionSdp(String sessionSdp) {
		this.sessionSdp = sessionSdp;
	}

}
