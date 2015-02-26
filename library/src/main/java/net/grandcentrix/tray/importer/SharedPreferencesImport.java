package net.grandcentrix.tray.importer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by pascalwelsch on 2/25/15.
 */
public class SharedPreferencesImport implements TrayImport {

    private static final String TAG = SharedPreferencesImport.class.getSimpleName();

    private final SharedPreferences mPreferences;

    private final String mSharedPrefsKey;

    private final String mTrayKey;

    public SharedPreferencesImport(final Context context, final String sharedPrefsName,
            final String sharedPrefsKey, final String trayKey) {
        mSharedPrefsKey = sharedPrefsKey;
        mTrayKey = trayKey;
        mPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_MULTI_PROCESS);
    }

    @Override
    public Object getImportData() {
        return mPreferences.getAll().get(mSharedPrefsKey);
    }

    @NonNull
    @Override
    public String getImportedKey() {
        return mSharedPrefsKey;
    }

    @NonNull
    @Override
    public String getTrayKey() {
        return mTrayKey;
    }

    @Override
    public void onPostImport() {
        mPreferences.edit().remove(mSharedPrefsKey).apply();
    }

    @Override
    public boolean onPreImport() {
        if (!mPreferences.contains(mSharedPrefsKey)) {
            Log.v(TAG, "SharedPreference with key '" + mSharedPrefsKey
                    + "' not found. skipped import");
            return true;
        }
        return false;
    }
}
