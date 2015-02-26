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

import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p/>
 * Access interface to interact with preferences.
 */
public interface PreferenceAccessor<T> {

    void clear();

    Collection<T> getAll();

    // TODO for version 1.1
    // boolean contains(String key);

    // TODO for version 1.1
    // List<String> keys();

    boolean getBoolean(String key, boolean defaultValue);

    float getFloat(String key, float defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    /**
     * Get a preference by its key
     *
     * @param key desired key
     * @return Returns the preference if found or null if it doesn't exist
     */
    @Nullable
    T getPref(String key);

    String getString(String key, String defaultValue);

    void put(String key, String value);

    void put(String key, int value);

    void put(String key, float value);

    void put(String key, long value);

    void put(String key, boolean value);

    void remove(String key);
}
