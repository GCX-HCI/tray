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

package net.grandcentrix.tray.storage;

import net.grandcentrix.tray.TrayRuntimeException;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderHelper;
import net.grandcentrix.tray.provider.TrayUri;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collection;
import java.util.List;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * Implements the functionality between the {@link net.grandcentrix.tray.TrayPreferences}
 * and the {@link net.grandcentrix.tray.provider.TrayProvider}. Uses functions of the {@link
 * net.grandcentrix.tray.provider.TrayProviderHelper} for simple and unified access to the
 * provider.
 * <p>
 * This class represents a simple key value storage solution based on a {@link
 * android.content.ContentProvider}. Replacing this class with a {@link java.util.HashMap}
 * implementation for testing works seamless.
 */
public class TrayStorage extends ModularizedStorage<TrayItem> {

    /**
     * The type of the data indicating their backup strategy to the cloud
     */
    public enum Type {
        /**
         * don't use {@link #UNDEFINED} when creating a {@link net.grandcentrix.tray.TrayPreferences}.
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

    public static final String VERSION = "version";

    private static final String TAG = TrayStorage.class.getSimpleName();

    private final Context mContext;

    private final TrayProviderHelper mProviderHelper;

    private final TrayUri mTrayUri;

    private final Type mType;

    public TrayStorage(@NonNull final Context context, @NonNull final String module,
            @NonNull final Type type) {
        super(module);
        mContext = context.getApplicationContext();
        mTrayUri = new TrayUri(mContext);
        mProviderHelper = new TrayProviderHelper(mContext);
        mType = type;
    }

    @Override
    public void annex(final ModularizedStorage<TrayItem> oldStorage) {
        for (final TrayItem trayItem : oldStorage.getAll()) {
            put(trayItem);
        }
        oldStorage.wipe();
    }

    @Override
    public void clear() {
        final Uri uri = mTrayUri.builder()
                .setModule(getModuleName())
                .setType(mType)
                .build();
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    @Nullable
    public TrayItem get(@NonNull final String key) {
        final Uri uri = mTrayUri.builder()
                .setType(mType)
                .setModule(getModuleName())
                .setKey(key)
                .build();
        final List<TrayItem> prefs = mProviderHelper.queryProvider(uri);
        final int size = prefs.size();
        if (size > 1) {
            Log.w(TAG, "found more than one item for key '" + key
                    + "' in module " + getModuleName() + ". "
                    + "This can be caused by using the same name for a device and user specific preference.");
            for (int i = 0; i < prefs.size(); i++) {
                final TrayItem pref = prefs.get(i);
                Log.d(TAG, "item #" + i + " " + pref);
            }
        }
        return size > 0 ? prefs.get(0) : null;
    }

    @NonNull
    @Override
    public Collection<TrayItem> getAll() {
        final Uri uri = mTrayUri.builder()
                .setType(mType)
                .setModule(getModuleName())
                .build();
        return mProviderHelper.queryProvider(uri);
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Indicates where the data internally gets stored and how the backup is handled for the data
     *
     * @return the type of storage
     */
    public Type getType() {
        return mType;
    }

    @Override
    public int getVersion() {
        final Uri internalUri = mTrayUri.builder()
                .setInternal(true)
                .setType(mType)
                .setModule(getModuleName())
                .setKey(VERSION)
                .build();
        final List<TrayItem> trayItems = mProviderHelper.queryProvider(internalUri);
        if (trayItems.size() == 0) {
            // fallback, not found
            return 0;
        }
        return Integer.valueOf(trayItems.get(0).value());
    }

    @Override
    public void put(final TrayItem item) {
        put(item.key(), item.migratedKey(), item.value());
    }

    @Override
    public void put(@NonNull final String key, @Nullable final Object data) {
        put(key, null, data);
    }

    /**
     * same as {@link #put(String, Object)} but with an additional migration key to save where the
     * data came from. Putting data twice with the same param migraionKey does not override the
     * already saved data. This should prevent migrating data multiple times while the data my be
     * edited with {@link #put(String, Object)}.
     *
     * @param key          where to save
     * @param migrationKey where the data came from
     * @param data         what to save
     */
    @Override
    public void put(@NonNull final String key, @Nullable final String migrationKey,
            @Nullable final Object data) {
        if (getType() == Type.UNDEFINED) {
            throw new TrayRuntimeException(
                    "writing data into a storage with type UNDEFINED is forbidden. Only Read and delete is allowed.");
        }

        final String value = data == null ? null : String.valueOf(data);

        final Uri uri = mTrayUri.builder()
                .setType(mType)
                .setModule(getModuleName())
                .setKey(key)
                .build();
        mProviderHelper.persist(uri, value, migrationKey);
    }

    @Override
    public void remove(@NonNull final String key) {
        //noinspection ConstantConditions
        if (key == null) {
            throw new IllegalArgumentException(
                    "null is not valid. use clear or wipe to delete all preferences");
        }
        final Uri uri = mTrayUri.builder()
                .setType(mType)
                .setModule(getModuleName())
                .setKey(key)
                .build();
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    public void setVersion(final int version) {
        if (getType() == Type.UNDEFINED) {
            throw new TrayRuntimeException(
                    "writing data into a storage with type UNDEFINED is forbidden. Only Read and delete is allowed.");
        }
        final Uri uri = mTrayUri.builder()
                .setInternal(true)
                .setType(mType)
                .setModule(getModuleName())
                .setKey(VERSION)
                .build();
        mProviderHelper.persist(uri, String.valueOf(version));
    }

    /**
     * clear the data inside the preference and all evidence this preference has ever existed
     * <p>
     * also cleans internal information like the version for this preference
     *
     * @see #clear()
     */
    public void wipe() {
        clear();
        final Uri uri = mTrayUri.builder()
                .setInternal(true)
                .setType(mType)
                .setModule(getModuleName())
                .build();
        mContext.getContentResolver().delete(uri, null, null);
    }
}
