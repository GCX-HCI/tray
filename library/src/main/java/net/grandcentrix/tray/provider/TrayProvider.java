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

import net.grandcentrix.tray.R;
import net.grandcentrix.tray.util.ProviderHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.Date;

/**
 * Created by jannisveerkamp on 16.09.14.
 */
public class TrayProvider extends ContentProvider {

    private static final int SINGLE_PREFERENCE = 10;

    private static final int MODULE_PREFERENCE = 20;

    private static final int ALL_PREFERENCE = 30;

    private static final String TAG = TrayProvider.class.getSimpleName();

    private static UriMatcher sURIMatcher;

    private TrayDBHelper mDbHelper;

    @Override
    public int delete(final Uri uri, String selection, String[] selectionArgs) {

        final String table;
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
                selection = ProviderHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.KEY + " = ?");
                selectionArgs = ProviderHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(2)});
                // no break
            case MODULE_PREFERENCE:
                selection = ProviderHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.MODULE + " = ?");
                selectionArgs = ProviderHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(1)});
                // no break
            case ALL_PREFERENCE:
                table = TrayDBHelper.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for Uri: " + uri);
        }

        final int rows = mDbHelper.getWritableDatabase().delete(table, selection, selectionArgs);

        // Don't force an UI refresh if nothing has changed
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final String table;
        final int match = sURIMatcher.match(uri);

        switch (match) {
            case SINGLE_PREFERENCE:
                // Add created and updated dates
                Date date = new Date();
                values.put(TrayContract.Preferences.Columns.CREATED, date.getTime());
                values.put(TrayContract.Preferences.Columns.UPDATED, date.getTime());
                values.put(TrayContract.Preferences.Columns.MODULE,
                        uri.getPathSegments().get(1));
                values.put(TrayContract.Preferences.Columns.KEY,
                        uri.getPathSegments().get(2));
                table = TrayDBHelper.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Insert is not supported for Uri: " + uri);
        }

        try {
            //long rows = mDbHelper.getWritableDatabase().insertOrThrow(table, null, values);
            final int status = insertOrUpdate(table, values);

            if (status >= 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            } else {
                //throw new SQLiteException("An error occurred while saving preference.");
                Log.w(TAG, "Couldn't update or insert data. Uri: " + uri);
                return null;
            }
        } catch (SQLiteException e) {
            Log.w(TAG, "Data is already inserted, no need to insert here");
        }

        return uri;
    }

    @Override
    public boolean onCreate() {
        setAuthority(getContext().getString(R.string.tray__authority));

        mDbHelper = new TrayDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
            final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        final int match = sURIMatcher.match(uri);

        switch (match) {
            case SINGLE_PREFERENCE:
                builder.appendWhere(
                        TrayContract.Preferences.Columns.KEY + " = " +
                                DatabaseUtils.sqlEscapeString(uri.getPathSegments().get(2)));
                // no break
            case MODULE_PREFERENCE:
                if (match == SINGLE_PREFERENCE) {
                    builder.appendWhere(" AND ");
                }
                builder.appendWhere(
                        TrayContract.Preferences.Columns.MODULE + " = " +
                                DatabaseUtils.sqlEscapeString(uri.getPathSegments().get(1)));
                // no break
            case ALL_PREFERENCE:
                builder.setTables(TrayDBHelper.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Query is not supported for Uri: " + uri);
        }

        // Query
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs) {
        final String table;
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
                // Add updated date
                Date date = new Date();
                values.put(TrayContract.Preferences.Columns.UPDATED, date.getTime());

                table = TrayDBHelper.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for Uri: " + uri);
        }

        final int rows = mDbHelper.getWritableDatabase().update(table, values, selection,
                selectionArgs);

        // Don't force an UI refresh if nothing has changed
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    /**
     * @see TrayContract#setAuthority(String)
     */
    static void setAuthority(final String authority) {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sURIMatcher.addURI(authority,
                TrayContract.Preferences.BASE_PATH,
                ALL_PREFERENCE);

        // BASE/module
        sURIMatcher.addURI(authority,
                TrayContract.Preferences.BASE_PATH + "/*",
                MODULE_PREFERENCE);

        // BASE/module/key
        sURIMatcher.addURI(authority,
                TrayContract.Preferences.BASE_PATH + "/*/*",
                SINGLE_PREFERENCE);
    }

    private int insertOrUpdate(final String table, final ContentValues values) {
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        if (sqlDB == null) {
            return -1;
        }

        final String prefSelection =
                TrayContract.Preferences.Columns.MODULE + " = ?"
                        + "AND " + TrayContract.Preferences.Columns.KEY + " = ?";
        final String[] prefSelectionArgs = {
                values.getAsString(TrayContract.Preferences.Columns.MODULE),
                values.getAsString(TrayContract.Preferences.Columns.KEY)
        };

        final long items = DatabaseUtils
                .queryNumEntries(sqlDB, table, prefSelection, prefSelectionArgs);

        if (items == 0) {
            // insert
            final long row = sqlDB.insertOrThrow(table, null, values);
            if (row == -1) {
                throw new SQLiteException("an error occurred");
            }
            return 1;
        } else {
            // If insert fails (row already present) try an update
            // Remove created timestamp since it shouldn't be updated
            values.remove(TrayContract.Preferences.Columns.CREATED);
            final int update = sqlDB.update(table, values,
                    prefSelection,
                    prefSelectionArgs);
            if (update > 0) {
                return 0;
            }

            Log.w(TAG, "Could not insert or update preference ("
                    + values.getAsString(TrayContract.Preferences.Columns.MODULE) + "/"
                    + values.getAsString(TrayContract.Preferences.Columns.KEY) + ")");
            return -1;
        }
    }
}
