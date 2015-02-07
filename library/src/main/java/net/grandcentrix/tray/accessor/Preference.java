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

import net.grandcentrix.tray.storage.PreferenceStorage;

import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/20/14.
 *
 * A Preference has a interface to interact and a storage to save the data.
 */
public abstract class Preference<T> implements PreferenceAccessor<T> {

    private PreferenceStorage<T> mStorage;

    public Preference(final PreferenceStorage<T> storage) {
        mStorage = storage;
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
    public T getPref(final String key) {
        return mStorage.get(key);
    }

    public PreferenceStorage<T> getStorage() {
        return mStorage;
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

}
