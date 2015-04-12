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
import net.grandcentrix.tray.util.SqliteHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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

    private static UriMatcher sURIMatcher;

    TrayDBHelper mDbHelper;

    @Override
    public int delete(final Uri uri, String selection, String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        switch (match) {
            case SINGLE_PREFERENCE:
            case INTERNAL_SINGLE_PREFERENCE:
                selection = SqliteHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.KEY + " = ?");
                selectionArgs = SqliteHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(2)});
                // no break
            case MODULE_PREFERENCE:
            case INTERNAL_MODULE_PREFERENCE:
                selection = SqliteHelper.extendSelection(selection,
                        TrayContract.Preferences.Columns.MODULE + " = ?");
                selectionArgs = SqliteHelper.extendSelectionArgs(selectionArgs,
                        new String[]{uri.getPathSegments().get(1)});
                // no break
            case ALL_PREFERENCE:
            case INTERNAL_ALL_PREFERENCE:
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for Uri: " + uri);
        }

        final int rows = getWritableDatabase()
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

    /**
     * @param uri localtion of the data
     * @return correct sqlite table for the given {@param uri}
     */
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

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mDbHelper.getWritableDatabase();
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

        final String prefSelection =
                TrayContract.Preferences.Columns.MODULE + " = ?"
                        + "AND " + TrayContract.Preferences.Columns.KEY + " = ?";
        final String[] prefSelectionArgs = {
                values.getAsString(TrayContract.Preferences.Columns.MODULE),
                values.getAsString(TrayContract.Preferences.Columns.KEY)
        };

        final String[] excludeForUpdate = {TrayContract.Preferences.Columns.CREATED};

        final int status = insertOrUpdate(getWritableDatabase(), getTable(uri),
                prefSelection, prefSelectionArgs, values, excludeForUpdate);

        if (status >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;

        } else if (status == -1) {
            //throw new SQLiteException("An error occurred while saving preference.");
            Log.w(TAG, "Couldn't update or insert data. Uri: " + uri);
        } else if (status == -2) {
            Log.w(TAG, "Data is already inserted, no need to insert here");
        } else {
            Log.w(TAG, "unknown SQLite error");
        }
        return null;
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

        sURIMatcher.addURI(authority,
                TrayContract.InternalPreferences.BASE_PATH,
                INTERNAL_ALL_PREFERENCE);

        // INTERNAL_BASE/module
        sURIMatcher.addURI(authority,
                TrayContract.InternalPreferences.BASE_PATH + "/*",
                INTERNAL_MODULE_PREFERENCE);

        // INTERNAL_BASE/module/key
        sURIMatcher.addURI(authority,
                TrayContract.InternalPreferences.BASE_PATH + "/*/*",
                INTERNAL_SINGLE_PREFERENCE);
    }

    public int insertOrUpdate(final SQLiteDatabase writableDatabase, final String table,
            final String prefSelection, final String[] prefSelectionArgs,
            final ContentValues values, final String[] excludeForUpdate) {
        return SqliteHelper
                .insertOrUpdate(writableDatabase, table, prefSelection, prefSelectionArgs, values,
                        excludeForUpdate);
    }

}
