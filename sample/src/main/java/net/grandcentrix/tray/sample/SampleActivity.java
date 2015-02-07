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

import android.app.Activity;
import android.content.Intent;
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

    private TrayAppPreferences mAppPrefs;

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.reset:
                mAppPrefs.remove(STARTUP_COUNT);
                //restart activity
                final Intent intent = new Intent(this, SampleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
        int startupCount = mAppPrefs.getInt(STARTUP_COUNT, 0);

        if (savedInstanceState == null) {
            mAppPrefs.put(STARTUP_COUNT, ++startupCount);
        }

        final TextView text = (TextView) findViewById(android.R.id.text1);
        text.setText(getString(R.string.launched_x_times, startupCount));

        final Button resetBtn = (Button) findViewById(R.id.reset);
        resetBtn.setOnClickListener(this);
    }
}
