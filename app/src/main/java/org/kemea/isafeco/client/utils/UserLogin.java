package org.kemea.isafeco.client.utils;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import org.kemea.isafeco.client.streamselector.stubs.StreamSelectorClient;
import org.kemea.isafeco.client.streamselector.stubs.output.LoginOutput;

public class UserLogin {
    public void logUser(Context context, Activity activity) {
        LoginOutput loginOutput = getStreamSelectorUser(context);
        if (loginOutput == null) {
            ApplicationProperties applicationProperties = new ApplicationProperties(context.getFilesDir().getAbsolutePath());
            (new Validator()).validateStreamSelectorProperties(applicationProperties, context);
            StreamSelectorClient streamSelectorClient = new StreamSelectorClient(
                    applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS),
                    applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_API_KEY));
            String userName = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME);
            String password = applicationProperties.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD);
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginOutput loginOutput = null;
                    try {
                        loginOutput = streamSelectorClient.logUser(userName, password, deviceId);
                        if (loginOutput == null)
                            throw new RuntimeException("Login to StreamSelector returned  null. Login failed.");
                        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(LoginOutput.class.getName(), Util.toJson(loginOutput));
                        editor.apply();
                    } catch (Exception e) {
                        AppLogger.getLogger().e(Util.stacktrace(e));
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, String.format("Error: %s ", e.getMessage()), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();

        }
    }

    public LoginOutput getStreamSelectorUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        String value = prefs.getString(LoginOutput.class.getName(), null);
        LoginOutput loginOutput = null;
        if (value != null) {
            loginOutput = Util.fromJson(value, LoginOutput.class);
        }
        return loginOutput;
    }
}
