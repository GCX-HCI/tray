package net.grandcentrix.tray.core;

import android.util.Log;

/**
 * Logging helper class for Tray inspired by Volley
 * <p>
 * start logging with: {@code adb shell setprop log.tag.Tray VERBOSE}
 * <p>
 * disable logging: {@code adb shell setprop log.tag.Tray SUPPRESS}
 * <p>
 * Created by pascalwelsch on 9/23/15.
 */
public class TrayLog {

    private static String TAG = "Tray";

    public static boolean DEBUG = Log.isLoggable(TAG, Log.VERBOSE);

    public static void d(String s) {
        if (s == null) {
            s = "";
        }
        Log.d(TAG, s);
    }

    public static void e(String s) {
        if (s == null) {
            s = "";
        }
        Log.e(TAG, s);
    }

    public static void e(Throwable tr, String s) {
        Log.e(TAG, s, tr);
    }

    /**
     * Customize the log tag for your application, so that other apps
     * using Tray don't mix their logs with yours.
     * <p>
     * Enable the log property for your tag before starting your app:
     * <p>
     * {@code adb shell setprop log.tag.&lt;tag&gt;}
     *
     * @param tag new tag will be used for logging
     */
    public static void setTag(String tag) {
        d("Changing log tag to " + tag);
        TAG = tag;

        // Reinitialize the DEBUG "constant"
        DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
    }

    public static void v(String s) {
        if (DEBUG) {
            if (s == null) {
                s = "";
            }
            Log.v(TAG, s);
        }
    }

    public static void w(String s) {
        if (s == null) {
            s = "";
        }
        Log.w(TAG, s);
    }

    public static void wtf(Throwable tr, String s) {
        Log.wtf(TAG, s, tr);
    }

    public static void wtf(String s) {
        if (s == null) {
            s = "";
        }
        Log.wtf(TAG, s);
    }

    TrayLog() {
        throw new IllegalStateException("no instances");
    }
}
