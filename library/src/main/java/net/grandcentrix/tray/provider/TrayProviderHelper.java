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

package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.util.SqliteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public class TrayProviderHelper {

    private final Uri mContentUri;

    private final Uri mContentUriInternal;

    private final Context mContext;

    public TrayProviderHelper(@NonNull final Context context) {
        mContext = context;
        mContentUri = TrayContract.generateContentUri(context);
        mContentUriInternal = TrayContract.generateInternalContentUri(context);
    }

    /**
     * clears <b>all</b> Preferences saved. Module independent. Erases everything
     */
    public void clear() {
        mContext.getContentResolver().delete(mContentUri, null, null);
    }

    /**
     * clears the stated modules
     *
     * @param modules which modules to clear
     */
    public void clear(TrayPreference... modules) {
        for (TrayPreference module : modules) {
            if (module == null) {
                continue;
            }
            module.clear();
        }
    }

    /**
     * clears <b>all</b> Preferences saved but the modules stated.
     *
     * @param modules modules excluded when deleting preferences
     */
    public void clearBut(TrayPreference... modules) {
        String selection = null;
        String[] selectionArgs = new String[]{};

        for (final TrayPreference module : modules) {
            if (module == null) {
                continue;
            }
            String moduleName = module.getModularizedStorage().getModuleName();
            selection = SqliteHelper
                    .extendSelection(selection, TrayContract.Preferences.Columns.MODULE + " != ?");
            selectionArgs = SqliteHelper
                    .extendSelectionArgs(selectionArgs, new String[]{moduleName});
        }

        mContext.getContentResolver().delete(mContentUri, selection, selectionArgs);
    }

    /**
     * Builds a list of all Preferences saved.
     *
     * @return all Preferences as list.
     */
    @NonNull
    public List<TrayItem> getAll() {
        return queryProvider(mContentUri);
    }

    public Uri getInternalUri() {
        return getUri(null, null, true);
    }

    public Uri getInternalUri(final String module) {
        return getUri(module, null, true);
    }

    public Uri getInternalUri(@Nullable final String module, @Nullable final String key) {
        return getUri(module, key, true);
    }

    public Uri getUri() {
        return getUri(null, null);
    }

    public Uri getUri(final String module) {
        return getUri(module, null);
    }

    public Uri getUri(@Nullable final String module, @Nullable final String key) {
        return getUri(module, key, false);
    }

    public Uri getUri(@Nullable final String module, @Nullable final String key,
            final boolean internal) {
        if (module == null && key != null) {
            throw new IllegalArgumentException(
                    "key without module is not valid. Look into the TryProvider for valid Uris");
        }
        final Uri uri = internal ? mContentUriInternal : mContentUri;
        final Uri.Builder builder = uri
                .buildUpon();
        if (module != null) {
            builder.appendPath(module);
        }
        if (key != null) {
            builder.appendPath(key);
        }
        return builder.build();
    }

    /**
     * saves the value into the database.
     *
     * @param module module name
     * @param key    key for mapping
     * @param value  data to save
     */
    public void persist(@NonNull final String module, @NonNull final String key,
            @NonNull final String value) {
        persist(module, key, null, value);
    }

    /**
     * saves the value into the database combined with a previousKey.
     *
     * @param module      module name
     * @param key         key for mapping
     * @param previousKey key used before migration
     * @param value       data to save
     */
    public void persist(@NonNull final String module, @NonNull final String key,
            @Nullable final String previousKey, @Nullable final String value) {
        persist(module, key, previousKey, value, false);
    }

    /**
     * saves data internally, not accessible with public api
     *
     * @param module module name
     * @param key    key for mapping
     * @param value  data to save
     */
    public void persistInternal(@NonNull final String module, @NonNull final String key,
            @Nullable final String value) {
        persist(module, key, null, value, true);
    }

    /**
     * sends a query for TrayItems to the provider
     *
     * @param uri path to data
     * @return list of items
     * @throws IllegalStateException something is wrong with the provider/database
     */
    @NonNull
    public List<TrayItem> queryProvider(@NonNull final Uri uri)
            throws IllegalStateException {
        final Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        // Return Preference if found
        if (cursor == null) {
            throw new IllegalStateException(
                    "could not access stored data with uri " + uri
                            + ". Is the provider registered in the manifest of your application?");
            //todo test with tray mock context
        }

        final ArrayList<TrayItem> list = new ArrayList<>();
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            list.add(new TrayItem(cursor));
        }
        cursor.close();
        return list;
    }

    /**
     * @param module      module name
     * @param key         key for mapping
     * @param value       data to save
     * @param previousKey key before the migration
     * @param internal    where to save
     */
    private void persist(@NonNull final String module, @NonNull final String key,
            @Nullable final String previousKey, @Nullable final String value,
            final boolean internal) {

        final Uri contentUri = internal ? mContentUriInternal : mContentUri;
        final Uri uri = contentUri
                .buildUpon()
                .appendPath(module)
                .appendPath(key)
                .build();
        ContentValues values = new ContentValues();
        values.put(TrayContract.Preferences.Columns.VALUE, value);
        values.put(TrayContract.Preferences.Columns.MIGRATED_KEY, previousKey);
        mContext.getContentResolver().insert(uri, values);
    }
}
