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


import net.grandcentrix.tray.core.TrayLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

/**
 * Helper to access the two internal databases where all tray data are saved
 * <p>
 * Created by jannisveerkamp on 17.09.14.
 */
@VisibleForTesting
public class TrayDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TrayPreferences";

    public static final String INTERNAL_TABLE_NAME = "TrayInternal";

    public static final String DATABASE_NAME = "tray.db";

    public static final String DATABASE_NAME_NO_BACKUP = "tray_backup_excluded.db";

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

    private final int mCreateVersion;

    private final boolean mWithBackup;

    /*package*/ TrayDBHelper(Context context, String databaseName, final boolean withBackup,
            int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        mWithBackup = withBackup;
        mCreateVersion = databaseVersion;
    }

    public TrayDBHelper(Context context) {
        this(context, true);
    }

    public TrayDBHelper(Context context, final boolean withBackup) {
        super(context, withBackup ? DATABASE_NAME : DATABASE_NAME_NO_BACKUP, null,
                DATABASE_VERSION);
        mWithBackup = withBackup;
        mCreateVersion = DATABASE_VERSION;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        TrayLog.v(logTag() + "onCreate with version " + mCreateVersion);

        createV1(db);
        TrayLog.v(logTag() + "created database version 1");

        if (mCreateVersion > 1) {
            onUpgrade(db, 1, mCreateVersion);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
            final int newVersion) {
        TrayLog.v(logTag() + "upgrading Database from version " + oldVersion
                + " to version " + newVersion);

        // increase the version here after the upgrade was implemented
        if (newVersion > 2) {
            throw new IllegalStateException(
                    "onUpgrade doesn't support the upgrade to version " + newVersion);
        }

        switch (oldVersion) {
            case 1:
                upgradeToV2(db);
                TrayLog.v(logTag() + "upgraded Database to version 2");
                break;
            default:
                throw new IllegalArgumentException(
                        "onUpgrade() with oldVersion <= 0 is useless");
        }
    }

    private void createV1(final SQLiteDatabase db) {
        db.execSQL(V1_PREFERENCES_CREATE);
    }

    @NonNull
    private String logTag() {
        return "tray internal db (" + (mWithBackup ? "backup" : "no backup") + "): ";
    }

    private void upgradeToV2(final SQLiteDatabase db) {
        db.execSQL(V2_ALTER_PREFERENCES_TABLE);
        db.execSQL(V2_CREATE_INTERNAL_TRAY_TABLE);
    }
}
