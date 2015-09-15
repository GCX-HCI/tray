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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * basic functionality for every storage implementation
 * <p>
 * Created by pascalwelsch on 11/20/14.
 */
public interface PreferenceStorage<T> {

    /**
     * clears the storage by deleting all of its content. But doesn't clear metadata like the
     * version. to do so, use {@link #wipe()}
     *
     * @see #wipe()
     * @see #getVersion()
     */
    void clear();

    /**
     * @param key mapping key for the stored object
     * @return the corresponding Item object {@link T} for the given key
     */
    @Nullable
    T get(@NonNull final String key);

    /**
     * @return all items saved in this storage
     */
    @NonNull
    Collection<T> getAll();

    /**
     * @return the current version of this storage
     * @see #setVersion(int)
     */
    int getVersion();

    /**
     * stores a data item.
     *
     * @param item data object
     */
    void put(T item);

    /**
     * same as {@link #put(String, Object)} but with an additional migration key to save where the
     * data came from.
     *
     * @param key          where to save
     * @param migrationKey where the data came from
     * @param data         what to save
     */
    void put(@NonNull final String key, @Nullable final String migrationKey,
            @Nullable final Object data);

    /**
     * stores the data using the key to access the data later with {@link #get(String)}
     *
     * @param key  access key to the data
     * @param data what to save
     */
    void put(@NonNull final String key, @Nullable final Object data);

    /**
     * removes the item with the given key
     *
     * @param key mapping key for the stored object
     */
    void remove(@NonNull final String key);

    /**
     * sets the version of this storage
     *
     * @param version should be &gt; 0
     */
    void setVersion(final int version);

    /**
     * deleted this storage like it has never existed. removed saved data and all possible meta
     * data
     *
     * @see #clear()
     */
    void wipe();

}
