/*
 * Copyright (C) 2015 grandcentrix GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.tray.sample;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.SharedPreferencesImport;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayStorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by pascalwelsch on 2/7/15.
 */
public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String STARTUP_COUNT = "startup_count";

    public static final String SHARED_PREF_NAME = "shared_pref";

    private static final String SHARED_PREF_KEY = "shared_pref_key";

    private static final String TRAY_PREF_KEY = "importedData";

    private static final String TAG = SampleActivity.class.getSimpleName();

    private AppPreferences mAppPrefs;

    private ImportTrayPreferences mImportPreference;

    private SharedPreferences mSharedPreferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener mSharedPrefsListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                final String key) {
            Log.v(TAG, "sharedPrefs changed key: " + key);
            updateSharedPrefInfo();
        }
    };

    private final OnTrayPreferenceChangeListener mTrayPrefsListener
            = new OnTrayPreferenceChangeListener() {
        @Override
        public void onTrayPreferenceChanged(final Collection<TrayItem> items) {
            Log.v(TAG, "trayPrefs changed items: " + items);
            updateSharedPrefInfo();
        }
    };

    @SuppressWarnings("Annotator")
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.reset:
                resetAndRestart();
                break;
            case R.id.write_shared_pref:
                writeInSharedPref();
                break;
            case R.id.import_shared_pref:
                importSharedPref();
                break;
            default:
                Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        mAppPrefs = new AppPreferences(this);
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        int startupCount = mAppPrefs.getInt(STARTUP_COUNT, 0);

        if (startupCount == 0) {
            // save some "old" preferences which get migrated into the ImportTrayPreferences.
            // this works only the very first time ImportTrayPreferences gets created. You need to
            // wipe (clean is not enough) ImportTrayPreferences or delete the app data to retrigger
            // the call to ImportTrayPreferences#onCreate()
            mSharedPreferences.edit()
                    .putString("userToken", UUID.randomUUID().toString())
                    .putString("gcmToken", UUID.randomUUID().toString())
                    .commit();
        }

        if (savedInstanceState == null) {
            mAppPrefs.put(STARTUP_COUNT, ++startupCount);
        }

        testAutoBackup();

        mImportPreference = new ImportTrayPreferences(this);

        final TextView text = (TextView) findViewById(android.R.id.text1);
        text.setText(getString(R.string.sample_launched_x_times, startupCount));

        final Button resetBtn = (Button) findViewById(R.id.reset);
        resetBtn.setOnClickListener(this);

        final Button writeSharedPref = (Button) findViewById(R.id.write_shared_pref);
        writeSharedPref.setOnClickListener(this);

        final Button importInTray = (Button) findViewById(R.id.import_shared_pref);
        importInTray.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSharedPrefInfo();

        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPrefsListener);
        mImportPreference.registerOnTrayPreferenceChangeListener(mTrayPrefsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPrefsListener);
        mImportPreference.unregisterOnTrayPreferenceChangeListener(mTrayPrefsListener);
    }

    private void importSharedPref() {
        final SharedPreferencesImport sharedPreferencesImport =
                new SharedPreferencesImport(this, SampleActivity.SHARED_PREF_NAME,
                        SampleActivity.SHARED_PREF_KEY, TRAY_PREF_KEY);
        mImportPreference.migrate(sharedPreferencesImport);
    }

    /**
     * resets the startup count and restarts the activity
     */
    private void resetAndRestart() {
        mAppPrefs.remove(STARTUP_COUNT);
        //restart activity
        final Intent intent = new Intent(this, SampleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void testAutoBackup() {

        {
            // device specific data
            final TrayPreferences deviceSpecificPref =
                    new TrayPreferences(this, "nobackup", 1, TrayStorage.Type.DEVICE);
            final String deviceId = deviceSpecificPref.getString("deviceId", null);
            Log.v(TAG, "deviceId: " + deviceId);
            if (deviceId == null) {
                final String uuid = UUID.randomUUID().toString();
                deviceSpecificPref.put("deviceId", uuid);
                Log.v(TAG, "no deviceId, created: " + uuid);
            }
        }

        {
            // user specific data
            final TrayPreferences userSpecificPref =
                    new TrayPreferences(this, "autobackup", 1, TrayStorage.Type.USER);
            final String userId = userSpecificPref.getString("userId", null);
            Log.v(TAG, "userId: " + userId);
            if (userId == null) {
                final String uuid = UUID.randomUUID().toString();
                userSpecificPref.put("userId", uuid);
                Log.v(TAG, "no userId, created: " + uuid);
            }
        }
    }

    private void updateSharedPrefInfo() {
        final TextView info = (TextView) findViewById(R.id.shared_pref_info);
        final String sharedPrefData = mSharedPreferences.getString(SHARED_PREF_KEY, "null");
        final String trayData = mImportPreference.getString(TRAY_PREF_KEY, "null");

        info.setText(
                "SharedPref Data: " + sharedPrefData + "\n"
                        + "Tray Data: " + trayData);
    }

    private void writeInSharedPref() {
        final String data = "SOM3 D4T4 " + (System.currentTimeMillis() % 100000);
        mSharedPreferences.edit()
                .putString(SHARED_PREF_KEY, data)
                .apply();
    }
}
