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

import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.storage.ModularizedStorage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class MockModularizedStorage extends ModularizedStorage<TrayItem> {

    private HashMap<String, TrayItem> mData = new HashMap<>();

    private int mVersion = 0;

    public MockModularizedStorage(final String module) {
        super(module);
    }

    @Override
    public void annex(final ModularizedStorage<TrayItem> oldStorage) {
        for (final TrayItem trayItem : oldStorage.getAll()) {
            mData.put(trayItem.key(), trayItem);
        }
        oldStorage.wipe();
    }

    @Override
    public void clear() {
        mData.clear();
    }

    @Override
    public void wipe() {
        mData.clear();
        mVersion = 0;
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
    public int getVersion() {
        return mVersion;
    }

    @Override
    public void put(final TrayItem item) {
        put(item.key(), item.value());
    }

    @Override
    public void put(@NonNull final String key, @Nullable final String migrationKey,
            final Object data) {
        final String value = String.valueOf(data);
        final TrayItem item = new TrayItem(getModuleName(), key, migrationKey, value, new Date(),
                new Date());
        this.mData.put(key, item);
    }

    @Override
    public void put(@NonNull final String key, final Object data) {
        put(key, null, data);
    }

    @Override
    public void remove(@NonNull final String key) {
        mData.remove(key);
    }

    @Override
    public void setVersion(final int version) {
        this.mVersion = version;
    }
}
