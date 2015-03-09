package net.grandcentrix.tray.migration;

import junit.framework.TestCase;

import net.grandcentrix.tray.accessor.MockSimplePreference;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;

public class TrayMigratorTest extends TestCase {

    private class TestMigration implements TrayMigration {

        private final String mNewKey;

        private final String mOldKey;

        private TestMigration(final String newKey, final String oldKey) {
            mNewKey = newKey;
            mOldKey = oldKey;
        }

        @Override
        public Object getData() {
            return mDataStore.get(mOldKey);
        }

        @NonNull
        @Override
        public String getPreviousKey() {
            return mOldKey;
        }

        @NonNull
        @Override
        public String getTrayKey() {
            return mNewKey;
        }

        @Override
        public void onPostMigrate(final boolean successful) {
            if (successful) {
                mDataStore.remove(mOldKey);
            }
        }

        @Override
        public boolean shouldMigrate() {
            return !mDataStore.containsKey(mOldKey);
        }
    }

    public static final String OLD_KEY = "oldKey";

    public static final String NEW_KEY = "newKey";

    public static final String DATA = "7h3 D474";

    private HashMap<String, String> mDataStore;

    private MockSimplePreference mTrayPreference;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDataStore = new HashMap<>();
        mDataStore.put(OLD_KEY, DATA);
        assertEquals(1, mDataStore.size());

        mTrayPreference = new MockSimplePreference(1);
        assertEquals(0, mTrayPreference.getAll().size());
    }

    public void testMigrateTwice() throws Exception {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(Arrays.asList(migration));
        trayMigrator.performMigration(Arrays.asList(migration));

        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testMissingPreviousKey() throws Exception {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TrayMigration() {
            @Override
            public Object getData() {
                return null;
            }

            @NonNull
            @Override
            public String getPreviousKey() {
                return null;
            }

            @NonNull
            @Override
            public String getTrayKey() {
                return null;
            }

            @Override
            public void onPostMigrate(final boolean successful) {

            }

            @Override
            public boolean shouldMigrate() {
                return false;
            }
        };
        try {
            trayMigrator.performMigration(Arrays.asList(migration));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("previousKey"));
        }

    }

    public void testPerformMigration() throws Exception {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(Arrays.asList(migration));

        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testOverrideWithMigration() throws Exception {
        mTrayPreference.put(NEW_KEY, "other data");

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(Arrays.asList(migration));

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testOverrideMigratedDataWithNewMigration() throws Exception {
        mTrayPreference.put(NEW_KEY, "other data");

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(Arrays.asList(migration));

        mDataStore.put("something", "other");
        final TrayMigration migration2 = new TestMigration(NEW_KEY, "something");
        trayMigrator.performMigration(Arrays.asList(migration2));
        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals("other", mTrayPreference.getString(NEW_KEY, null));
    }
}