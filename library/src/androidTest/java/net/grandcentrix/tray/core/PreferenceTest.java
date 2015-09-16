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

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockTrayStorage;

import android.annotation.SuppressLint;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class PreferenceTest extends TestCase {

    @SuppressLint("UseValueOf")
    @SuppressWarnings(
            {"RedundantStringConstructorCall", "UnnecessaryBoxing", "BooleanConstructorCall"})
    public void testCheckIfDataTypIsSupported() throws Exception {

        // supported
        assertTrue(Preferences.isDataTypeSupported("string"));
        assertTrue(Preferences.isDataTypeSupported(1));
        assertTrue(Preferences.isDataTypeSupported(1f));
        assertTrue(Preferences.isDataTypeSupported(1l));
        assertTrue(Preferences.isDataTypeSupported(true));

        assertTrue(Preferences.isDataTypeSupported(new String("string")));
        assertTrue(Preferences.isDataTypeSupported(new Integer(1)));
        assertTrue(Preferences.isDataTypeSupported(new Float(1f)));
        assertTrue(Preferences.isDataTypeSupported(new Long(1l)));
        assertTrue(Preferences.isDataTypeSupported(new Boolean(true)));

        assertTrue(Preferences.isDataTypeSupported(null));

        // not supported
        assertFalse(Preferences.isDataTypeSupported(new Object()));
        assertFalse(Preferences.isDataTypeSupported(new Date()));
        assertFalse(Preferences.isDataTypeSupported(1d));
        assertFalse(Preferences.isDataTypeSupported(new Double(1d)));
    }

    public void testClear() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        mockPreference.put("a", "a");
        mockPreference.put("b", "b");
        assertEquals(mockPreference.getAll().size(), 2);

        mockPreference.clear();
        assertEquals(mockPreference.getAll().size(), 0);
    }

    public void testGetAll() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        final Collection<TrayItem> all = mockPreference.getAll();
        assertNotNull(all);
        assertEquals(0, all.size());

        mockPreference.put("test", "test");
        mockPreference.put("foo", "foo");

        final Collection<TrayItem> all2 = mockPreference.getAll();
        assertNotNull(all2);
        assertEquals(2, all2.size());
    }

    public void testGetPref() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        mockPreference.put("key", "value");
        final TrayItem item = mockPreference.getPref("key");
        assertNotNull(item);
        assertEquals("key", item.key());
        assertEquals("value", item.value());
        assertEquals(mockPreference.getName(), item.module());
        assertEquals(item.created(), item.updateTime());
    }

    public void testInstantiation() throws Exception {
        final HashMap<String, String> map = new HashMap<>();
        new MockSimplePreferences(1) {
            @Override
            protected void onCreate(final int newVersion) {
                super.onCreate(newVersion);
                map.put("create", "create");
            }
        };
        assertTrue(map.containsKey("create"));
    }

    public void testLowVersion() throws Exception {
        int version = 0;
        try {
            new MockSimplePreferences(version);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(version)));
        }

        version = -1000;
        try {
            new MockSimplePreferences(version);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(version)));
        }
    }

    public void testOnDowngradeShouldFail() throws Exception {
        final MockTrayStorage storage = new MockTrayStorage("blubb");
        new MockSimplePreferences(storage, 2);
        try {
            new MockSimplePreferences(storage, 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("downgrade"));
        }
    }

    public void testOnUpgrade() throws Exception {
        final MockSimplePreferences appPreferences = new MockSimplePreferences(1);
        try {
            appPreferences.onUpgrade(0, 1);
            fail();
        } catch (IllegalStateException e) {
            // not implemented yet
        }
    }

    public void testPut() throws Exception {
        final MockSimplePreferences pref = new MockSimplePreferences(1);
        // String
        pref.put("a", "a");
        assertEquals("a", pref.getString("a", ""));

        // Int
        pref.put("a", 1);
        assertEquals(1, pref.getInt("a", 0));

        // Long
        pref.put("a", 5l);
        assertEquals(5l, pref.getLong("a", 0l));

        // Float
        pref.put("a", 10f);
        assertEquals(10f, pref.getFloat("a", 0f));

        // Boolean
        pref.put("a", true);
        assertEquals(true, pref.getBoolean("a", false));
    }

    public void testRemove() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        mockPreference.put("test", "test");
        mockPreference.put("foo", "foo");

        final Collection<TrayItem> all2 = mockPreference.getAll();
        assertNotNull(all2);
        assertEquals(2, all2.size());

        mockPreference.remove("test");

        final Collection<TrayItem> all1 = mockPreference.getAll();
        assertNotNull(all1);
        assertEquals(1, all2.size());

        mockPreference.remove("foo");

        final Collection<TrayItem> all = mockPreference.getAll();
        assertNotNull(all);
        assertEquals(0, all.size());
    }

    public void testVersionChange() throws Exception {
        final HashMap<String, String> map = new HashMap<>();
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1) {
            @Override
            protected void onCreate(final int newVersion) {
                super.onCreate(newVersion);
                map.put("create", "create");
            }

            @Override
            protected void onDowngrade(final int oldVersion, final int newVersion) {
                map.put("down", "down");
            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {
                map.put("up", "up");
            }
        };
        mockPreference.changeVersion(2);
        assertTrue(map.containsKey("up"));
        assertFalse(map.containsKey("down"));
        map.clear();

        mockPreference.changeVersion(1);
        assertTrue(map.containsKey("down"));
        assertFalse(map.containsKey("up"));
    }

    public void testWipe() throws Exception {
        final MockSimplePreferences preferences = new MockSimplePreferences(1);
        assertEquals(1, preferences.getVersion());
        preferences.wipe();
        assertEquals(0, preferences.getVersion());
    }
}