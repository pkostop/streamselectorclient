package org.kemea.isafeco.client.net;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import org.kemea.isafeco.client.streamselector.stubs.input.LoginInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionDestinationStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.input.SessionSourceStreamInput;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionDestinationStreamOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StreamSelectorClient {

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    String streamSelectorUrl;
    Context context;

    public StreamSelectorClient(String streamSelectorUrl) {
        this.streamSelectorUrl = streamSelectorUrl;
    }

    public SessionSourceStreamOutput postSessionsSessionSourceStreams(Long applicationId, String userLogin, String userPassword, String sessionSdp, String apiKey) throws Exception {
        SessionSourceStreamInput sessionSourceStreamInput = new SessionSourceStreamInput();
        sessionSourceStreamInput.setApplicationId(applicationId);
        sessionSourceStreamInput.setUserLogin(userLogin);
        sessionSourceStreamInput.setUserPassword(userPassword);
        sessionSourceStreamInput.setSessionSdp(sessionSdp);
        String json = Util.toJson(sessionSourceStreamInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/api/v2.0.0.0/sessions/session-source-streams"), json,
                postHeaders(apiKey), "UTF-8", 0, 0);

        return Util.fromJson(new String(payload),
                SessionSourceStreamOutput.class);
    }

    public LoginOutput logUser(String userName, String password, String deviceId) throws Exception {
        Log.d(TAG, ">>> About to logUser: " + userName);
        LoginInput loginInput = new LoginInput();
        loginInput.setLogin(userName);
        loginInput.setPassword(password);
        loginInput.setApplicationDeviceId(deviceId);
        loginInput.setApplicationType("COPAEUROPE");
        loginInput.setApplicationOs("ANDROID");
        loginInput.setApplicationDeviceName("any");
        String json = Util.toJson(loginInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/api/v2.0.0.0/users/login"), json,
                postHeaders(null), "UTF-8", 10000, 10000);
        return Util.fromJson(new String(payload),
                LoginOutput.class);
    }

    public SessionDestinationStreamOutput postSessionsSessionDestinationStreams(Long sessionId, String apiKey) throws Exception {
        SessionDestinationStreamInput sessionDestinationStreamInput = new SessionDestinationStreamInput();
        sessionDestinationStreamInput.setSessionId(sessionId);
        String json = Util.toJson(sessionDestinationStreamInput);
        byte[] payload = NetUtil.post(String.format("%s%s", streamSelectorUrl, "/api/v2.0.0.0/sessions/session-destination-streams"), json,
                postHeaders(apiKey), "UTF-8", 10000, 10000);
        return Util.fromJson(new String(payload),
                SessionDestinationStreamOutput.class);
    }

    public GetSessionsOutput getSessions(Integer limit, Integer offset, Integer clusterId, Long sessionId, Long contractId, Long status, String apiKey) throws Exception {
        StringBuffer queryString = new StringBuffer();
        appendGetParameter(queryString, "limit", String.valueOf(limit));
        appendGetParameter(queryString, "offset", String.valueOf(offset));
        appendGetParameter(queryString, "cluster_id", String.valueOf(clusterId));
        appendGetParameter(queryString, "session_id", String.valueOf(sessionId));
        appendGetParameter(queryString, "contract_id", String.valueOf(contractId));
        appendGetParameter(queryString, "status", String.valueOf(status));
        String url = String.format("%s%s%s", streamSelectorUrl, "/api/v2.0.0.0/sessions", queryString.toString());
        byte[] payload = NetUtil.get(url, getHeaders(apiKey), CONNECT_TIMEOUT, READ_TIMEOUT);
        return Util.fromJson(new String(payload),
                GetSessionsOutput.class);
    }


    public void postStopSessionByID(Long sessionId, String apiKey) throws Exception {
        String url = String.format("%s%s/%s/stop", streamSelectorUrl, "/api/v2.0.0.0/sessions", String.valueOf(sessionId));
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
        if (apiKey != null)
            headers.put("Authorization", String.format("JWT %s", apiKey));
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }

    private Map<String, String> getHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        if (apiKey != null)
            headers.put("Authorization", String.format("JWT %s", apiKey));
        headers.put("Accept", "application/json");
        return headers;
    }
}