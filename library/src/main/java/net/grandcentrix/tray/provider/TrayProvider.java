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

import net.grandcentrix.tray.BuildConfig;
import net.grandcentrix.tray.util.SqlSelectionHelper;

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

    private static final int INTERNAL_SINGLE_PREFERENCE = 110;

    private static final int INTERNAL_MODULE_PREFERENCE = 120;

    private static final int INTERNAL_ALL_PREFERENCE = 130;

    private static final String TAG = TrayProvider.class.getSimpleName();

    public static String AUTHORITY;

    public static Uri AUTHORITY_URI;

    public static Uri CONTENT_URI;

    public static Uri CONTENT_URI_INTERNAL;

    private static UriMatcher sURIMatcher;

    static {
        setAuthority(BuildConfig.AUTHORITY);
    }

    /*protected*/ TrayDBHelper mDbHelper;

    @Override
    public int delete(final Uri uri, String selection, String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
            case INTERNAL_SINGLE_PREFERENCE:
                selection = SqlSelectionHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.KEY + " = ?");
                selectionArgs = SqlSelectionHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(2)});
                // no break
            case MODULE_PREFERENCE:
            case INTERNAL_MODULE_PREFERENCE:
                selection = SqlSelectionHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.MODULE + " = ?");
                selectionArgs = SqlSelectionHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(1)});
                // no break
            case ALL_PREFERENCE:
            case INTERNAL_ALL_PREFERENCE:
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for Uri: " + uri);
        }

        final int rows = mDbHelper.getWritableDatabase()
                .delete(getTable(uri), selection, selectionArgs);

        // Don't force an UI refresh if nothing has changed
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    public SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        Date date = new Date();
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
            case INTERNAL_SINGLE_PREFERENCE:
                // Add created and updated dates
                values.put(TrayContract.Preferences.Columns.CREATED, date.getTime());
                values.put(TrayContract.Preferences.Columns.UPDATED, date.getTime());
                values.put(TrayContract.Preferences.Columns.MODULE, uri.getPathSegments().get(1));
                values.put(TrayContract.Preferences.Columns.KEY, uri.getPathSegments().get(2));
                break;

            default:
                throw new IllegalArgumentException("Insert is not supported for Uri: " + uri);
        }

        try {
            //long rows = mDbHelper.getWritableDatabase().insertOrThrow(table, null, values);
            final int status = insertOrUpdate(getTable(uri), values);

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
            case INTERNAL_SINGLE_PREFERENCE:
                builder.appendWhere(
                        TrayContract.Preferences.Columns.KEY + " = " +
                                DatabaseUtils.sqlEscapeString(uri.getPathSegments().get(2)));
                // no break
            case MODULE_PREFERENCE:
            case INTERNAL_MODULE_PREFERENCE:
                if (match == SINGLE_PREFERENCE
                        || match == INTERNAL_SINGLE_PREFERENCE) {
                    builder.appendWhere(" AND ");
                }
                builder.appendWhere(
                        TrayContract.Preferences.Columns.MODULE + " = " +
                                DatabaseUtils.sqlEscapeString(uri.getPathSegments().get(1)));
                // no break
            case ALL_PREFERENCE:
            case INTERNAL_ALL_PREFERENCE:
                builder.setTables(getTable(uri));
                break;
            default:
                throw new IllegalArgumentException("Query is not supported for Uri: " + uri);
        }

        // Query
        Cursor cursor = builder.query(getReadableDatabase(), projection, selection,
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
        throw new UnsupportedOperationException("not implemented");

        // this is a standard implementation (but untested at the moment).
        // Perhaps useful in the future. The current implementation doesn't require a an
        // update mechanism other than multiple calls to {@link #insert} which results
        // in an {@link #insertOrUpdate} call

        /*final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
            case INTERNAL_SINGLE_PREFERENCE:
                // Add updated date
                values.put(TrayContract.Preferences.Columns.UPDATED, new Date().getTime());
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for Uri: " + uri);
        }

        final int rows = mDbHelper.getWritableDatabase()
                .update(getTable(uri), values, selection, selectionArgs);

        // Don't force an UI refresh if nothing has changed
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;*/
    }

    public String getTable(final Uri uri) {
        if (uri == null) {
            return null;
        }
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
            case MODULE_PREFERENCE:
            case ALL_PREFERENCE:
            default:
                return TrayDBHelper.TABLE_NAME;

            case INTERNAL_SINGLE_PREFERENCE:
            case INTERNAL_MODULE_PREFERENCE:
            case INTERNAL_ALL_PREFERENCE:
                return TrayDBHelper.INTERNAL_TABLE_NAME;
        }
    }

    /**
     * Tries to insert the values. If it fails because the item already exists it tries to update
     * the item.
     *
     * @param table  the table to insert
     * @param values the values to insert
     * @return 1 for insert, 0 for update and -1 if an error occurred
     */
    /*package*/ int insertOrUpdate(final String table, final ContentValues values) {
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
            sqlDB.insertOrThrow(table, null, values);
            return 1;
        } else {
            // If insert fails (row already present) try an update
            // Remove created timestamp since it shouldn't be updated
            values.remove(TrayContract.Preferences.Columns.CREATED);
            sqlDB.update(table, values, prefSelection, prefSelectionArgs);
            return 0;
        }
    }

    /*package*/
    static void setAuthority(final String authority) {
        AUTHORITY = authority;

        AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

        CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TrayContract.Preferences.BASE_PATH);

        CONTENT_URI_INTERNAL = Uri
                .withAppendedPath(AUTHORITY_URI, TrayContract.InternalPreferences.BASE_PATH);

        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.Preferences.BASE_PATH,
                ALL_PREFERENCE);

        // BASE/module
        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.Preferences.BASE_PATH + "/*",
                MODULE_PREFERENCE);

        // BASE/module/key
        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.Preferences.BASE_PATH + "/*/*",
                SINGLE_PREFERENCE);

        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.InternalPreferences.BASE_PATH,
                INTERNAL_ALL_PREFERENCE);

        // INTERNAL_BASE/module
        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.InternalPreferences.BASE_PATH + "/*",
                INTERNAL_MODULE_PREFERENCE);

        // INTERNAL_BASE/module/key
        sURIMatcher.addURI(TrayProvider.AUTHORITY,
                TrayContract.InternalPreferences.BASE_PATH + "/*/*",
                INTERNAL_SINGLE_PREFERENCE);
    }
}
