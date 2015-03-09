package net.grandcentrix.tray.provider;

import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TrayProviderTest extends TrayProviderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
    }

    public TrayProvider startupProvider() {
        final TrayProvider provider = new TrayProvider();
        provider.attachInfo(getProviderMockContext(), new ProviderInfo());
        assertTrue(provider.onCreate());
        assertTrue(provider.mDbHelper.getWritableDatabase().isOpen());
        return provider;
    }

    public void testDelete() throws Exception {

        final Uri[] workingUris = {
                TrayProviderHelper.getUri(),
                TrayProviderHelper.getUri("module"),
                TrayProviderHelper.getUri("module", "key"),
                TrayProviderHelper.getInternalUri(),
                TrayProviderHelper.getInternalUri("module"),
                TrayProviderHelper.getInternalUri("module", "key")
        };
        for (Uri uri : workingUris) {
            getProviderMockContext().getContentResolver().delete(uri, null, null);
        }

        final Uri badUri = Uri
                .withAppendedPath(TrayProvider.AUTHORITY_URI, "something");
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
                trayProvider.getTable(TrayProviderHelper.getUri()));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(TrayProviderHelper.getUri("module")));
        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(TrayProviderHelper.getUri("module", "key")));

        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(TrayProviderHelper.getInternalUri()));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(TrayProviderHelper.getInternalUri("module")));
        assertEquals(TrayDBHelper.INTERNAL_TABLE_NAME,
                trayProvider.getTable(TrayProviderHelper.getInternalUri("module", "key")));

        assertEquals(TrayDBHelper.TABLE_NAME,
                trayProvider.getTable(Uri.parse("http://www.google.com")));

    }

    public void testGetType() throws Exception {
        assertNull(getProviderMockContext().getContentResolver()
                .getType(TrayProviderHelper.getUri()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(TrayProviderHelper.getUri("module", "key")));

        assertNull(getProviderMockContext().getContentResolver()
                .getType(TrayProviderHelper.getInternalUri()));
        assertNull(getProviderMockContext().getContentResolver()
                .getType(TrayProviderHelper.getInternalUri("module", "key")));
    }

    public void testInsert() throws Exception {
        final ContentValues fakeValues = new ContentValues();

        final Uri[] workingUris = {
                TrayProviderHelper.getUri("module", "key"),
                TrayProviderHelper.getInternalUri("module", "key")
        };
        for (Uri uri : workingUris) {
            getProviderMockContext().getContentResolver().insert(uri, fakeValues);
        }

        final Uri[] notWorkingUris = {
                TrayProviderHelper.getUri(),
                TrayProviderHelper.getUri("module"),
                TrayProviderHelper.getInternalUri(),
                TrayProviderHelper.getInternalUri("module"),
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

        final TrayProvider trayProvider = new TrayProvider();
        final TrayDBHelper mockDbHelper = mock(TrayDBHelper.class);
        when(mockDbHelper.getWritableDatabase()).thenReturn(null);
        trayProvider.mDbHelper = mockDbHelper;
        final ContentValues values = new ContentValues();
        assertNull(trayProvider
                .insert(TrayProviderHelper.getUri("module", "key"), values));

        final TrayProvider spy = spy(trayProvider);
        when(spy.insertOrUpdate(TrayDBHelper.TABLE_NAME, values))
                .thenThrow(new SQLiteException("some error"));

        trayProvider
                .insert(TrayProviderHelper.getUri("module", "key"), values);

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

    public void testSetAuthority() throws Exception {

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
        final TrayProvider provider = new TrayProvider();
        try {
            provider.update(null, null, null, null);
            fail("implemented but no test written");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("not implemented"));
        }
    }
}