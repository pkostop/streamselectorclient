package org.kemea.isafeco.client.utils;

import android.content.Context;
import android.widget.Toast;

public class Validator {
    public void validateStreamSelectorProperties(ApplicationProperties props, Context context) {
        if (isEmpty(props.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_ADDRESS))) {
            Toast.makeText(context, "Empty Stream Selector Address. Go to settings tab", Toast.LENGTH_LONG).show();
        }

        if (isEmpty(props.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_USERNAME))) {
            Toast.makeText(context, "Empty Stream Selector User name. Go to settings tab", Toast.LENGTH_LONG).show();
        }

        if (isEmpty(props.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_PASSWORD))) {
            Toast.makeText(context, "Empty Stream Selector Password. Go to settings tab", Toast.LENGTH_LONG).show();
        }

        if (isEmpty(props.getProperty(ApplicationProperties.PROP_STREAM_SELECTOR_API_KEY))) {
            Toast.makeText(context, "Empty Stream Selector Api Key. Go to settings tab", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmpty(String val) {
        return val == null || "".equalsIgnoreCase(val);
    }
}
