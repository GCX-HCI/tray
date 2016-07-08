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

package net.grandcentrix.tray.core;

import net.grandcentrix.tray.TrayPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Migrates a key value pair from the {@link SharedPreferences} into a {@link
 * TrayPreferences}. There is no Migration which imports all data from {@link SharedPreferences}
 * into tray because devs using tray should be aware of what gets imported. It's your chance to get
 * rid off all the long forgotten junk stored in your {@link SharedPreferences} ;)
 * <p>
 * <b>Caution!</b> the key value pair imported into tray will be deleted in the {@link
 * SharedPreferences} after the import.
 * <p>
 * Created by pascalwelsch on 2/25/15.
 */
public class SharedPreferencesImport implements TrayMigration {

    private final SharedPreferences mPreferences;

    private final String mSharedPrefsKey;

    private final String mSharedPrefsName;

    private final String mTrayKey;

    public SharedPreferencesImport(final Context context, @NonNull final String sharedPrefsName,
            @NonNull final String sharedPrefsKey, @NonNull final String trayKey) {
        mSharedPrefsKey = sharedPrefsKey;
        mSharedPrefsName = sharedPrefsName;
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
        if (trayItem == null) {
            TrayLog.wtf("migration " + this + " failed, saved data in tray is null");
            return;
        }
        if (equals(trayItem.value(), getData().toString())) {
            TrayLog.v("removing key '" + mSharedPrefsKey + "' from SharedPreferences '"
                    + mSharedPrefsName + "'");
            mPreferences.edit().remove(mSharedPrefsKey).apply();
        }
    }

    @Override
    public boolean shouldMigrate() {
        if (mPreferences.contains(mSharedPrefsKey)) {
            return true;
        }

        TrayLog.v("key '" + mSharedPrefsKey + "' in SharedPreferences '"
                + mSharedPrefsName + "' not found. skipped import");
        return false;
    }

    @Override
    public String toString() {
        return "SharedPreferencesImport(@" + Integer.toHexString(hashCode()) + "){" +
                "sharedPrefsName='" + mSharedPrefsName + '\'' +
                ", sharedPrefsKey='" + mSharedPrefsKey + '\'' +
                ", trayKey='" + mTrayKey + '\'' +
                '}';
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
