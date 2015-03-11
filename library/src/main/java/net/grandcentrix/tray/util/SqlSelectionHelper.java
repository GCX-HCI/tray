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

package net.grandcentrix.tray.util;

import android.net.Uri;
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
public class SqlSelectionHelper {

    /**
     * combines selection a and selection b to (a) AND (b). handles all cases if a or b are
     * <code>null</code> or <code>""</code>
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
     * <p/>
     * <code>[a, b] , [c] -> [a, b ,c]</code>
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
     */
    public static String[] extendSelectionArgs(@Nullable String selectionArg,
            @Nullable String[] newSelectionArgs) {
        if (TextUtils.isEmpty(selectionArg)) {
            return newSelectionArgs;
        }
        return extendSelectionArgs(new String[]{selectionArg}, newSelectionArgs);
    }

}
