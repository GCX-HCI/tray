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

import net.grandcentrix.tray.mock.MockProvider;

import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TrayProviderTest extends TrayProviderTestCase {

    private TrayProviderHelper mProviderHelper;

    public TrayProvider startupProvider() {
        final TrayProvider provider = new TrayProvider();
        provider.attachInfo(getProviderMockContext(), new ProviderInfo());
        assertTrue(provider.onCreate());
        assertTrue(provider.mDbHelper.getWritableDatabase().isOpen());
        return provider;
    }

    public void testDelete() throws Exception {

        final Uri[] workingUris = {
                mProviderHelper.getUri(),
                mProviderHelper.getUri("module"),
                mProviderHelper.getUri("module", "key"),
                mProviderHelper.getInternalUri(),
                mProviderHelper.getInternalUri("module"),
                mProviderHelper.getInternalUri("module", "key")
        };
        for (Uri uri : workingUris) {
            getProviderMockContext().getContentResolver().delete(uri, null, null);
        }

        final Uri badUri = Uri
                .withAppendedPath(Uri.parse("content://" + MockProvider.AUTHORITY), "something");
        try {
            getProviderMockContext().getContentResolver().delete(badUri, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not supported"));
        }
    }

    public void testGetTable() throws Exception {
        final TrayProvider trayProvider = new TrayProvider();
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getUri()));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getUri("module")));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getUri("module", "key")));

        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getInternalUri()));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getInternalUri("module")));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(mProviderHelper.getInternalUri("module", "key")));

        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(Uri.parse("http://www.google.com")));

        assertNull(trayProvider.getTable(null));

    }

    public void testGetType() throws Exception {
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mProviderHelper.getUri()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mProviderHelper.getUri("module", "key")));

        assertNull(getProviderMockContext().getContentResolver()
                .getType(mProviderHelper.getInternalUri()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mProviderHelper.getInternalUri("module", "key")));
    }

    public void testInsert() throws Exception {
        final ContentValues fakeValues = new ContentValues();

        final Uri[] workingUris = {
                mProviderHelper.getUri("module", "key"),
                mProviderHelper.getInternalUri("module", "key")
        };
        for (Uri uri : workingUris) {
            getProviderMockContext().getContentResolver().insert(uri, fakeValues);
        }

        final Uri[] notWorkingUris = {
                mProviderHelper.getUri(),
                mProviderHelper.getUri("module"),
                mProviderHelper.getInternalUri(),
                mProviderHelper.getInternalUri("module"),
        };

        for (Uri badUri : notWorkingUris) {
            try {
                getProviderMockContext().getContentResolver().insert(badUri, fakeValues);
                fail("inserted Uri: " + badUri);
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("not supported"));
            }
        }
    }

    public void testInsertFailed() throws Exception {

        assertInsertUriEqualsNullForUpdateOrInsertError(-1);
        assertInsertUriEqualsNullForUpdateOrInsertError(-2);
        assertInsertUriEqualsNullForUpdateOrInsertError(-1000);


        /*doReturn(null).when(spy).getWritableDatabase();
        final String table = trayProvider.getTable(uri1);
        doReturn(-1).when(spy).insertOrUpdate(null, table, prefSelection, prefSelectionArgs, values,
                excludeForUpdate);

        assertNull(spy.insert(uri1, values));

        final Uri uri = spy
                .insert(mProviderHelper.getUri("module", "key"), values);
        assertEquals(null, uri);*/

    }

    public void testQueryUnregisteredProvider() throws Exception {

        final TrayProvider provider = spy(new TrayProvider());
        provider.mDbHelper = spy(new TrayDBHelper(getProviderMockContext()));
        when(provider.mDbHelper.getReadableDatabase()).thenReturn(null);

        // null as table forces the internal SQLiteQueryBuilder to return null on a query
        // in reality this may happen for many other hard sql or database errors
        final Uri uri = mProviderHelper.getUri();
        when(provider.getTable(uri)).thenReturn(null);

        final Cursor cursor = provider.query(uri, null, null, null, null);
        assertNull(cursor);
    }

    public void testQueryWrongUri() throws Exception {
        final Uri googleUri = Uri.parse("http://www.google.com");
        final TrayProvider trayProvider = new TrayProvider();
        trayProvider.attachInfo(getProviderMockContext(), new ProviderInfo());
        try {
            trayProvider.query(googleUri, null, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not supported"));
        }
    }

    public void testShutdown() throws Exception {
        final TrayProvider provider = startupProvider();
        final SQLiteDatabase writableDatabase = provider.mDbHelper.getWritableDatabase();
        assertTrue(writableDatabase.isOpen());
        provider.shutdown();
        assertFalse(writableDatabase.isOpen());
    }

    public void testStartup() throws Exception {
        startupProvider();
    }

    public void testUpdate() throws Exception {
        final TrayProvider provider = spy(new TrayProvider());
        provider.mDbHelper = spy(new TrayDBHelper(getProviderMockContext()));
        when(provider.mDbHelper.getReadableDatabase()).thenReturn(null);
        try {
            provider.update(null, null, null, null);
            fail("implemented but no test written");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("not implemented"));
        }
    }

    void assertInsertUriEqualsNullForUpdateOrInsertError(final int errorCode) {
        final TrayProvider trayProvider = new TrayProvider();
        final TrayProvider spy = spy(trayProvider);

        doReturn(null).when(spy).getWritableDatabase();

        final Uri mockInsertUri = mProviderHelper.getUri("module", "key");

        doReturn(errorCode).when(spy)
                .insertOrUpdate(any(SQLiteDatabase.class), anyString(), anyString(),
                        any(String[].class), any(ContentValues.class), any(String[].class));
        final Uri insert = spy.insert(mockInsertUri, new ContentValues());
        assertNull(insert);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        mProviderHelper = new TrayProviderHelper(getProviderMockContext());
    }
}