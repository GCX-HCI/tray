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

import android.net.Uri;

/**
 * Created by pascalwelsch on 4/5/15.
 */
public class MockProvider {

    public static final String AUTHORITY = "net.grandcentrix.tray.test";

    public static Uri getDeviceContentUri() {
        return getContentUri(TrayContract.Preferences.BASE_PATH).buildUpon()
                .appendQueryParameter("backup", "false")
                .build();
    }

    public static Uri getInternalDeviceContentUri() {
        return getContentUri(TrayContract.InternalPreferences.BASE_PATH).buildUpon()
                .appendQueryParameter("backup", "false")
                .build();
    }

    public static Uri getInternalUserContentUri() {
        return getContentUri(TrayContract.InternalPreferences.BASE_PATH).buildUpon()
                .appendQueryParameter("backup", "true")
                .build();
    }

    public static Uri getUserContentUri() {
        return getContentUri(TrayContract.Preferences.BASE_PATH).buildUpon()
                .appendQueryParameter("backup", "true")
                .build();
    }

    private static Uri getContentUri(final String basepath) {
        final Uri authorityUri = Uri.parse("content://" + AUTHORITY);
        return Uri.withAppendedPath(authorityUri, basepath);
    }
}
