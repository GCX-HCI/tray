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

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A helper for interactions with a {@link android.database.sqlite.SQLiteDatabase} when using the
 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)} method.
 *
 * @author Pascal Welsch
 */
public class SqliteHelper {

    /**
     * combines selection a and selection b to (a) AND (b). handles all cases if a or b are
     * <code>null</code> or <code>""</code>
     *
     * @param selection      base selection
     * @param selectionToAdd this selection will connected with AND
     * @return combined selection
     */
    public static String extendSelection(@Nullable String selection,
            @Nullable String selectionToAdd) {
        // Add to selection or set as selection if selection is empty
        if (!TextUtils.isEmpty(selection)) {
            if (TextUtils.isEmpty(selectionToAdd)) {
                return selection;
            }
            //noinspection StringBufferReplaceableByString
            StringBuilder selectionToAddBuilder = new StringBuilder();
            selection = selectionToAddBuilder
                    .append("(")
                    .append(selection)
                    .append(") AND (")
                    .append(selectionToAdd)
                    .append(")")
                    .toString();
        } else {
            selection = selectionToAdd;
        }
        return selection;
    }

    /**
     * alternative arguments for {@link #extendSelectionArgs(String[], List)}
     *
     * @param selectionArgs    base selection args
     * @param newSelectionArgs will be concatenated
     * @return concatenated selection args
     */
    public static String[] extendSelectionArgs(@Nullable String[] selectionArgs,
            @Nullable String[] newSelectionArgs) {
        if (newSelectionArgs == null) {
            return selectionArgs;
        }
        return extendSelectionArgs(selectionArgs, Arrays.asList(newSelectionArgs));
    }

    /**
     * combines the selectionArgs analog to the selection itself with {@link
     * #extendSelection(String, String)}.
     * <p>
     * <code>[a, b] , [c] -&gt; [a, b ,c]</code>
     *
     * @param selectionArgs    base selection args
     * @param newSelectionArgs will be concatenated
     * @return concatenated selection args
     */
    public static String[] extendSelectionArgs(@Nullable String[] selectionArgs,
            @Nullable List<String> newSelectionArgs) {
        if (newSelectionArgs == null) {
            return selectionArgs;
        }
        if (selectionArgs != null) {
            List<String> selectionArgList = new ArrayList<>(Arrays.asList(selectionArgs));
            selectionArgList.addAll(newSelectionArgs);
            selectionArgs = selectionArgList.toArray(new String[selectionArgList.size()]);
        } else {
            selectionArgs = newSelectionArgs.toArray(new String[newSelectionArgs.size()]);
        }
        return selectionArgs;
    }

    /**
     * alternative arguments for {@link #extendSelectionArgs(String[], List)}
     *
     * @param selectionArg     base selection arg
     * @param newSelectionArgs will be concatenated
     * @return concatenated selection args
     */
    public static String[] extendSelectionArgs(@Nullable String selectionArg,
            @Nullable String[] newSelectionArgs) {
        if (TextUtils.isEmpty(selectionArg)) {
            return newSelectionArgs;
        }
        return extendSelectionArgs(new String[]{selectionArg}, newSelectionArgs);
    }

    /**
     * Tries to insert the values. If it fails because the item already exists it tries to update
     * the item.
     *
     * @param sqlDb                  database to work with. has to be writable
     * @param table                  the table to insert
     * @param selection              selection to detect a already inserted item
     * @param selectionArgs          keys of the contentValues. there values will be used as the
     *                               selectionArgs for the param selection
     * @param values                 the values to insert
     * @param excludeFieldsForUpdate contentValues keys which should be deleted before the update
     * @return 1 for insert, 0 for update and -1 if something goes wrong
     */
    public static int insertOrUpdate(@Nullable SQLiteDatabase sqlDb, String table,
            @Nullable String selection, String[] selectionArgs, @NonNull final ContentValues values,
            @Nullable final String[] excludeFieldsForUpdate) {
        if (sqlDb == null) {
            return -1;
        }

        final long items = DatabaseUtils.queryNumEntries(sqlDb, table, selection, selectionArgs);

        if (items == 0) {
            // insert, item doesn't exist
            final long row = sqlDb.insert(table, null, values);
            if (row == -1) {
                // unknown error
                return -1;
            }
            // success, inserted
            return 1;
        } else {
            // update existing item

            if (excludeFieldsForUpdate != null) {
                for (String excludeField : excludeFieldsForUpdate) {
                    values.remove(excludeField);
                }
            }

            sqlDb.update(table, values, selection, selectionArgs);

            // handling the update error is not needed. All possible errors are thrown by the
            // DatabaseUtils.queryNumEntries() (which uses the same params).
            // a wrong selection results only in an insert. update will never called then.
            return 0;
        }
    }
}
