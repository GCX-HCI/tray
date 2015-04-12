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
import android.util.Log;

/**
 * Created by jannisveerkamp on 17.09.14.
 */
public class TrayDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TrayPreferences";

    public static final String INTERNAL_TABLE_NAME = "TrayInternal";

    public static final String DATABASE_NAME = "tray.db";

    public static final String KEY = "KEY";

    public static final String VALUE = "VALUE";

    public static final String MODULE = "MODULE";

    public static final String CREATED = "CREATED";

    public static final String UPDATED = "UPDATED";

    public static final String MIGRATED_KEY = "MIGRATED_KEY";

    // TODO add additional meta fields:
    // public static final String APP_VERSION_CODE = "APP_VERSION_CODE";

    public static final String V1_PREFERENCES_CREATE = "CREATE TABLE "
            + TABLE_NAME + " ( "
            + BaseColumns._ID + " INTEGER PRIMARY KEY, "
            + KEY + " TEXT NOT NULL, "
            + VALUE + " TEXT, "
            + MODULE + " TEXT, "
            + CREATED + " INT DEFAULT 0, "  // Date
            + UPDATED + " INT DEFAULT 0, "    // Date
            + "UNIQUE ("
            + MODULE + ", "
            + KEY
            + ")"
            + ");";

    public static final String V2_ALTER_PREFERENCES_TABLE = "ALTER TABLE " + TABLE_NAME
            + " ADD COLUMN " + MIGRATED_KEY + " TEXT";

    public static final String V2_CREATE_INTERNAL_TRAY_TABLE = "CREATE TABLE "
            + INTERNAL_TABLE_NAME + " ( "
            + BaseColumns._ID + " INTEGER PRIMARY KEY, "
            + KEY + " TEXT NOT NULL, "
            + VALUE + " TEXT, "
            + MODULE + " TEXT, "
            + CREATED + " INT DEFAULT 0, "  // Date
            + UPDATED + " INT DEFAULT 0, "    // Date
            + MIGRATED_KEY + " TEXT, "
            + "UNIQUE ("
            + MODULE + ", "
            + KEY
            + ")"
            + ");";

    /*package*/ static final int DATABASE_VERSION = 2;

    private static final String TAG = TrayDBHelper.class.getSimpleName();

    private final int mCreateVersion;

    /*package*/ TrayDBHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        mCreateVersion = databaseVersion;
    }

    public TrayDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCreateVersion = DATABASE_VERSION;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.v(TAG, "onCreate with version " + mCreateVersion);

        createV1(db);
        Log.v(TAG, "created database version 1");

        if (mCreateVersion > 1) {
            onUpgrade(db, 1, mCreateVersion);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
            final int newVersion) {
        Log.v(TAG, "upgrading Database from version " + oldVersion + " to version " + newVersion);

        // increase the version here after the upgrade was implemented
        if (newVersion > 2) {
            throw new IllegalStateException(
                    "onUpgrade doesn't support the upgrade to version " + newVersion);
        }

        switch (oldVersion) {
            case 1:
                upgradeToV2(db);
                Log.v(TAG, "upgraded Database to version 2");
                break;
            default:
                throw new IllegalArgumentException(
                        "onUpgrade() with oldVersion <= 0 is useless");
        }
    }

    private void createV1(final SQLiteDatabase db) {
        db.execSQL(V1_PREFERENCES_CREATE);
    }

    private void upgradeToV2(final SQLiteDatabase db) {
        db.execSQL(V2_ALTER_PREFERENCES_TABLE);
        db.execSQL(V2_CREATE_INTERNAL_TRAY_TABLE);
    }
}
