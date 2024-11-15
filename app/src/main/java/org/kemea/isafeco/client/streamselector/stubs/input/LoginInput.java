package org.kemea.isafeco.client.streamselector.stubs.input;

import com.google.gson.annotations.SerializedName;

public class LoginInput {
    String login;

    @SerializedName("password")
    String password;

    @SerializedName("application_type")
    String applicationType;

    @SerializedName("application_os")
    String applicationOs = "ANDROID";

    @SerializedName("application_device_id")
    String applicationDeviceId;

    @SerializedName("application_device_name")
    String applicationDeviceName;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationOs() {
        return applicationOs;
    }

    public void setApplicationOs(String applicationOs) {
        this.applicationOs = applicationOs;
    }

    public String getApplicationDeviceId() {
        return applicationDeviceId;
    }

    public void setApplicationDeviceId(String applicationDeviceId) {
        this.applicationDeviceId = applicationDeviceId;
    }

    public String getApplicationDeviceName() {
        return applicationDeviceName;
    }

    public void setApplicationDeviceName(String applicationDeviceName) {
        this.applicationDeviceName = applicationDeviceName;
    }
}
