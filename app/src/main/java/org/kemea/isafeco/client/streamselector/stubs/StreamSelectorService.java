package org.kemea.isafeco.client.streamselector.stubs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import org.kemea.isafeco.client.net.StreamSelectorClient;
import org.kemea.isafeco.client.streamselector.stubs.output.GetSessionsOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionDestinationStreamOutput;
import org.kemea.isafeco.client.streamselector.stubs.output.SessionSourceStreamOutput;
import org.kemea.isafeco.client.utils.AppLogger;
import org.kemea.isafeco.client.utils.ApplicationProperties;
import org.kemea.isafeco.client.utils.Util;
import org.kemea.isafeco.client.utils.Validator;

public class StreamSelectorService {
    Context context;
    ApplicationProperties applicationProperties;
    LoginOutput loginOutput;
    StreamSelectorClient streamSelectorClient;

    public StreamSelectorService(Context context) {
        this.context = context;
        applicationProperties = new ApplicationProperties(context.getFilesDir().getAbsolutePath());
    }

    private void initStreamSelectorResources() {
        loginOutput = getStreamSelectorUser();
        streamSelectorClient = new StreamSelectorClient(getStreamSelectorAddress());
    }

    public SessionSourceStreamOutput postSessionsSessionSourceStreams(Long applicationId, String sessionSdp) throws Exception {
        initStreamSelectorResources();
        return streamSelectorClient.postSessionsSessionSourceStreams(applicationId, getUserName(), getUserPassword(), sessionSdp, loginOutput.getToken());
    }

    public SessionDestinationStreamOutput postSessionsSessionDestinationStreams(Long sessionId) throws Exception {
        initStreamSelectorResources();
        return streamSelectorClient.postSessionsSessionDestinationStreams(sessionId, loginOutput.getToken());
    }

    public GetSessionsOutput getSessions(Integer limit, Integer offset, Integer clusterId, Long sessionId, Long contractId, Long status) throws Exception {
        initStreamSelectorResources();
        return streamSelectorClient.getSessions(limit, offset, clusterId, sessionId, contractId, status, loginOutput.getToken());
    }

    private String getStreamSelectorAddress() {
        return applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
    }

    private String getUserName() {
        return applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
    }

    private String getUserPassword() {
        return applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
    }

    private LoginOutput getStreamSelectorUser() {
        LoginOutput loginOutput = getStreamSelectorUserFromSharedPrefs(context);
        if (loginOutput != null)
            return loginOutput;
        (new Validator()).validateStreamSelectorProperties(applicationProperties, context);
        String streamSelectorAddress = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS);
        StreamSelectorClient streamSelectorClient = new StreamSelectorClient(streamSelectorAddress);
        String userName = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
        String password = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            loginOutput = streamSelectorClient.logUser(userName, password, deviceId);
            if (loginOutput == null)
                throw new RuntimeException("Login to StreamSelector returned  null. Login failed.");
        } catch (Exception e) {
            AppLogger.getLogger().e(e);
            if (context instanceof Activity)
                Util.toast((Activity) context, String.format("Error: %s", e.getMessage()));
        }
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LoginOutput.class.getName(), Util.toJson(loginOutput));
        editor.apply();
        return loginOutput;
    }

    private LoginOutput getStreamSelectorUserFromSharedPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        String value = prefs.getString(LoginOutput.class.getName(), null);
        LoginOutput loginOutput = null;
        if (value != null) {
            loginOutput = Util.fromJson(value, LoginOutput.class);
        }
        return loginOutput;
    }

    public static void clearLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(LoginOutput.class.getName());
        editor.apply();
    }


}
