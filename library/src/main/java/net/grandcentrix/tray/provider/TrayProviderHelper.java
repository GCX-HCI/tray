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
import net.grandcentrix.tray.util.SqlSelectionHelper;

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

    final Context mContext;

    public TrayProviderHelper(@NonNull final Context context) {
        mContext = context;
    }

    /**
     * clears <b>all</b> Preferences saved. Module independent. Erases everything
     */
    public void clear() {
        mContext.getContentResolver().delete(TrayProvider.CONTENT_URI, null, null);
    }

    /**
     * clears the stated modules
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
            String moduleName = module.getModularizedStorage().getModule();
            selection = SqlSelectionHelper
                    .extendSelection(selection, TrayContract.Preferences.Columns.MODULE + " != ?");
            selectionArgs = SqlSelectionHelper
                    .extendSelectionArgs(selectionArgs, new String[]{moduleName});
        }

        mContext.getContentResolver().delete(TrayProvider.CONTENT_URI, selection, selectionArgs);
    }

    /**
     * Builds a list of all Preferences saved.
     *
     * @return all Preferences as list.
     */
    @NonNull
    public List<TrayItem> getAll() {
        return queryProvider(TrayProvider.CONTENT_URI);
    }

    public static Uri getInternalUri() {
        return getUri(null, null, true);
    }

    public static Uri getInternalUri(final String module) {
        return getUri(module, null, true);
    }

    public static Uri getInternalUri(@Nullable final String module, @Nullable final String key) {
        return getUri(module, key, true);
    }

    public static Uri getUri() {
        return getUri(null, null);
    }

    public static Uri getUri(final String module) {
        return getUri(module, null);
    }

    public static Uri getUri(@Nullable final String module, @Nullable final String key) {
        return getUri(module, key, false);
    }

    public static Uri getUri(@Nullable final String module, @Nullable final String key,
            final boolean internal) {
        if (module == null && key != null) {
            throw new IllegalArgumentException(
                    "key without module is not valid. Look into the TryProvider for valid Uris");
        }
        final Uri uri = internal
                ? TrayProvider.CONTENT_URI_INTERNAL
                : TrayProvider.CONTENT_URI;
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
     */
    public void persist(@NonNull final String module, @NonNull final String key,
            @NonNull final String value) {
        persist(module, key, value, false);
    }

    public void persist(@NonNull final String module, @NonNull final String key,
            @NonNull final String value, final boolean internal) {
        //noinspection ConstantConditions
        if (value == null) {
            return;
        }

        final Uri contentUri = internal
                ? TrayProvider.CONTENT_URI_INTERNAL
                : TrayProvider.CONTENT_URI;
        final Uri uri = contentUri
                .buildUpon()
                .appendPath(module)
                .appendPath(key)
                .build();
        ContentValues values = new ContentValues();
        values.put(TrayContract.Preferences.Columns.VALUE, value);
        mContext.getContentResolver().insert(uri, values);
    }

    public void persistInternal(@NonNull final String module, @NonNull final String key,
            @NonNull final String value) {
        persist(module, key, value, true);
    }

    @NonNull
    public List<TrayItem> queryProvider(@NonNull final Uri uri)
            throws IllegalStateException {
        final Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        final ArrayList<TrayItem> list = new ArrayList<>();
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            list.add(new TrayItem(cursor));
        }
        cursor.close();
        return list;
    }
}
