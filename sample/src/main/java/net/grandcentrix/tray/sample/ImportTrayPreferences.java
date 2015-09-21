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

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.SharedPreferencesImport;
import net.grandcentrix.tray.core.TrayItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pascalwelsch on 3/14/15.
 */
public class ImportTrayPreferences extends TrayPreferences {

    public static final String KEY_USER_TOKEN = "user_token";

    public static final String KEY_GCM_TOKEN = "gcm_token";

    private static final String TAG = ImportTrayPreferences.class.getSimpleName();

    public ImportTrayPreferences(final Context context) {
        super(context, "imported", 1);
    }

    @Override
    protected void onCreate(final int initialVersion) {
        super.onCreate(initialVersion);

        // onCreate is only called at the very first creation of this Module

        importSharedPreferencesWithLogging();
    }

    /**
     * example how to import shared preferences
     */
    private void importSharedPreferences() {
        // migrate sharedPreferences in here.
        final SharedPreferencesImport userTokenMigration = new SharedPreferencesImport(getContext(),
                SampleActivity.SHARED_PREF_NAME, "userToken", KEY_USER_TOKEN);
        migrate(userTokenMigration);

        final SharedPreferencesImport gcmTokenMigration = new SharedPreferencesImport(getContext(),
                SampleActivity.SHARED_PREF_NAME, "gcmToken", KEY_GCM_TOKEN);
        migrate(gcmTokenMigration);
    }

    /**
     * logging wrapper for:
     * example how to import shared preferences
     */
    private void importSharedPreferencesWithLogging() {
        final SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(SampleActivity.SHARED_PREF_NAME,
                        Context.MODE_MULTI_PROCESS);

        final HashMap<String, ?> allBefore = new HashMap<>(sharedPreferences.getAll());
        Log.v(TAG, allBefore.size() + " items in sharedPreferences: " + allBefore.toString());
        // 2 items in sharedPreferences: {userToken=cf26535a-6949-4728-b595-c6d80c094eff, gcmToken=2ca7e9a0-9114-4d55-8d2d-870b8d49fafe}
        importSharedPreferences();

        final ArrayList<TrayItem> all = new ArrayList<>(getAll());

        Log.v(TAG, "imported " + all.size() + " items: " + all.toString());
        // imported 2 items: [
        // {key: gcm:token, value: 2ca7e9a0-9114-4d55-8d2d-870b8d49fafe, module: imported, created: 14:06:04 05.06.2015, updated: 14:06:04 05.06.2015, migratedKey: gcmToken},
        // {key: user:token, value: cf26535a-6949-4728-b595-c6d80c094eff, module: imported, created: 14:06:04 05.06.2015, updated: 14:06:04 05.06.2015, migratedKey: userToken}
        // ]

        final HashMap<String, ?> allAfter = new HashMap<>(sharedPreferences.getAll());
        Log.v(TAG, allAfter.size() + " items in sharedPreferences: " + allAfter.toString());
        // 0 items in sharedPreferences: {}
    }
}
