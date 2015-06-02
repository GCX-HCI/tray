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

package net.grandcentrix.tray.accessor;

import net.grandcentrix.tray.migration.Migration;
import net.grandcentrix.tray.storage.PreferenceStorage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collection;

/**
 * Base class that can be used to access and persist simple data to a {@link PreferenceStorage}.
 * The access to this storage defines the {@link PreferenceAccessor} interface.
 * <p>
 * Created by pascalwelsch on 11/20/14.
 */
public abstract class Preference<T> implements PreferenceAccessor<T> {

    private static final String TAG = Preference.class.getSimpleName();

    private PreferenceStorage<T> mStorage;

    public static boolean isDataTypeSupported(final Object data) {
        return data instanceof Integer
                || data instanceof String
                || data instanceof Long
                || data instanceof Float
                || data instanceof Boolean
                || data == null;
    }

    public Preference(final PreferenceStorage<T> storage, final int version) {
        mStorage = storage;

        changeVersion(version);
    }

    @Override
    public void clear() {
        mStorage.clear();
    }

    @Override
    public Collection<T> getAll() {
        return mStorage.getAll();
    }

    @Nullable
    @Override
    public T getPref(@NonNull final String key) {
        return mStorage.get(key);
    }

    public PreferenceStorage<T> getStorage() {
        return mStorage;
    }

    /**
     * Migrates data into this preference.
     *
     * @param migrations migrations will be migrated into this preference
     */
    @SafeVarargs
    public final void migrate(Migration<T>... migrations) {
        for (Migration<T> migration : migrations) {

            if (!migration.shouldMigrate()) {
                continue;
            }

            final Object data = migration.getData();

            final boolean supportedDataType = Preference.isDataTypeSupported(data);
            if (!supportedDataType) {
                Log.w(TAG, "could not migrate " + migration.getPreviousKey()
                        + " because the datatype" + data.getClass().getSimpleName() + "is invalid");
                migration.onPostMigrate(null);
                continue;
            }
            final String key = migration.getTrayKey();
            final String migrationKey = migration.getPreviousKey();
            // save into tray
            getStorage().put(key, migrationKey, data);

            // return the saved data.
            final T trayItem = getStorage().get(key);
            migration.onPostMigrate(trayItem);
        }
    }

    @Override
    public void put(@NonNull final String key, final String value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(@NonNull final String key, final int value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(@NonNull final String key, final float value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(@NonNull final String key, final long value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(@NonNull final String key, final boolean value) {
        getStorage().put(key, value);
    }

    public void remove(@NonNull final String key) {
        mStorage.remove(key);
    }

    /**
     * Changes the version of this preferences. checks for version changes and calls the correct
     * handling methods.
     * <pre>
     * <ul>
     * <li>{@link #onCreate(int)} when there is no previous version</li>
     * <li>{@link #onUpgrade(int, int)} for an increasing version</li>
     * <li>{@link #onDowngrade(int, int)} for a decreasing version</li>
     * </ul>
     * </pre>
     * compareable to the mechanism in  {@link android.database.sqlite.SQLiteOpenHelper#getWritableDatabase()}
     */
    // TODO PW is public useful?
    /*package*/
    synchronized void changeVersion(final int newVersion) {
        if (newVersion < 1) {
            // negative versions are illegal.
            // 0 is reserved to detect the initial state
            throw new IllegalArgumentException("Version must be >= 1, was " + newVersion);
        }

        final int version = getStorage().getVersion();
        if (version != newVersion) {
            if (version == 0) {
                onCreate(newVersion);
            } else {
                if (version > newVersion) {
                    onDowngrade(version, newVersion);
                } else {
                    onUpgrade(version, newVersion);
                }
            }
        }
        getStorage().setVersion(newVersion);
    }

    /**
     * Called when this Preference is created for the first time. This is where the initial
     * migration from other data source should happen.
     *
     * @param initialVersion the version set in the constructor, always &gt; 0
     */
    protected abstract void onCreate(final int initialVersion);

    /**
     * works inverse to the {@link #onUpgrade(int, int)} method
     *
     * @param oldVersion version before downgrade
     * @param newVersion version to downgrade to, always &gt; 0
     */
    protected void onDowngrade(final int oldVersion, final int newVersion) {
        throw new IllegalStateException("Can't downgrade from version " +
                oldVersion + " to " + newVersion);
    }

    /**
     * Called when the Preference needs to be upgraded. Use this to migrate data in this Preference
     * over time.
     * <p>
     * Once the version in the constructor is increased the next constructor call to this
     * Preference
     * will trigger an upgrade.
     *
     * @param oldVersion version before upgrade, always &gt; 0
     * @param newVersion version after upgrade
     */
    protected abstract void onUpgrade(final int oldVersion, final int newVersion);
}