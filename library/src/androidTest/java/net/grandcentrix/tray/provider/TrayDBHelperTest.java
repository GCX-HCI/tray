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
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;

public class TrayDBHelperTest extends AndroidTestCase {

    private static final String TEST_DATABASE_NAME = "upgradeDbTest.db";

    public void testCreateVersion1() throws Exception {
        final TrayDBHelper trayDBHelper = initDb(1, false);
        assertV1Integrity(trayDBHelper);
    }

    public void testCreateVersion2() throws Exception {
        final TrayDBHelper trayDBHelper = initDb(2, false);
        assertV2Integrity(trayDBHelper);
    }

    public void testInstantiation() throws Exception {
        new TrayDBHelper(getContext());
    }

    public void testUpgradeFrom1to2() throws Exception {
        initDb(1);
        final TrayDBHelper trayDBHelper = initDb(2, false);
        assertV2Integrity(trayDBHelper);
    }

    public void testUpgradeNotImplemented() throws Exception {
        final TrayDBHelper trayDBHelper = initDb(1, false);
        try {
            trayDBHelper.onUpgrade(trayDBHelper.getWritableDatabase(), 1,
                    TrayDBHelper.DATABASE_VERSION + 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("version"));
        }
    }

    public void testUpgradeWithWrongOldVersion() throws Exception {
        final TrayDBHelper trayDBHelper = initDb(1, false);
        try {
            trayDBHelper.onUpgrade(trayDBHelper.getWritableDatabase(), 0,
                    TrayDBHelper.DATABASE_VERSION);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("oldVersion"));
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase(TEST_DATABASE_NAME);
    }

    private void assertV1Integrity(final TrayDBHelper trayDBHelper) {
        final SQLiteDatabase db = trayDBHelper.getReadableDatabase();
        {// check tray table
            final Cursor cursor = db
                    .query(TrayDBHelper.TABLE_NAME, null, null, null, null, null, null);
            assertNotNull(cursor);
            final List<String> columnNames = Arrays.asList(cursor.getColumnNames());
            cursor.close();
            assertEquals(6, columnNames.size());
            assertTrue(columnNames.contains(BaseColumns._ID));
            assertTrue(columnNames.contains(TrayDBHelper.MODULE));
            assertTrue(columnNames.contains(TrayDBHelper.KEY));
            assertTrue(columnNames.contains(TrayDBHelper.VALUE));
            assertTrue(columnNames.contains(TrayDBHelper.CREATED));
            assertTrue(columnNames.contains(TrayDBHelper.UPDATED));
        }
        db.close();
    }

    private void assertV2Integrity(final TrayDBHelper trayDBHelper) {
        final SQLiteDatabase db = trayDBHelper.getReadableDatabase();
        {// check added MIGRATED_KEY column
            final Cursor cursor = db
                    .query(TrayDBHelper.TABLE_NAME, null, null, null, null, null, null);
            assertNotNull(cursor);
            final List<String> columnNames = Arrays.asList(cursor.getColumnNames());
            cursor.close();
            assertEquals(7, columnNames.size());
            assertTrue(columnNames.contains(BaseColumns._ID));
            assertTrue(columnNames.contains(TrayDBHelper.MODULE));
            assertTrue(columnNames.contains(TrayDBHelper.KEY));
            assertTrue(columnNames.contains(TrayDBHelper.VALUE));
            assertTrue(columnNames.contains(TrayDBHelper.CREATED));
            assertTrue(columnNames.contains(TrayDBHelper.UPDATED));
            assertTrue(columnNames.contains(TrayDBHelper.MIGRATED_KEY));
        }

        {// check added internal tray table
            final Cursor cursor = db
                    .query(TrayDBHelper.INTERNAL_TABLE_NAME, null, null, null, null, null, null);
            assertNotNull(cursor);
            final List<String> columnNames = Arrays.asList(cursor.getColumnNames());
            cursor.close();
            assertEquals(7, columnNames.size());
            assertTrue(columnNames.contains(BaseColumns._ID));
            assertTrue(columnNames.contains(TrayDBHelper.MODULE));
            assertTrue(columnNames.contains(TrayDBHelper.KEY));
            assertTrue(columnNames.contains(TrayDBHelper.VALUE));
            assertTrue(columnNames.contains(TrayDBHelper.CREATED));
            assertTrue(columnNames.contains(TrayDBHelper.UPDATED));
            assertTrue(columnNames.contains(TrayDBHelper.MIGRATED_KEY));
        }
        db.close();
    }

    private void initDb(final int version) {
        initDb(version, true);
    }

    private TrayDBHelper initDb(final int version, final boolean closeDb) {
        TrayDBHelper dbHelper = new TrayDBHelper(getContext(),
                TEST_DATABASE_NAME, true, version);
        dbHelper.getReadableDatabase();
        if (closeDb) {
            dbHelper.close();
        }
        return dbHelper;
    }
}