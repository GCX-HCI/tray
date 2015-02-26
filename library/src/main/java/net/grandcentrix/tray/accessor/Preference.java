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

import net.grandcentrix.tray.importer.TrayImport;
import net.grandcentrix.tray.storage.PreferenceStorage;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p/>
 * A Preference has a interface to interact and a storage to save the data.
 */
public abstract class Preference<T> implements PreferenceAccessor<T> {

    private PreferenceStorage<T> mStorage;

    public Preference(final PreferenceStorage<T> storage, final int version) {
        mStorage = storage;

        if (version < 1) {
            throw new IllegalArgumentException("Version must be >= 1, was " + version);
        }

        synchronized (this) {
            detectVersionChange(version);
        }
    }

    protected List<TrayImport> getImports(final int version) {
        return new ArrayList<>();
    }

    /**
     * Called when this Preference is created for the first time. This is where the initial
     * migration from other data source should happen.
     */
    protected abstract void onCreate();

    /**
     * works inverse to the {@see #onUpgrade} method
     * @param oldVersion
     * @param newVersion
     */
    protected void onDowngrade(final int oldVersion, final int newVersion) {
        throw new IllegalStateException("downgrade isn't implemented");
    }

    /**
     * Called when the Preference needs to be upgraded. Use this to migrate data in this Preference
     * over time.
     * <p/>
     * Once the version in the constructor is increased the next constructor call to this Preference
     * will trigger an upgrade.
     */
    protected abstract void onUpgrade(final int oldVersion, final int newVersion);

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
    public T getPref(final String key) {
        return mStorage.get(key);
    }

    public PreferenceStorage<T> getStorage() {
        return mStorage;
    }

    public static boolean isDataTypeSupported(final Object data) {
        return data instanceof Integer
                || data instanceof String
                || data instanceof Long
                || data instanceof Float
                || data instanceof Boolean
                || data == null;
    }

    @Override
    public void put(final String key, final String value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(final String key, final int value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(final String key, final float value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(final String key, final long value) {
        getStorage().put(key, value);
    }

    @Override
    public void put(final String key, final boolean value) {
        getStorage().put(key, value);
    }

    @Override
    public void remove(final String key) {
        mStorage.remove(key);
    }

    private void detectVersionChange(final int newVersion) {
        final int version = getStorage().getVersion();
        if (version != newVersion) {
            if (version == 0) {
                onCreate();
            } else {
                if (version > newVersion) {
                    onDowngrade(version, newVersion);
                } else {
                    onUpgrade(version, newVersion);
                }
            }
        }
    }
}