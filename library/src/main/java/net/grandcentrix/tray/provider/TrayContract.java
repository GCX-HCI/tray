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

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Contract defining the data in the {@link TrayContentProvider}. Use {@link TrayProviderHelper} to
 * access them.
 * <p>
 * Created by jannisveerkamp on 17.09.14.
 */
class TrayContract {

    /**
     * default preferences
     */
    public interface Preferences {

        interface Columns extends BaseColumns {

            String ID = BaseColumns._ID;

            String KEY = TrayDBHelper.KEY;

            String VALUE = TrayDBHelper.VALUE;

            String MODULE = TrayDBHelper.MODULE;

            String CREATED = TrayDBHelper.CREATED; // DATE

            String UPDATED = TrayDBHelper.UPDATED; // DATE

            String MIGRATED_KEY = TrayDBHelper.MIGRATED_KEY;
        }

        String BASE_PATH = "preferences";
    }

    /**
     * trays internal preferences to hold things like the version number
     */
    public interface InternalPreferences extends Preferences {

        String BASE_PATH = "internal_preferences";
    }

    private static String sTestAuthority;

    @NonNull
    public static Uri generateContentUri(@NonNull final Context context) {
        return generateContentUri(context, Preferences.BASE_PATH);
    }

    /**
     * use this only for tests and not in production
     *
     * @param authority the new authority for Tray
     * @see TrayContentProvider#setAuthority(String)
     */
    public static void setAuthority(final String authority) {
        sTestAuthority = authority;
    }

    @NonNull
    /*package*/ static Uri generateInternalContentUri(@NonNull final Context context) {
        return generateContentUri(context, InternalPreferences.BASE_PATH);
    }

    private static void checkForDefaultAuthority(final @NonNull String authority) {
        if (authority.equals("com.example.preferences")) {
            Log.e("Tray", "Tray authority not defined\n"
                    + "#########################################\n"
                    + "#########################################\n"
                    + "#########################################\n"
                    + "Don't use the default authority from the tray library. Two apps with the same tray authority can't be installed on the same device!\n\n"
                    + "Override the authority adding a string resource to your project with key: `tray__authority`.\n"
                    + "The simples way is to add it via gradle:\n"
                    + ".\n"
                    + "defaultConfig {\n"
                    + "resValue \"string\", \"tray__authority\", \"<your.app.package.tray>\"\n"
                    + "}\n"
                    + "#########################################\n"
                    + "#########################################\n"
                    + "#########################################");
        }
    }

    @NonNull
    private static Uri generateContentUri(@NonNull final Context context, final String basepath) {

        final String authority = getAuthority(context);
        checkForDefaultAuthority(authority);
        final Uri authorityUri = Uri.parse("content://" + authority);
        //noinspection UnnecessaryLocalVariable
        final Uri contentUri = Uri.withAppendedPath(authorityUri, basepath);
        return contentUri;
    }

    @NonNull
    private static String getAuthority(@NonNull final Context context) {
        return TextUtils.isEmpty(sTestAuthority) ?
                context.getString(R.string.tray__authority) :
                sTestAuthority;
    }
}
