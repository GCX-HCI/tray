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

package net.grandcentrix.tray.mock;

import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayException;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayStorage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class MockTrayStorage extends TrayStorage {

    public ArrayList<OnTrayPreferenceChangeListener> mListeners = new ArrayList<>();

    private HashMap<String, TrayItem> mData = new HashMap<>();

    private int mVersion = 0;

    public MockTrayStorage(final String module) {
        super(module, Type.USER);
    }

    @Override
    public void annex(final TrayStorage oldStorage) {
        for (final TrayItem trayItem : oldStorage.getAll()) {
            mData.put(trayItem.key(), trayItem);
        }
        oldStorage.wipe();
    }

    @Override
    public boolean clear() {
        mData.clear();
        return true;
    }

    @Override
    public TrayItem get(@NonNull final String key) {
        return mData.get(key);
    }

    @NonNull
    @Override
    public Collection<TrayItem> getAll() {
        return mData.values();
    }

    @Override
    public int getVersion() throws TrayException {
        return mVersion;
    }

    @Override
    public boolean put(final TrayItem item) {
        return put(item.key(), item.value());
    }

    @Override
    public boolean put(@NonNull final String key, @Nullable final String migrationKey,
            final Object data) {
        final TrayItem saved = this.mData.get(key);
        final String value = String.valueOf(data);
        final Date now = new Date();
        final TrayItem item;
        if (saved == null) {
            item = new TrayItem(getModuleName(), key, migrationKey, value, now, now);
        } else {
            final Date created = saved.created();
            item = new TrayItem(getModuleName(), key, migrationKey, value, created, now);
        }
        this.mData.put(key, item);
        return true;
    }

    @Override
    public boolean put(@NonNull final String key, final Object data) {
        return put(key, null, data);
    }

    @Override
    public void registerOnTrayPreferenceChangeListener(
            @NonNull final OnTrayPreferenceChangeListener listener) {
        mListeners.add(listener);
    }

    @Override
    public boolean remove(@NonNull final String key) {
        return mData.remove(key) != null;
    }

    @Override
    public boolean setVersion(final int version) {
        this.mVersion = version;
        return true;
    }

    @Override
    public void unregisterOnTrayPreferenceChangeListener(
            @NonNull final OnTrayPreferenceChangeListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public boolean wipe() {
        mData.clear();
        mVersion = 0;
        return true;
    }
}
