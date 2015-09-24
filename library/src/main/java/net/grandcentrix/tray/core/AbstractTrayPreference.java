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

/**
 * Modular implementation of a {@link Preferences} which allows access to a {@link TrayStorage}.
 *
 * Created by pascalwelsch on 11/20/14.
 */
public abstract class AbstractTrayPreference<T extends TrayStorage> extends
        Preferences<TrayItem, T> {

    protected AbstractTrayPreference(@NonNull final T storage, final int version) {
        super(storage, version);
    }

    @Override
    public boolean getBoolean(@NonNull final String key, final boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (ItemNotFoundException e) {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(@NonNull final String key) throws ItemNotFoundException {
        final String value = getString(key);
        return Boolean.parseBoolean(value);
    }

    @Override
    public float getFloat(@NonNull final String key, final float defaultValue) {
        try {
            return getFloat(key);
        } catch (ItemNotFoundException e) {
            return defaultValue;
        }
    }

    @Override
    public float getFloat(@NonNull final String key) throws ItemNotFoundException {
        final String value = getString(key);
        throwForNullValue(value, Float.class, key);
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new WrongTypeException(e);
        }
    }

    @Override
    public int getInt(@NonNull final String key, final int defaultValue) {
        try {
            return getInt(key);
        } catch (ItemNotFoundException e) {
            return defaultValue;
        }
    }

    @Override
    public int getInt(@NonNull final String key) throws ItemNotFoundException {
        final String value = getString(key);
        throwForNullValue(value, Integer.class, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new WrongTypeException(e);
        }
    }

    @Override
    public long getLong(@NonNull final String key, final long defaultValue) {
        try {
            return getLong(key);
        } catch (ItemNotFoundException e) {
            return defaultValue;
        }
    }

    @Override
    public long getLong(@NonNull final String key) throws ItemNotFoundException {
        final String value = getString(key);
        throwForNullValue(value, Long.class, key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new WrongTypeException(e);
        }
    }

    /**
     * @return the module name of this preference
     */
    public String getName() {
        return getStorage().getModuleName();
    }

    @Override
    public String getString(@NonNull final String key) throws ItemNotFoundException {
        final TrayItem pref = getPref(key);
        if (pref == null) {
            throw new ItemNotFoundException("Value for Key <%s> not found", key);
        }
        return pref.value();
    }

    @Override
    @Nullable
    public String getString(@NonNull final String key, final String defaultValue) {
        try {
            return getString(key);
        } catch (ItemNotFoundException e) {
            return defaultValue;
        }
    }

    /**
     * registers a listener which gets called when a tray preference is changed, added, or removed.
     * This may be called even if a preference is set to its existing value.
     * <p>
     * The listener gets called on the same Looper you call this method. Registering it on the main
     * tread causes the listener to call on the main thread, too.</p>
     * <p>
     * <strong>Caution:</strong> The storage does not store a strong reference to the listener.
     * You must store a strong reference to the listener, or it will be susceptible to garbage
     * collection. We recommend you keep a reference to the listener in the instance data of an
     * object that will exist as long as you need the listener.</p>
     * <p>
     * don't forget to unregister the listener when no longer needed in {@link
     * #unregisterOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)}</p>
     *
     * @param listener the listener that will run.
     * @see #unregisterOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)
     */
    public void registerOnTrayPreferenceChangeListener(
            @NonNull OnTrayPreferenceChangeListener listener) {
        getStorage().registerOnTrayPreferenceChangeListener(listener);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(@" + Integer.toHexString(hashCode()) + "){"
                + "name=" + getName()
                + "}";
    }

    /**
     * unregisters the previously registered callback
     *
     * @param listener The callback that should be unregistered.
     * @see #registerOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)
     */
    public void unregisterOnTrayPreferenceChangeListener(
            @NonNull OnTrayPreferenceChangeListener listener) {
        getStorage().unregisterOnTrayPreferenceChangeListener(listener);
    }

    /**
     * imports all data from an old storage. Use this if you have changed the module name
     * <p>
     * Call this in {@link #onCreate(int)}. The created and updated fields of the old {@link
     * TrayItem}s will be lost. The old data gets deleted completely.
     *
     * @param oldStorage the old storage
     */
    protected void annex(final T oldStorage) {
        getStorage().annex(oldStorage);
        TrayLog.v("annexed " + oldStorage + " to " + this);
    }

    /**
     * logs a warning that warns that the given value for the given key is null and null is only
     * supported when reading it as a String and not other java primitives
     */
    private void throwForNullValue(@Nullable final String value,
            final Class<?> clazz, final @NonNull String key) throws WrongTypeException {
        if (value == null) {
            throw new WrongTypeException("The value for key <" + key + "> is null. "
                    + "You obviously saved this value as String and try to access it with type "
                    + clazz.getSimpleName() + " which cannot be null. "
                    + " Always use getString(key, defaultValue) when accessing data you saved with put(String).");
        }
    }
}
