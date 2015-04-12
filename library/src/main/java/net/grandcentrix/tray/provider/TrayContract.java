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

/**
 * Created by jannisveerkamp on 17.09.14.
 */
public class TrayContract {

    public interface Preferences {

        interface Columns extends BaseColumns {

            String ID = BaseColumns._ID;

            String KEY = TrayDBHelper.KEY;

            String VALUE = TrayDBHelper.VALUE;

            String MODULE = TrayDBHelper.MODULE;

            String CREATED = TrayDBHelper.CREATED; // DATE

            String UPDATED = TrayDBHelper.UPDATED; // DATE
        }

        String BASE_PATH = "preferences";
    }

    private static String sTestAuthority;

    @NonNull
    public static Uri generateContentUri(@NonNull final Context context) {

        final String authority = getAuthority(context);
        final Uri authorityUri = Uri.parse("content://" + authority);
        final Uri contentUri = Uri.withAppendedPath(authorityUri, Preferences.BASE_PATH);

        return contentUri;
    }

    /**
     * use this only for tests and not in production
     *
     * @see TrayProvider#setAuthority(String)
     */
    public static void setAuthority(final String authority) {
        sTestAuthority = authority;
    }

    @NonNull
    private static String getAuthority(@NonNull final Context context) {
        return TextUtils.isEmpty(sTestAuthority) ?
                context.getString(R.string.tray__authority) :
                sTestAuthority;
    }
}
