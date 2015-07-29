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

package net.grandcentrix.tray.migration;

import net.grandcentrix.tray.provider.TrayItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Objects;

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
    public void onPostMigrate(final TrayItem trayItem) {
        if (trayItem != null) {
            if (equals(trayItem.value(), getData())) {
                mPreferences.edit().remove(mSharedPrefsKey).apply();
            }
        }
    }

    @Override
    public boolean shouldMigrate() {
        if (mPreferences.contains(mSharedPrefsKey)) {
            return true;
        }

        Log.v(TAG, "SharedPreference with key '" + mSharedPrefsKey
                + "' not found. skipped import");
        return false;
    }

    /**
     * Null-safe equivalent of {@code a.equals(b)}. Taken from {@link Objects#equals(Object,
     * Object)} API level 19+
     */
    /*protected*/
    static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
}
