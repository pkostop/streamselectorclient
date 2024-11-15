package org.kemea.isafeco.client.streamselector.stubs;

import org.kemea.isafeco.client.net.NetUtil;
import org.kemea.isafeco.client.streamselector.stubs.input.LoginInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionSourceStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.Util;

import java.util.Collections;

public class StreamSelectorClient {
    public StreamSelectorClient(String streamSelectorUrl) {
        this.streamSelectorUrl = streamSelectorUrl;
    }

    String streamSelectorUrl;

    public SessionSourceStreamOutput postSessionsSessionSourceStreams(Long applicationId, String userLogin, String userPassword, String sessionSdp) throws Exception {
        SessionSourceStreamInput sessionSourceStreamInput = new SessionSourceStreamInput();
        sessionSourceStreamInput.setApplicationId(applicationId);
        sessionSourceStreamInput.setUserLogin(userLogin);
        sessionSourceStreamInput.setUserPassword(userPassword);
        sessionSourceStreamInput.setSessionSdp(sessionSdp);
        String json = Util.toJson(sessionSourceStreamInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/sessions/session-source-streams"), json,
                Collections.singletonMap("Content-Type", "application/json"), "UTF-8", 0, 0);

        return Util.fromJson(new String(payload),
                SessionSourceStreamOutput.class);
    }

    public LoginOutput logUser(String userName, String password, String deviceId) throws Exception {
        LoginInput loginInput = new LoginInput();
        loginInput.setLogin(userName);
        loginInput.setPassword(password);
        loginInput.setApplicationDeviceId(deviceId);
        String json = Util.toJson(loginInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/users/login"), json,
                Collections.singletonMap("Content-Type", "application/json"), "UTF-8", 0, 0);
        return Util.fromJson(new String(payload),
                LoginOutput.class);

    }
}
