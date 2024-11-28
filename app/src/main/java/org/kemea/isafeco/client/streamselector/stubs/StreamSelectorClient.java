package org.kemea.isafeco.client.streamselector.stubs;

import org.kemea.isafeco.client.net.NetUtil;
import org.kemea.isafeco.client.streamselector.stubs.input.LoginInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionDestinationStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionSourceStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionDestinationStreamOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StreamSelectorClient {

    String streamSelectorUrl;
    String apiKey;

    public StreamSelectorClient(String streamSelectorUrl, String apiKey) {
        this.streamSelectorUrl = streamSelectorUrl;
        this.apiKey = apiKey;
    }

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
                postHeaders(apiKey), "UTF-8", 10000, 10000);
        return Util.fromJson(new String(payload),
                LoginOutput.class);
    }

    public SessionDestinationStreamOutput postSessionsSessionDestinationStreams(Integer sessionId) throws Exception {
        SessionDestinationStreamInput sessionDestinationStreamInput = new SessionDestinationStreamInput();
        sessionDestinationStreamInput.setSessionId(sessionId);
        String json = Util.toJson(sessionDestinationStreamInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/sessions/session-destination-streams"), json,
                postHeaders(apiKey), "UTF-8", 10000, 10000);
        return Util.fromJson(new String(payload),
                SessionDestinationStreamOutput.class);
    }

    public GetSessionsOutput getSessions(Integer limit, Integer offset, Integer clusterId, Long sessionId, Long contractId, Long status) throws Exception {
        StringBuffer queryString = new StringBuffer();
        /*appendGetParameter(queryString, "limit", String.valueOf(limit));
        appendGetParameter(queryString, "offset", String.valueOf(offset));
        appendGetParameter(queryString, "cluster_id", String.valueOf(clusterId));
        appendGetParameter(queryString, "session_id", String.valueOf(sessionId));
        appendGetParameter(queryString, "contract_id", String.valueOf(contractId));
        appendGetParameter(queryString, "status", String.valueOf(status));*/
        String url = String.format("%s%s%s", streamSelectorUrl, "/sessions", queryString.toString());
        byte[] payload = NetUtil.get(url, getHeaders(apiKey), 10000, 10000);
        return Util.fromJson(new String(payload),
                GetSessionsOutput.class);
    }

    public void postStopSessionByID(Long sessionId) throws Exception {
        String url = String.format("%s%s/%s/stop", streamSelectorUrl, "/sessions", String.valueOf(sessionId));
        byte[] payload = NetUtil.get(url, getHeaders(apiKey), 10000, 10000);
    }

    private void appendGetParameter(StringBuffer paramClause, String paramName, String paramValue) {
        if (paramClause == null || Util.isEmpty(paramName) || Util.isEmpty(paramValue))
            return;
        if (paramClause.length() == 0)
            paramClause.append("?");
        else
            paramClause.append("&");
        paramClause.append(paramName).append("=").append(paramValue);
    }

    private Map<String, String> postHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiKey);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }

    private Map<String, String> getHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiKey);
        headers.put("Accept", "application/json");
        return headers;
    }
}
