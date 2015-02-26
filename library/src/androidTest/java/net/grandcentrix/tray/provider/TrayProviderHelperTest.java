package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.TrayModulePreferences;
import net.grandcentrix.tray.TrayTest;
import net.grandcentrix.tray.accessor.TrayPreference;

import android.database.Cursor;
import android.net.Uri;
import android.test.mock.MockContentProvider;

import java.util.List;

public class TrayProviderHelperTest extends TrayTest {

    private TrayModulePreferences mPrefA;

    private TrayModulePreferences mPrefB;

    private TrayProviderHelper mProviderHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        mPrefA = new TrayModulePreferences(getProviderMockContext(), "a", 1) {

            @Override
            protected void onCreate(final int newVersion) {

            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {

            }
        };
        mPrefB = new TrayModulePreferences(getProviderMockContext(), "b", 1) {

            @Override
            protected void onCreate(final int newVersion) {

            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {

            }
        };
        mProviderHelper = new TrayProviderHelper(getProviderMockContext());
    }

    public void testClear() throws Exception {
        mPrefA.put("a", "a");
        mPrefB.put("b", "b");
        assertDatabaseSize(2);
        mProviderHelper.clear();
        assertDatabaseSize(0);

        mPrefA.put("a", "a");
        mPrefB.put("b", "b");
        assertDatabaseSize(2);
        //noinspection NullArgumentToVariableArgMethod
        mProviderHelper.clear((TrayPreference[]) null);
        assertDatabaseSize(2);

        mProviderHelper.clear(mPrefA);
        assertDatabaseSize(1);
        assertEquals(1, mPrefB.getAll().size());

        mProviderHelper.clear(mPrefB);
        assertDatabaseSize(0);
    }

    public void testClearBut() throws Exception {
        mPrefA.put("a", "a");
        mPrefB.put("b", "b");
        assertDatabaseSize(2);
        //noinspection NullArgumentToVariableArgMethod
        mProviderHelper.clearBut((TrayPreference[]) null);
        assertDatabaseSize(0);

        mPrefA.put("a", "a");
        mPrefB.put("b", "b");
        assertDatabaseSize(2);

        mProviderHelper.clearBut(mPrefA);
        assertDatabaseSize(1);
        assertEquals(1, mPrefA.getAll().size());

        mProviderHelper.clearBut(mPrefB);
        assertDatabaseSize(0);
    }

    public void testGetAll() throws Exception {
        mPrefA.put("a", "a");
        mPrefB.put("b", "b");
        final List<TrayItem> all = mProviderHelper.getAll();
        assertNotNull(all);
        assertEquals(2, all.size());
    }

    public void testGetInternalUri() throws Exception {
        assertEquals(TrayProvider.CONTENT_URI_INTERNAL.toString(),
                TrayProviderHelper.getInternalUri().toString());

        assertEquals(TrayProvider.CONTENT_URI_INTERNAL.toString() + "/test",
                TrayProviderHelper.getInternalUri("test").toString());

        assertEquals(TrayProvider.CONTENT_URI_INTERNAL.toString() + "/test/key",
                TrayProviderHelper.getInternalUri("test", "key").toString());
    }

    public void testGetUri() throws Exception {
        assertEquals(TrayProvider.CONTENT_URI.toString(),
                TrayProviderHelper.getUri().toString());

        assertEquals(TrayProvider.CONTENT_URI.toString() + "/test",
                TrayProviderHelper.getUri("test").toString());

        assertEquals(TrayProvider.CONTENT_URI.toString() + "/test/key",
                TrayProviderHelper.getUri("test", "key").toString());
    }

    public void testPersist() throws Exception {
        mProviderHelper.persist("a", "key", "value");
        assertEquals("value", mPrefA.getString("key", null));

        mProviderHelper.persist("b", "key", "value");
        assertEquals("value", mPrefA.getString("key", null));

        assertDatabaseSize(2);
    }

    public void testPersistInternal() throws Exception {
        mProviderHelper.persistInternal("a", "key", "value");
        final TrayItem aItem = mProviderHelper
                .queryProvider(TrayProviderHelper.getInternalUri("a", "key")).get(0);
        assertEquals("value", aItem.value());

        mProviderHelper.persistInternal("b", "key", "value");
        final TrayItem bItem = mProviderHelper
                .queryProvider(TrayProviderHelper.getInternalUri("a", "key")).get(0);
        assertEquals("value", bItem.value());

        //all internal
        assertDatabaseSize(0);
    }

    public void testQueryProvider() throws Exception {

        mProviderHelper.persist("a", "key", "value");

        final List<TrayItem> trayItems = mProviderHelper
                .queryProvider(TrayProviderHelper.getUri());
        assertEquals(1, trayItems.size());
        assertDatabaseSize(1);

        final TrayIsolatedContext mockContext = getProviderMockContext();

        MockContentProvider mockProvider = new MockContentProvider(mockContext) {
            @Override
            public Cursor query(final Uri uri, final String[] projection,
                    final String selection, final String[] selectionArgs,
                    final String sortOrder) {
                // this causes the exception below
                return null;
            }
        };
        mockContext.addProvider(TrayProvider.AUTHORITY, mockProvider);
        mockContext.enableMockResolver(true);
        final TrayProviderHelper providerHelper = new TrayProviderHelper(mockContext);
        try {
            providerHelper.queryProvider(TrayProviderHelper.getInternalUri());
            fail();
        } catch (IllegalStateException e) {
            // no connection to the provider/database
        }

    }
}