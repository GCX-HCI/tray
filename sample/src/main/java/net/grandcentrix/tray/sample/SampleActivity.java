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
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.functions.Func1;

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

    private OnTrayPreferenceChangeListener mAppPrefsListener
            = new OnTrayPreferenceChangeListener() {
        @Override
        public void onTrayPreferenceChanged(final Collection<TrayItem> items) {
            Log.d(TAG, "read in main process: changed " + getNiceString(items));
        }
    };

    private ImportTrayPreferences mImportPreference;

    private int mMultiProcessCounter = 0;

    private SharedPreferences mSharedPreferences;

    private final OnTrayPreferenceChangeListener mImportPrefsListener
            = new OnTrayPreferenceChangeListener() {
        @Override
        public void onTrayPreferenceChanged(final Collection<TrayItem> items) {
            Log.v(TAG, "trayPrefs changed items: " + getNiceString(items));
            updateSharedPrefInfo();
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener mSharedPrefsListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                final String key) {
            Object value = null;
            try {
                value = mSharedPreferences.getString(key, "");
            } catch (ClassCastException e) {
            }
            try {
                value = mSharedPreferences.getInt(key, 0);
            } catch (ClassCastException e) {
            }
            Log.d(TAG, "sharedPrefs changed key: '" + key + "' value '" + value + "'");
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
            case R.id.increase_multiprocess_counter:
                increaseMultiprocessCounter();
                break;
            case R.id.increase_multiprocess_counter_other_process:
                increaseMultiprocessCounterInOtherProcess();
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

        final Button multiprocessIncreaser = (Button) findViewById(
                R.id.increase_multiprocess_counter);
        multiprocessIncreaser.setOnClickListener(this);
        final Button multiprocessIncreaserOther = (Button) findViewById(
                R.id.increase_multiprocess_counter_other_process);
        multiprocessIncreaserOther.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSharedPrefInfo();

        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPrefsListener);
        mImportPreference.registerOnTrayPreferenceChangeListener(mImportPrefsListener);
        mAppPrefs.registerOnTrayPreferenceChangeListener(mAppPrefsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPrefsListener);
        mImportPreference.unregisterOnTrayPreferenceChangeListener(mImportPrefsListener);
        mAppPrefs.unregisterOnTrayPreferenceChangeListener(mAppPrefsListener);

    }

    private List<String> getNiceString(final Collection<TrayItem> items) {
        return Observable.from(items)
                .map(new Func1<TrayItem, String>() {
                    @Override
                    public String call(final TrayItem trayItem) {
                        return "key: '" + trayItem.key() + "' value '" + trayItem.value() + "'";
                    }
                })
                .toList().toBlocking().first();
    }

    private void importSharedPref() {
        final SharedPreferencesImport sharedPreferencesImport =
                new SharedPreferencesImport(this, SampleActivity.SHARED_PREF_NAME,
                        SampleActivity.SHARED_PREF_KEY, TRAY_PREF_KEY);
        mImportPreference.migrate(sharedPreferencesImport);
    }

    /**
     * write here read remote
     * <p>
     * check logcat to see the what happens
     */
    @SuppressLint("CommitPrefEdits")
    private void increaseMultiprocessCounter() {
        mMultiProcessCounter++;
        Log.d(TAG, "write in main process: counter = " + mMultiProcessCounter);
        mSharedPreferences.edit()
                .putInt(MultiProcessService.KEY_MULTIPROCESS_COUNTER_SERVICE_READ,
                        mMultiProcessCounter)
                .commit();
        mAppPrefs.put(MultiProcessService.KEY_MULTIPROCESS_COUNTER_SERVICE_READ,
                mMultiProcessCounter);

        // starting a service in another process to read the values there.
        MultiProcessService.read(this);

        // sample output
        // you can see the shared preferences sometimes don't get updated (tray: 15 sharedPrefs: 13)
        /*
        11-05 09:06:51.086 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: write in main process: counter = 13
        11-05 09:06:51.089 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: sharedPrefs changed key: 'multiprocess_counter_read' value '13'
        11-05 09:06:51.103 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_read' value '13']
        11-05 09:06:51.105 19901-22324/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: read in other process => tray: 13 sharedPrefs: 13
        11-05 09:06:51.303 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: write in main process: counter = 14
        11-05 09:06:51.308 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: sharedPrefs changed key: 'multiprocess_counter_read' value '14'
        11-05 09:06:51.320 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_read' value '14']
        11-05 09:06:51.331 19901-22330/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: read in other process => tray: 14 sharedPrefs: 13
        11-05 09:06:51.695 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: write in main process: counter = 15
        11-05 09:06:51.700 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: sharedPrefs changed key: 'multiprocess_counter_read' value '15'
        11-05 09:06:51.718 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_read' value '15']
        11-05 09:06:51.730 19901-22335/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: read in other process => tray: 15 sharedPrefs: 13
        11-05 09:06:52.104 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: write in main process: counter = 16
        11-05 09:06:52.111 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: sharedPrefs changed key: 'multiprocess_counter_read' value '16'
        11-05 09:06:52.124 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_read' value '16']
        11-05 09:06:52.154 19901-22344/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: read in other process => tray: 16 sharedPrefs: 16
        11-05 09:06:52.287 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: write in main process: counter = 17
        11-05 09:06:52.289 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: sharedPrefs changed key: 'multiprocess_counter_read' value '17'
        11-05 09:06:52.303 19901-22346/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: read in other process => tray: 17 sharedPrefs: 16
        11-05 09:06:52.307 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_read' value '17']
        */
    }

    /**
     * write remote, get notified here with the listeners
     * <p>
     * check logcat to see what happens
     */
    private void increaseMultiprocessCounterInOtherProcess() {
        // the listeners will react to those changes. At least the tray listener because the
        // listener of the shared preferences has no idea something has changed in the shared prefs
        // in another process
        MultiProcessService.write(this);

        // sample output
        // you can see the shared preferences in the main thread don't get notified
        /*
        11-05 09:04:11.302 19901-19914/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: write in other process: counter = 1
        11-05 09:04:11.372 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_write' value '1']
        11-05 09:04:12.375 19901-19932/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: write in other process: counter = 2
        11-05 09:04:12.379 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_write' value '2']
        11-05 09:04:13.909 19901-19957/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: write in other process: counter = 3
        11-05 09:04:13.914 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_write' value '3']
        11-05 09:04:14.792 19901-19966/net.grandcentrix.tray.sample:otherProcess D/MultiProcessService: write in other process: counter = 4
        11-05 09:04:14.801 19787-19787/net.grandcentrix.tray.sample D/SampleActivity: read in main process: changed [key: 'multiprocess_counter_write' value '4']
        */
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

        info.setText("SharedPref Data: " + sharedPrefData + "\n"
                + "Tray Data: " + trayData);
    }

    private void writeInSharedPref() {
        final String data = "SOM3 D4T4 " + (System.currentTimeMillis() % 100000);
        mSharedPreferences.edit()
                .putString(SHARED_PREF_KEY, data)
                .apply();
    }
}
