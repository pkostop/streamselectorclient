package org.kemea.isafeco.client.streamselector.stubs.input;

import com.google.gson.annotations.SerializedName;



public class SessionSourceStreamInput {
	@SerializedName(value = "application_id")
	Long applicationId;

	@SerializedName(value = "user_login")
	String userLogin;

	@SerializedName(value = "user_password")
	String userPassword;

	@SerializedName(value = "session_sdp")
	String sessionSdp;

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getSessionSdp() {
		return sessionSdp;
	}

	public void setSessionSdp(String sessionSdp) {
		this.sessionSdp = sessionSdp;
	}

}
