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

import net.grandcentrix.tray.TrayAppPreferences;
import net.grandcentrix.tray.migration.SharedPreferencesImport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by pascalwelsch on 2/7/15.
 */
public class SampleActivity extends Activity implements View.OnClickListener {

    public static final String STARTUP_COUNT = "startup_count";

    private static final String SHARED_PREF_NAME = "shared_pref";

    private static final String SHARED_PREF_KEY = "shared_pref_key";

    private static final String TRAY_PREF_KEY = "importedData";

    private TrayAppPreferences mAppPrefs;

    private ImportTrayPreferences mImportPreference;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onStart() {
        super.onStart();
        updateSharedPrefInfo();
    }

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

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        mAppPrefs = new TrayAppPreferences(this);
        mImportPreference = new ImportTrayPreferences(this);
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        int startupCount = mAppPrefs.getInt(STARTUP_COUNT, 0);

        if (savedInstanceState == null) {
            mAppPrefs.put(STARTUP_COUNT, ++startupCount);
        }

        final TextView text = (TextView) findViewById(android.R.id.text1);
        text.setText(getString(R.string.launched_x_times, startupCount));

        final Button resetBtn = (Button) findViewById(R.id.reset);
        resetBtn.setOnClickListener(this);

        final Button writeSharedPref = (Button) findViewById(R.id.write_shared_pref);
        writeSharedPref.setOnClickListener(this);

        final Button importInTray = (Button) findViewById(R.id.import_shared_pref);
        importInTray.setOnClickListener(this);
    }

    private void importSharedPref() {
        final SharedPreferencesImport sharedPreferencesImport =
                new SharedPreferencesImport(this, SampleActivity.SHARED_PREF_NAME,
                        SampleActivity.SHARED_PREF_KEY, TRAY_PREF_KEY);
        mImportPreference.migrate(sharedPreferencesImport);
        updateSharedPrefInfo();
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
        updateSharedPrefInfo();
    }
}
