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

package net.grandcentrix.tray.migration;

import junit.framework.TestCase;

import net.grandcentrix.tray.BuildConfig;
import net.grandcentrix.tray.accessor.MockSimplePreference;
import net.grandcentrix.tray.mock.MockModularizedStorage;
import net.grandcentrix.tray.provider.TrayItem;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrayMigratorTest extends TestCase {

    public class TestMigration implements TrayMigration {

        private final String mNewKey;

        private final String mOldKey;

        public TestMigration(final String newKey, final String oldKey) {
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
            return mDataStore.containsKey(mOldKey);
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
        System.setProperty("dexmaker.dexcache",
                "/data/data/" + BuildConfig.APPLICATION_ID + ".test/cache");
        mDataStore = new HashMap<>();
        mDataStore.put(OLD_KEY, DATA);
        assertEquals(1, mDataStore.size());

        mTrayPreference = new MockSimplePreference(1);
        assertEquals(0, mTrayPreference.getAll().size());
    }

    public void testMigrateTwice() throws Exception {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(migration);
        trayMigrator.performMigration(migration);

        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testMigrationDidNotWorkNull() throws Exception {
        final MockModularizedStorage storage = spy(new MockModularizedStorage("test"));
        mTrayPreference = new MockSimplePreference(storage, 1);
        assertEquals(storage, mTrayPreference.getModularizedStorage());
        when(storage.get(NEW_KEY)).thenReturn(null);
        assertNull(storage.get(NEW_KEY));

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = spy(new TestMigration(NEW_KEY, OLD_KEY));

        trayMigrator.performMigration(migration);
        verify(migration, times(1)).onPostMigrate(false);
    }

    public void testMigrationDidNotWorkWrongData() throws Exception {
        final MockModularizedStorage storage = spy(new MockModularizedStorage("test"));
        mTrayPreference = new MockSimplePreference(storage, 1);
        assertEquals(storage, mTrayPreference.getModularizedStorage());
        final TrayItem wrongItem = new TrayItem("test", NEW_KEY, OLD_KEY,
                "thisIsNotTheMigratedData", new Date(), new Date());
        when(storage.get(NEW_KEY)).thenReturn(wrongItem);
        assertEquals(wrongItem, storage.get(NEW_KEY));

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = spy(new TestMigration(NEW_KEY, OLD_KEY));

        trayMigrator.performMigration(migration);
        verify(migration, times(1)).onPostMigrate(false);
    }

    public void testOverrideMigratedDataWithNewMigration() throws Exception {
        mTrayPreference.put(NEW_KEY, "other data");

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(migration);

        mDataStore.put("something", "other");
        final TrayMigration migration2 = new TestMigration(NEW_KEY, "something");
        trayMigrator.performMigration(migration2);
        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals("other", mTrayPreference.getString(NEW_KEY, null));
    }

    public void testOverrideWithMigration() throws Exception {
        mTrayPreference.put(NEW_KEY, "other data");

        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(migration);

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testPerformMigration() throws Exception {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TrayMigration migration = new TestMigration(NEW_KEY, OLD_KEY);
        trayMigrator.performMigration(migration);

        assertEquals(0, mDataStore.size());
        assertEquals(1, mTrayPreference.getAll().size());

        assertEquals(DATA, mTrayPreference.getString(NEW_KEY, null));
    }

    public void testUnsupportedDataType() {
        final TrayMigrator trayMigrator = new TrayMigrator(mTrayPreference);
        final TestMigration migration = spy(new TestMigration(NEW_KEY, OLD_KEY));
        when(migration.getData()).thenReturn(1d); // double is not supported

        trayMigrator.performMigration(migration);
        verify(migration, times(1)).onPostMigrate(false);

        // not imported
        assertEquals(1, mDataStore.size());
        assertEquals(0, mTrayPreference.getAll().size());
    }
}