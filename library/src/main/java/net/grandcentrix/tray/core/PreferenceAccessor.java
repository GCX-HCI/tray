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
 * Access interface to interact with preferences.
 * <p>
 * Created by pascalwelsch on 11/20/14.
 */
public interface PreferenceAccessor<T> {

    /**
     * clears all data in this preference. Access with {@code get} methods will return {@link
     * ItemNotFoundException} or the {@code defaultValue}
     *
     * @return true when successful, false otherwise
     */
    boolean clear();

    /**
     * checks if the preference has a value stored for the given key
     *
     * @param key the key to map the value
     * @return true when a value is stored for the key
     */
    boolean contains(final String key);

    /**
     * @return all data stored in the preference
     */
    Collection<T> getAll();

    /**
     * returns true if <code>true</code> or String "true" is saved. All other values will be parsed
     * as <code>false</code>
     * <p>
     * Is able to parse everything, so {@link WrongTypeException} is not used here
     *
     * @param key the key to map the value
     * @return value saved for the given key
     * @throws ItemNotFoundException if data could not be mapped to the param key
     * @see #getBoolean(String, boolean)
     */
    boolean getBoolean(@NonNull final String key) throws ItemNotFoundException;

    // TODO for version 1.1
    // boolean contains(String key);

    // TODO for version 1.1
    // List<String> keys();

    // TODO for version 1.1
    // int getSize();

    /**
     * returns true if <code>true</code> or String "true" is saved. All other values will be parsed
     * as <code>false</code>. If no entry for the key is found the param defaultValue is used.
     *
     * @param key          the key to map the value
     * @param defaultValue used if no entry is found for the key
     * @return the found value or the param defaultValue
     * @see #getBoolean(String)
     */
    boolean getBoolean(@NonNull final String key, final boolean defaultValue);

    /**
     * @param key the key to map the value
     * @return float value for param key
     * @throws ItemNotFoundException if data could not be mapped to the param key
     * @throws WrongTypeException    data was saved with a different format and could not be parsed
     *                               to {@link Float}
     */
    float getFloat(@NonNull final String key) throws ItemNotFoundException, WrongTypeException;

    /**
     * @param key          the key to map the value
     * @param defaultValue if no data is stored for param key
     * @return float value for param key, or the param defaultValue
     * @throws WrongTypeException data was saved with a different format and could not be parsed to
     *                            {@link Float}
     */
    float getFloat(@NonNull final String key, final float defaultValue) throws WrongTypeException;

    /**
     * @param key the key to map the value
     * @return int value for param key
     * @throws ItemNotFoundException if data could not be mapped to the param key
     * @throws WrongTypeException    data was saved with a different format and could not be parsed
     *                               to {@link Integer}
     */
    int getInt(@NonNull final String key) throws ItemNotFoundException, WrongTypeException;

    /**
     * @param key          the key to map the value
     * @param defaultValue if no data is stored for param key
     * @return int value for param key, or the param defaultValue
     * @throws WrongTypeException data was saved with a different format and could not be parsed to
     *                            {@link Integer}
     */
    int getInt(@NonNull final String key, final int defaultValue) throws WrongTypeException;

    /**
     * @param key the key to map the value
     * @return long value for param key
     * @throws ItemNotFoundException the key to map the value
     * @throws WrongTypeException    data was saved with a different format and could not be parsed
     *                               to {@link Long}
     */
    long getLong(@NonNull final String key) throws ItemNotFoundException, WrongTypeException;

    /**
     * @param key          the key to map the value
     * @param defaultValue if no data is stored for param key
     * @return long value for param key, or the param defaultValue
     * @throws WrongTypeException data was saved with a different format and could not be parsed to
     *                            {@link Long}
     */
    long getLong(@NonNull final String key, final long defaultValue) throws WrongTypeException;

    /**
     * Get a preference by its key
     *
     * @param key desired key
     * @return Returns the preference if found or null if it doesn't exist
     */
    @Nullable
    T getPref(@NonNull final String key);

    /**
     * Gets the String value of any data saved
     *
     * @param key the key to map the value
     * @return the data as String
     * @throws ItemNotFoundException when no data is found for the given param key
     */
    @Nullable
    String getString(@NonNull final String key) throws ItemNotFoundException;

    /**
     * Gets the String value of any data saved
     *
     * @param key          the key to map the value
     * @param defaultValue if no data is stored for param key
     * @return the data as String, or the param defaultValue
     */
    @Nullable
    String getString(@NonNull final String key, @Nullable final String defaultValue);

    /**
     * saves a {@link String} mapped to param key. String is the only data type which allows
     * {@code null} as param value
     *
     * @param key   the key to map the value
     * @param value the data to save
     * @return whether the put was successful
     * @throws IllegalArgumentException empty string value was passed as the key
     */
    boolean put(@NonNull final String key, @Nullable final String value);

    /**
     * saves a {@link Integer} mapped to param key
     *
     * @param key   the key to map the value
     * @param value the data to save
     * @return whether the put was successful
     * @throws IllegalArgumentException empty string value was passed as the key
     */
    boolean put(@NonNull final String key, final int value);

    /**
     * saves a {@link Float} mapped to param key
     *
     * @param key   the key to map the value
     * @param value the data to save
     * @return whether the put was successful
     * @throws IllegalArgumentException empty string value was passed as the key
     */
    boolean put(@NonNull final String key, final float value);

    /**
     * saves a {@link Long} mapped to param key
     *
     * @param key   the key to map the value
     * @param value the data to save
     * @return whether the put was successful
     * @throws IllegalArgumentException empty string value was passed as the key
     */
    boolean put(@NonNull final String key, final long value);

    /**
     * saves a {@link Boolean} mapped to param key
     *
     * @param key   the key to map the value
     * @param value the data to save
     * @return whether the put was successful
     * @throws IllegalArgumentException empty string value was passed as the key
     */
    boolean put(@NonNull final String key, final boolean value);

    /**
     * removes the data associated with param key
     *
     * @param key the key to map the value
     * @return whether the remove was successful
     */
    boolean remove(@NonNull final String key);

    /**
     * clear the data inside the preference and all evidence this preference has ever existed
     * <p>
     * also cleans internal information like the version for this preference
     *
     * @return true when successful, false otherwise
     * @see #clear()
     */
    boolean wipe();
}
