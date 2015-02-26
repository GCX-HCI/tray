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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jannisveerkamp on 17.09.14.
 */
/*package*/ class TrayDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TrayPreferences";

    public static final String INTERNAL_TABLE_NAME = "TrayInternal";

    public static final String DATABASE_NAME = "tray.db";

    public static final String KEY = "KEY";

    public static final String VALUE = "VALUE";

    public static final String MODULE = "MODULE";

    public static final String CREATED = "CREATED";

    public static final String UPDATED = "UPDATED";

    //TODO UPGRADE to v2
    public static final String IMPORTED_KEY = "IMPORTED_KEY";

    // TODO add additional meta fields:
    // public static final String APP_VERSION_CODE = "APP_VERSION_CODE";

    public static final String INTERNAL_PREFERENCES_CREATE = "CREATE TABLE "
            + INTERNAL_TABLE_NAME + " ( "
            + BaseColumns._ID + " INTEGER PRIMARY KEY, "
            + KEY + " TEXT NOT NULL, "
            + VALUE + " TEXT, "
            + MODULE + " TEXT, "
            + CREATED + " INT DEFAULT 0, "  // Date
            + UPDATED + " INT DEFAULT 0, "    // Date
            + IMPORTED_KEY + " TEXT, "
            + "UNIQUE ("
            + MODULE + ", "
            + KEY
            + ")"
            + ");";

    public static final String PREFERENCES_CREATE = "CREATE TABLE "
            + TABLE_NAME + " ( "
            + BaseColumns._ID + " INTEGER PRIMARY KEY, "
            + KEY + " TEXT NOT NULL, "
            + VALUE + " TEXT, "
            + MODULE + " TEXT, "
            + CREATED + " INT DEFAULT 0, "  // Date
            + UPDATED + " INT DEFAULT 0, "    // Date
            + IMPORTED_KEY + " TEXT, "
            + "UNIQUE ("
            + MODULE + ", "
            + KEY
            + ")"
            + ");";

    private static final int DATABASE_VERSION = 1;

    public TrayDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion,
            final int newVersion) {
        throw new IllegalStateException("Can't upgrade database from version " +
                oldVersion + " to " + newVersion + ", not implemented.");
    }

    private void createTables(final SQLiteDatabase db) {
        db.execSQL(PREFERENCES_CREATE);
        db.execSQL(INTERNAL_PREFERENCES_CREATE);
    }
}
