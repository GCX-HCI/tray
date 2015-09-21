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

package net.grandcentrix.tray.core;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Immutable item from the Tray storage.
 *
 * Created by jannisveerkamp on 17.09.14.
 */
public class TrayItem {

    private final Date mCreated;

    private final String mKey;

    private final String mMigratedKey;

    private final String mModule;

    private final Date mUpdated;

    private final String mValue;

    public TrayItem(final String module, final String key, final String migratedKey,
            final String value, final Date created, final Date updated) {
        mCreated = created;
        mKey = key;
        mModule = module;
        mUpdated = updated;
        mValue = value;
        mMigratedKey = migratedKey;
    }

    public Date created() {
        return mCreated;
    }

    public String key() {
        return mKey;
    }

    public String migratedKey() {
        return mMigratedKey;
    }

    public String module() {
        return mModule;
    }

    @Override
    public String toString() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.US);

        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append("{")
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
                .append(mMigratedKey)
                .append("}")
                .toString();
    }

    public Date updateTime() {
        return mUpdated;
    }

    @Nullable
    public String value() {
        return mValue;
    }
}
