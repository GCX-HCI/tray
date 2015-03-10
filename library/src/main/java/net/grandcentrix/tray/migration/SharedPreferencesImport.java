package net.grandcentrix.tray.migration;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by pascalwelsch on 2/25/15.
 */
public class SharedPreferencesImport implements TrayMigration {

    private static final String TAG = SharedPreferencesImport.class.getSimpleName();

    private final SharedPreferences mPreferences;

    private final String mSharedPrefsKey;

    private final String mTrayKey;

    public SharedPreferencesImport(final Context context, @NonNull final String sharedPrefsName,
            @NonNull final String sharedPrefsKey, @NonNull final String trayKey) {
        mSharedPrefsKey = sharedPrefsKey;
        mTrayKey = trayKey;
        mPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_MULTI_PROCESS);
    }

    @Override
    public Object getData() {
        return mPreferences.getAll().get(mSharedPrefsKey);
    }

    @NonNull
    @Override
    public String getPreviousKey() {
        return mSharedPrefsKey;
    }

    @NonNull
    @Override
    public String getTrayKey() {
        return mTrayKey;
    }

    @Override
    public void onPostMigrate(final boolean successful) {
        if (successful) {
            mPreferences.edit().remove(mSharedPrefsKey).commit();
        }
    }

    @Override
    public boolean shouldMigrate() {
        if (mPreferences.contains(mSharedPrefsKey)) {
            Log.v(TAG, "SharedPreference with key '" + mSharedPrefsKey
                    + "' not found. skipped import");
            return true;
        }
        return false;
    }
}
