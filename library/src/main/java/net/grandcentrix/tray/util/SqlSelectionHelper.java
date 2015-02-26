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

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlSelectionHelper {

    public static String extendSelection(String selection, String selectionToAdd) {
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

    public static String[] extendSelectionArgs(String[] selectionArgs, String[] newSelectionArgs) {
        if (newSelectionArgs == null) {
            return selectionArgs;
        }
        return extendSelectionArgs(selectionArgs, Arrays.asList(newSelectionArgs));
    }

    public static String[] extendSelectionArgs(String[] selectionArgs,
            List<String> newSelectionArgs) {
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

    public static String[] extendSelectionArgs(String selectionArg, String[] newSelectionArgs) {
        if (TextUtils.isEmpty(selectionArg)) {
            return newSelectionArgs;
        }
        return extendSelectionArgs(new String[]{selectionArg}, newSelectionArgs);
    }

}
