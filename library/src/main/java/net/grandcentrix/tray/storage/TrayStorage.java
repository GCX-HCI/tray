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

import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderHelper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * Implements the functionality between the {@link net.grandcentrix.tray.accessor.TrayPreference}
 * and the {@link net.grandcentrix.tray.provider.TrayProvider}. Uses functions of the {@link
 * net.grandcentrix.tray.provider.TrayProviderHelper} for simple and unified access to the
 * provider.
 * <p>
 * This class represents a simple key value storage solution based on a {@link
 * android.content.ContentProvider}. Replacing this class with a {@link java.util.HashMap}
 * implementation for testing works seamless.
 */
public class TrayStorage extends ModularizedStorage<TrayItem> {

    public static final String VERSION = "version";

    private final Context mContext;

    private final TrayProviderHelper mProviderHelper;

    public TrayStorage(@NonNull final Context context, @NonNull final String module) {
        super(module);
        mContext = context.getApplicationContext();
        mProviderHelper = new TrayProviderHelper(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void clear() {
        final Uri uri = mProviderHelper.getUri().buildUpon().appendPath(getModuleName())
                .build();
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    @Nullable
    public TrayItem get(@NonNull final String key) {
        final Uri uri = mProviderHelper.getUri(getModuleName(), key);
        final List<TrayItem> prefs = mProviderHelper.queryProvider(uri);
        return prefs.size() == 1 ? prefs.get(0) : null;
    }

    @NonNull
    @Override
    public Collection<TrayItem> getAll() {
        final Uri uri = mProviderHelper.getUri(getModuleName());
        return mProviderHelper.queryProvider(uri);
    }

    @Override
    public int getVersion() {
        final Uri internalUri = mProviderHelper.getInternalUri(getModuleName(), VERSION);
        final List<TrayItem> trayItems = mProviderHelper.queryProvider(internalUri);
        if (trayItems.size() == 0) {
            // fallback, not found
            return 0;
        }
        return Integer.valueOf(trayItems.get(0).value());
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
        String value = data == null ? null : String.valueOf(data);
        mProviderHelper.persist(getModuleName(), key, migrationKey, value);
    }

    @Override
    public void remove(@NonNull final String key) {
        //noinspection ConstantConditions
        if (key == null) {
            throw new IllegalArgumentException(
                    "null is not valid. use clear to delete all preferences");
        }
        final Uri uri = mProviderHelper.getUri(getModuleName(), key);
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    public void setVersion(final int version) {
        mProviderHelper.persistInternal(getModuleName(), VERSION, String.valueOf(version));
    }
}
