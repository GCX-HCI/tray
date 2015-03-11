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

import net.grandcentrix.tray.mock.MockTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesImportTest extends TrayProviderTestCase {

    public static final String SHARED_PREF_NAME = "test";

    private SharedPreferences mSharedPrefs;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSharedPrefs = getContext()
                .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
        mSharedPrefs.edit().clear().commit();
    }

    public void testConstruct() throws Exception {
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "sharedPrefKey", "trayKey");
        assertEquals("sharedPrefKey", sharedPreferencesImport.getPreviousKey());
        assertEquals("trayKey", sharedPreferencesImport.getTrayKey());
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

        final TrayMigrator migrator = new TrayMigrator(trayPreference);
        final SharedPreferencesImport trayImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        migrator.performMigration(trayImport);

        assertEquals("nothing", mSharedPrefs.getString("key", "nothing"));
        assertEquals(1, trayPreference.getAll().size());
        assertEquals("value", trayPreference.getString("trayKey", "nothing"));
        final TrayItem pref = trayPreference.getPref("trayKey");
        assertNotNull(pref);
        assertEquals("key", pref.migratedKey());
    }

    public void testPostMigrate() throws Exception {
        mSharedPrefs.edit().putString("key", "data").commit();
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        sharedPreferencesImport.onPostMigrate(false);
        assertEquals("data", mSharedPrefs.getString("key", null)); // data is not deleted

        sharedPreferencesImport.onPostMigrate(true);
        assertEquals(null, mSharedPrefs.getString("key", null)); // data is gone
    }

    public void testShouldMigrate() throws Exception {
        final SharedPreferencesImport sharedPreferencesImport = new SharedPreferencesImport(
                getContext(), SHARED_PREF_NAME, "key", "trayKey");
        assertEquals(false, sharedPreferencesImport.shouldMigrate());

        mSharedPrefs.edit().putString("key", "data").commit();
        assertEquals(true, sharedPreferencesImport.shouldMigrate());
    }
}