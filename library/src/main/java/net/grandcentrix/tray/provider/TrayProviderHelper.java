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

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.TrayItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper for accessing the {@link TrayContentProvider}
 * <p>
 * Created by pascalwelsch on 11/20/14.
 */
public class TrayProviderHelper {

    private final Context mContext;

    private final TrayUri mTrayUri;

    public TrayProviderHelper(@NonNull final Context context) {
        mContext = context;
        mTrayUri = new TrayUri(context);
    }

    /**
     * clears <b>all</b> Preferences saved. Module independent. Erases all preference data
     */
    public void clear() {
        mContext.getContentResolver().delete(mTrayUri.get(), null, null);
    }

    /**
     * clears <b>all</b> {@link TrayPreferences} but the modules stated.
     *
     * @param modules modules excluded when deleting preferences
     */
    public void clearBut(TrayPreferences... modules) {
        String selection = null;
        String[] selectionArgs = new String[]{};

        for (final TrayPreferences module : modules) {
            if (module == null) {
                continue;
            }
            String moduleName = module.getName();
            selection = SqliteHelper
                    .extendSelection(selection, TrayContract.Preferences.Columns.MODULE + " != ?");
            selectionArgs = SqliteHelper
                    .extendSelectionArgs(selectionArgs, new String[]{moduleName});
        }

        mContext.getContentResolver().delete(mTrayUri.get(), selection, selectionArgs);
    }

    /**
     * Builds a list of all Preferences saved.
     *
     * @return all Preferences as list.
     */
    @NonNull
    public List<TrayItem> getAll() {
        return queryProvider(mTrayUri.get());
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
        final Uri uri = mTrayUri.builder()
                .setModule(module)
                .setKey(key)
                .build();
        persist(uri, value, previousKey);
    }

    public void persist(@NonNull final Uri uri, @Nullable String value) {
        persist(uri, value, null);
    }

    public void persist(@NonNull final Uri uri, @Nullable String value,
            @Nullable final String previousKey) {
        ContentValues values = new ContentValues();
        values.put(TrayContract.Preferences.Columns.VALUE, value);
        values.put(TrayContract.Preferences.Columns.MIGRATED_KEY, previousKey);
        mContext.getContentResolver().insert(uri, values);
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
        }

        final ArrayList<TrayItem> list = new ArrayList<>();
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            final TrayItem trayItem = cursorToTrayItem(cursor);
            list.add(trayItem);
        }
        cursor.close();
        return list;
    }

    /**
     * wipes all data, including meta data for the preferences like the current version number.
     */
    public void wipe() {
        clear();
        mContext.getContentResolver().delete(mTrayUri.getInternal(), null, null);
    }

    /**
     * converts a {@link Cursor} to a {@link TrayItem}
     * <p>
     * This is not a secondary constructor in {@link TrayItem} because the columns are a
     * implementation detail of the provider package
     *
     * @param cursor (size > 1)
     * @return a {@link TrayItem} filled with data
     */
    @NonNull
    static TrayItem cursorToTrayItem(final Cursor cursor) {
        final String module = cursor.getString(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.MODULE));
        final String key = cursor.getString(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.KEY));
        final String migratedKey = cursor.getString(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.MIGRATED_KEY));
        final String value = cursor.getString(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.VALUE));
        final Date created = new Date(cursor.getLong(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.CREATED)));
        final Date updated = new Date(cursor.getLong(cursor
                .getColumnIndexOrThrow(TrayContract.Preferences.Columns.UPDATED)));
        return new TrayItem(module, key, migratedKey, value, created, updated);
    }
}
