package net.grandcentrix.tray.sample;

import net.grandcentrix.tray.AppPreferences;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by pascalwelsch on 11/5/15.
 */
public class MultiProcessService extends IntentService {

    private static final String TAG = MultiProcessService.class.getSimpleName();

    public static final String KEY_MULTIPROCESS_COUNTER_SERVICE_READ = "multiprocess_counter_read";

    public static final String KEY_MULTIPROCESS_COUNTER_SERVICE_WRITE
            = "multiprocess_counter_write";

    private static final String INTENT_MODE = "mode";

    private static final String INTENT_MODE_READ = "read";

    private static final String INTENT_MODE_WRITE = "write";

    private static int mCount = 0;

    private SharedPreferences mSharedPreferences;

    private AppPreferences mTrayPreferences;

    /**
     * reads data from shared preferences and tray and prints the values. This happens in a
     * different process
     */
    public static void read(Context context) {
        Intent intent = new Intent(context, MultiProcessService.class);
        intent.putExtra(INTENT_MODE, INTENT_MODE_READ);
        context.startService(intent);
    }

    /**
     * increases the number in shared preferences and tray. This happens in a different process
     */
    public static void write(Context context) {
        Intent intent = new Intent(context, MultiProcessService.class);
        intent.putExtra(INTENT_MODE, INTENT_MODE_WRITE);
        context.startService(intent);
    }

    public MultiProcessService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTrayPreferences = new AppPreferences(this);
        mSharedPreferences = getSharedPreferences(SampleActivity.SHARED_PREF_NAME,
                Context.MODE_MULTI_PROCESS);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onHandleIntent(final Intent intent) {

        final String mode = intent.getStringExtra(INTENT_MODE);
        switch (mode) {
            case INTENT_MODE_READ: {
                final int trayCount = mTrayPreferences
                        .getInt(KEY_MULTIPROCESS_COUNTER_SERVICE_READ, 0);
                final int sharedPrefsCount = mSharedPreferences
                        .getInt(KEY_MULTIPROCESS_COUNTER_SERVICE_READ, 0);
                Log.d(TAG, "read in other process =>"
                        + " tray: " + trayCount
                        + " sharedPrefs: " + sharedPrefsCount);
            }
            break;
            case INTENT_MODE_WRITE: {
                mCount++;
                Log.d(TAG, "write in other process: counter = " + mCount);
                mTrayPreferences.put(KEY_MULTIPROCESS_COUNTER_SERVICE_WRITE, mCount);
                mSharedPreferences.edit()
                        .putInt(KEY_MULTIPROCESS_COUNTER_SERVICE_WRITE, mCount)
                        .commit();
            }
            break;
            default:
                throw new IllegalArgumentException("unknown mode");

        }

    }

}
