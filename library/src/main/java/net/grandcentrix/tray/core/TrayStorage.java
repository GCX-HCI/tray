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

import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * A storage is now separated in modules (by name) and has a {@link Type} indicating how the data
 * stored will be handled with the Android Auto Backup feature. see
 * https://github.com/grandcentrix/tray/wiki/Android-M-Auto-Backup-for-Apps-support
 */
public abstract class TrayStorage implements PreferenceStorage<TrayItem> {

    /**
     * The type of the data indicating their backup strategy to the cloud
     */
    public enum Type {
        /**
         * don't use {@link #UNDEFINED} when creating a {@link TrayPreferences}.
         * It's used internally to import a preference by moduleName without knowing the location
         * of this preference (user or device). Because of that a undefined TrayStorage lookups the
         * data in both data stores.
         * <p>
         * Because it's not clear where to save data a undefined TrayStorage is only able to read
         * and delete items. Writing with <code>put()</code> is <b>not</b> allowed.
         */
        UNDEFINED,
        /**
         * the data relates to the user and can be saved in the cloud and restored on another
         * device
         */
        USER,
        /**
         * the data is device specific like a GCM push token or settings that is important for this
         * specific device.
         * <p>
         * Such data shouldn't saved to the cloud with auto backup since Android Marshmallow.
         */
        DEVICE
    }

    private String mModuleName;

    private Type mType;

    public TrayStorage(final String moduleName, final Type type) {
        mModuleName = moduleName;
        mType = type;
    }

    /**
     * imports all data from an old storage. The old storage gets wiped afterwards.
     * <p>
     * Use this if you have changed the module name
     *
     * @param oldStorage the old preference
     */
    public abstract void annex(final TrayStorage oldStorage);

    public String getModuleName() {
        return mModuleName;
    }

    /**
     * Indicates where the data internally gets stored and how the backup is handled for the data
     *
     * @return the type of storage
     */
    public Type getType() {
        return mType;
    }

    /**
     * registers a listener which gets called when a tray preference is changed, added, or removed.
     * This may be called even if a preference is set to its existing value.
     * <p>
     * <strong>Caution:</strong> The storage does not store a strong reference to the listener.
     * You must store a strong reference to the listener, or it will be susceptible to garbage
     * collection. We recommend you keep a reference to the listener in the instance data of an
     * object that will exist as long as you need the listener.</p>
     * <p>
     * don't forget to unregister the listener when no longer needed in {@link
     * #unregisterOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)}
     *
     * @param listener the listener that will run.
     * @see #unregisterOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)
     */
    public abstract void registerOnTrayPreferenceChangeListener(
            @NonNull OnTrayPreferenceChangeListener listener);

    /**
     * unregisters the previously registered callback
     *
     * @param listener The callback that should be unregistered.
     * @see #registerOnTrayPreferenceChangeListener(OnTrayPreferenceChangeListener)
     */
    public abstract void unregisterOnTrayPreferenceChangeListener(
            @NonNull OnTrayPreferenceChangeListener listener);
}
