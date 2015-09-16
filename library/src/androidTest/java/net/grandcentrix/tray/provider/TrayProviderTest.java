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

import net.grandcentrix.tray.core.TrayStorage;

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

    private TrayUri mTrayUri;

    public TrayContentProvider startupProvider() {
        final TrayContentProvider provider = new TrayContentProvider();
        provider.attachInfo(getProviderMockContext(), new ProviderInfo());
        assertTrue(provider.onCreate());
        assertTrue(provider.mUserDbHelper.getWritableDatabase().isOpen());
        return provider;
    }

    public void testDelete() throws Exception {

        final Uri[] workingUris = {
                mTrayUri.get(),
                mTrayUri.builder().setModule("module").build(),
                mTrayUri.builder().setModule("module").setKey("key").build(),
                mTrayUri.getInternal(),
                mTrayUri.builder().setInternal(true).setModule("module").build(),
                mTrayUri.builder().setInternal(true).setModule("module").setKey("key").build()
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
        final TrayContentProvider trayContentProvider = new TrayContentProvider();
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayContentProvider.getTable(mTrayUri.get()));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayContentProvider.getTable(mTrayUri.builder().setModule("module").build()));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayContentProvider
                        .getTable(mTrayUri.builder().setModule("module").setKey("key").build()));

        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayContentProvider.getTable(mTrayUri.getInternal()));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayContentProvider.getTable(
                        mTrayUri.builder().setInternal(true).setModule("module").build()));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayContentProvider.getTable(
                        mTrayUri.builder().setInternal(true).setModule("module").setKey("key")
                                .build()));

        assertEquals(TrayDBHelper.TABLE_NAME,
                trayContentProvider.getTable(Uri.parse("http://www.google.com")));

        assertNull(trayContentProvider.getTable(null));

    }

    public void testGetType() throws Exception {
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mTrayUri.get()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mTrayUri.builder().setModule("module").setKey("key").build()));

        assertNull(getProviderMockContext().getContentResolver()
                .getType(mTrayUri.getInternal()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(mTrayUri.builder().setInternal(true).setModule("module").setKey("key")
                        .build()));
    }

    public void testInsert() throws Exception {
        final ContentValues fakeValues = new ContentValues();

        final Uri[] workingUris = {
                mTrayUri.builder().setModule("module").setKey("key").build(),
                mTrayUri.builder().setInternal(true).setModule("module").setKey("key").build()
        };
        for (Uri uri : workingUris) {
            getProviderMockContext().getContentResolver().insert(uri, fakeValues);
        }

        final Uri[] notWorkingUris = {
                mTrayUri.get(),
                mTrayUri.builder().setModule("module").build(),
                mTrayUri.getInternal(),
                mTrayUri.builder().setInternal(true).setModule("module").build(),
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
                .insert(mProviderHelper.get("module", "key"), values);
        assertEquals(null, uri);*/

    }

    public void testQueryUnregisteredProvider() throws Exception {

        final TrayContentProvider provider = spy(new TrayContentProvider());
        provider.mUserDbHelper = spy(new TrayDBHelper(getProviderMockContext()));
        provider.mDeviceDbHelper = spy(new TrayDBHelper(getProviderMockContext()));
        when(provider.mUserDbHelper.getReadableDatabase()).thenReturn(null);
        when(provider.mDeviceDbHelper.getReadableDatabase()).thenReturn(null);

        // null as table forces the internal SQLiteQueryBuilder to return null on a query
        // in reality this may happen for many other hard sql or database errors
        final Uri uri = new TrayUri(getProviderMockContext()).builder()
                .setType(TrayStorage.Type.DEVICE) // unknown will not work
                .build();
        when(provider.getTable(uri)).thenReturn(null);

        final Cursor cursor = provider.query(uri, null, null, null, null);
        assertNull(cursor);
    }

    public void testQueryWrongUri() throws Exception {
        final Uri googleUri = Uri.parse("http://www.google.com");
        final TrayContentProvider trayContentProvider = new TrayContentProvider();
        trayContentProvider.attachInfo(getProviderMockContext(), new ProviderInfo());
        try {
            trayContentProvider.query(googleUri, null, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not supported"));
        }
    }

    public void testShouldBackup() throws Exception {
        final TrayContentProvider provider = new TrayContentProvider();
        final TrayUri trayUri = new TrayUri(getProviderMockContext());

        // only &backup=false -> false
        assertFalse(provider.shouldBackup(trayUri.builder()
                .setType(TrayStorage.Type.DEVICE)
                .build()));

        assertTrue(provider.shouldBackup(trayUri.get()));

        assertTrue(provider.shouldBackup(trayUri.builder()
                .setType(TrayStorage.Type.USER)
                .build()));

        assertTrue(provider.shouldBackup(trayUri.builder()
                .setType(TrayStorage.Type.UNDEFINED)
                .build()));
    }

    public void testShutdown() throws Exception {
        final TrayContentProvider provider = startupProvider();
        final SQLiteDatabase writableDatabase = provider.mUserDbHelper.getWritableDatabase();
        assertTrue(writableDatabase.isOpen());
        provider.shutdown();
        assertFalse(writableDatabase.isOpen());
    }

    public void testStartup() throws Exception {
        startupProvider();
    }

    public void testUpdate() throws Exception {
        final TrayContentProvider provider = spy(new TrayContentProvider());
        provider.mUserDbHelper = spy(new TrayDBHelper(getProviderMockContext()));
        when(provider.mUserDbHelper.getReadableDatabase()).thenReturn(null);
        try {
            provider.update(null, null, null, null);
            fail("implemented but no test written");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("not implemented"));
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        mProviderHelper = new TrayProviderHelper(getProviderMockContext());
        mTrayUri = new TrayUri(getProviderMockContext());
    }

    void assertInsertUriEqualsNullForUpdateOrInsertError(final int errorCode) {
        final TrayContentProvider trayContentProvider = new TrayContentProvider();
        final TrayContentProvider spy = spy(trayContentProvider);

        final Uri mockInsertUri = mTrayUri.builder().setModule("module").setKey("key").build();
        doReturn(null).when(spy).getWritableDatabase(mockInsertUri);

        doReturn(errorCode).when(spy)
                .insertOrUpdate(any(SQLiteDatabase.class), anyString(), anyString(),
                        any(String[].class), any(ContentValues.class), any(String[].class));
        final Uri insert = spy.insert(mockInsertUri, new ContentValues());
        assertNull(insert);
    }
}