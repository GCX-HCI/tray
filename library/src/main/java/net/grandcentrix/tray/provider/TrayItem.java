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

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jannisveerkamp on 17.09.14.
 */
public class TrayItem {

    private final Date mCreated;

    private final String mImportedKey;

    private final String mKey;

    private final String mModule;

    private final Date mUpdated;

    private final String mValue;

    /*package*/ TrayItem(final Cursor cursor) {
        mKey = cursor.getString(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.KEY));
        mValue = cursor.getString(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.VALUE));
        mModule = cursor.getString(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.MODULE));
        mCreated = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.CREATED)));
        mUpdated = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.UPDATED)));
        mImportedKey = cursor.getString(cursor.getColumnIndexOrThrow(
                TrayContract.Preferences.Columns.IMPORTED_KEY));
    }

    public TrayItem(final Date created, final String key, final String module,
            final Date updated, final String value, final String importedKey) {
        mCreated = created;
        mKey = key;
        mModule = module;
        mUpdated = updated;
        mValue = value;
        mImportedKey = importedKey;
    }

    public Date created() {
        return mCreated;
    }

    public String migratedKey() {
        return mImportedKey;
    }

    public String key() {
        return mKey;
    }

    public String module() {
        return mModule;
    }

    @Override
    public String toString() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append("key: ")
                .append(mKey)
                .append(", value: ")
                .append(mValue)
                .append(", module: ")
                .append(mModule)
                .append(", created: ")
                .append(sf.format(mCreated))
                .append(", updated: ")
                .append(sf.format(mUpdated))
                .append(", migratedKey: ")
                .append(sf.format(mImportedKey))
                .toString();
    }

    public Date updateTime() {
        return mUpdated;
    }

    public String value() {
        return mValue;
    }
}
