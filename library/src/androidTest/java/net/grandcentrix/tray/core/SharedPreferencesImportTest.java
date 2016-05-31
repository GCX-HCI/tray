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

package net.grandcentrix.tray.core;

import net.grandcentrix.tray.mock.MockTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesImportTest extends TrayProviderTestCase {

    public static final String SHARED_PREF_NAME = "test";

    private SharedPreferences mSharedPrefs;

    public void testConstruct() throws Exception {
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "sharedPrefKey", "trayKey");
        assertEquals("sharedPrefKey", sharedPreferencesImport.getPreviousKey());
        assertEquals("trayKey", sharedPreferencesImport.getTrayKey());
    }

    public void testEquals() throws Exception {
        final Object object = new Object();
        final Object other = "";
        assertTrue(SharedPreferencesImport.equals(object, object));
        assertFalse(SharedPreferencesImport.equals(object, other));

        assertTrue(SharedPreferencesImport.equals(null, null));
        assertFalse(SharedPreferencesImport.equals(object, null));
        assertFalse(SharedPreferencesImport.equals(null, object));
    }

    public void testGetData() throws Exception {
        mSharedPrefs.edit().putString("sharedPrefKey", "data").commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "sharedPrefKey", "trayKey");
        assertEquals("data", sharedPreferencesImport.getData());
    }

    public void testMigration() throws Exception {
        mSharedPrefs.edit().putString("key", "value").commit();
        assertEquals("value", mSharedPrefs.getString("key", null));

        final MockTrayModulePreferences trayPreference = new MockTrayModulePreferences(
                getProviderMockContext(), "myModule");
        assertEquals(0, trayPreference.getAll().size());

        final SharedPreferencesImport trayImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        trayPreference.migrate(trayImport);

        assertEquals("nothing", mSharedPrefs.getString("key", "nothing"));
        assertEquals(1, trayPreference.getAll().size());
        assertEquals("value", trayPreference.getString("trayKey", "nothing"));
        final TrayItem pref = trayPreference.getPref("trayKey");
        assertNotNull(pref);
        assertEquals("key", pref.migratedKey());
    }

    public void testMigrationInOnCreate() throws Exception {
        mSharedPrefs.edit().putString("key", "value").commit();

        final MockTrayModulePreferences trayPref = new MockTrayModulePreferences(
                getProviderMockContext(), "importWithAccess") {
            @Override
            protected void onCreate(final int newVersion) {
                super.onCreate(newVersion);
                migrate(new SharedPreferencesImport(getContext(), SHARED_PREF_NAME, "key",
                        "myKey"));
            }
        };

        assertEquals("value", trayPref.getString("myKey"));
        assertEquals("nothing", mSharedPrefs.getString("key", "nothing"));
    }

    public void testMigrationInOnCreateNoDataInSharedPreferences() throws Exception {
        assertEquals("nothing", mSharedPrefs.getString("key", "nothing"));

        final MockTrayModulePreferences trayPref = new MockTrayModulePreferences(
                getProviderMockContext(), "importWithAccess") {
            @Override
            protected void onCreate(final int newVersion) {
                super.onCreate(newVersion);
                migrate(new SharedPreferencesImport(getContext(), SHARED_PREF_NAME, "key",
                        "myKey"));
            }
        };

        assertEquals("nothing", trayPref.getString("myKey", "nothing"));
        assertEquals("nothing", mSharedPrefs.getString("key", "nothing"));
    }

    public void testPostMigrateCorrect() throws Exception {
        final String DATA = "data";
        mSharedPrefs.edit().putString("key", DATA).commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        final TrayItem trayItem = mock(TrayItem.class);
        when(trayItem.value()).thenReturn(DATA);
        sharedPreferencesImport.onPostMigrate(trayItem);
        assertEquals("default", mSharedPrefs.getString("key", "default")); // data is deleted
    }

    public void testPostMigrateIncorrect() throws Exception {
        final String DATA = "data";
        mSharedPrefs.edit().putString("key", DATA).commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        final TrayItem trayItem = mock(TrayItem.class);
        when(trayItem.value()).thenReturn(DATA + "something");
        sharedPreferencesImport.onPostMigrate(trayItem);
        assertEquals(DATA, mSharedPrefs.getString("key", "default")); // data is deleted
    }

    public void testPostMigrateIntegerCorrect() throws Exception {
        final int DATA = 13;
        mSharedPrefs.edit().putInt("key", DATA).commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        final TrayItem trayItem = mock(TrayItem.class);
        when(trayItem.value()).thenReturn(Integer.toString(DATA));
        sharedPreferencesImport.onPostMigrate(trayItem);
        assertEquals(1, mSharedPrefs.getInt("key", 1)); // data is deleted
    }

    public void testPostMigrateWithNull() throws Exception {
        final String DATA = "data";
        mSharedPrefs.edit().putString("key", DATA).commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        sharedPreferencesImport.onPostMigrate(null);
        assertEquals(DATA, mSharedPrefs.getString("key", "default")); // data is not deleted
    }

    public void testShouldMigrate() throws Exception {
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        assertEquals(false, sharedPreferencesImport.shouldMigrate());

        mSharedPrefs.edit().putString("key", "data").commit();
        assertEquals(true, sharedPreferencesImport.shouldMigrate());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSharedPrefs = getContext()
                .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
        mSharedPrefs.edit().clear().commit();
    }
}