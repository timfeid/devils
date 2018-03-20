package com.timfeid.devils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Tim on 2/10/2018.
 * Get values from config file
 */

public class Config {
    private static final String TAG = "Config";

    private static Properties properties = new Properties();

    public static void init(Context context) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            properties.load(rawResource);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }
    }

    public static String getValue(String name) {
        return properties.getProperty(name);
    }

}
