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


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.grandcentrix.tray.provider.SqliteHelper.extendSelection;
import static net.grandcentrix.tray.provider.SqliteHelper.extendSelectionArgs;

public class SqliteHelperTest extends AndroidTestCase {

    public class MockDatabaseHelper extends SQLiteOpenHelper {

        public MockDatabaseHelper() {
            super(getContext(), "sqlitehelpertest", null, 1);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE test (_id INTEGER PRIMARY KEY, name TEXT, foo TEXT);");
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

        }
    }

    public void testExtendSelection() throws Exception {
        assertEquals("(a) AND (b)", extendSelection("a", "b"));
        assertEquals("b", extendSelection(null, "b"));
        assertEquals("b", extendSelection("", "b"));
        assertEquals("a", extendSelection("a", null));
        assertEquals("a", extendSelection("a", ""));
    }

    public void testExtendSelectionArgs() throws Exception {
        assertTrue(Arrays.equals(new String[]{"a", "b", "c"},
                extendSelectionArgs("a", new String[]{"b", "c"})));

        assertTrue(Arrays.equals(new String[]{"b", "c"},
                extendSelectionArgs("", new String[]{"b", "c"})));

        assertTrue(Arrays.equals(new String[]{"a"},
                extendSelectionArgs("a", new String[]{})));

        assertTrue(Arrays.equals(new String[]{"a"},
                extendSelectionArgs("a", null)));

        assertTrue(Arrays.equals(new String[]{"a"},
                extendSelectionArgs(null, Arrays.asList("a"))));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, new String[]{})));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, new ArrayList<String>())));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, (String[]) null)));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, (List<String>) null)));

        assertEquals(null, extendSelectionArgs((String[]) null, (String[]) null));

        assertEquals(null, extendSelectionArgs(null, (List<String>) null));
    }

    public void testInsertFails() throws Exception {
        final SQLiteDatabase db = new MockDatabaseHelper().getWritableDatabase();
        db.delete("test", null, null);
        final ContentValues values = new ContentValues();
        values.put("wrongColName", "foobar");
        final int result = SqliteHelper.insertOrUpdate(db, "test", null, null, values, null);
        assertEquals(-1, result);
    }

    public void testInsertOrUpdateWithNullDb() throws Exception {
        final int result = SqliteHelper.insertOrUpdate(null, null, null, null, null, null);
        assertEquals(-1, result);
    }

    public void testUpdateWithoutExclusion() throws Exception {
        final SQLiteDatabase db = new MockDatabaseHelper().getReadableDatabase();
        db.delete("test", null, null);

        // insert data. This forces the update
        final ContentValues dataValues = new ContentValues();
        dataValues.put("name", "has data");
        db.insert("test", null, dataValues);

        // update without exclusion
        final ContentValues values = new ContentValues();
        values.put("name", "data");
        final int result = SqliteHelper
                .insertOrUpdate(db, "test", null, null, values, null);
        assertEquals(0, result);
    }

    public void testUselessConstructorCall() throws Exception {
        // make sure the test coverage is at 100%
        new SqliteHelper();
    }
}