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

import junit.framework.Assert;
import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockTrayStorage;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

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
        assertTrue(mockPreference.put("a", "a"));
        assertTrue(mockPreference.put("b", "b"));
        assertEquals(mockPreference.getAll().size(), 2);

        assertTrue(mockPreference.clear());
        assertEquals(mockPreference.getAll().size(), 0);
    }

    public void testContains() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        assertFalse(mockPreference.contains("a"));
        assertTrue(mockPreference.put("a", "value"));
        assertTrue(mockPreference.contains("a"));
    }

    public void testClearFails() throws Exception {
        final TrayStorage storage = new MockTrayStorage("test") {
            @Override
            public boolean clear() {
                return false;
            }
        };
        final MockSimplePreferences mockPreference = new MockSimplePreferences(storage, 1);
        assertTrue(mockPreference.put("a", "a"));
        assertTrue(mockPreference.put("b", "b"));
        assertEquals(mockPreference.getAll().size(), 2);

        assertFalse(mockPreference.clear());
        assertEquals(mockPreference.getAll().size(), 2);
    }

    public void testGetAll() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        final Collection<TrayItem> all = mockPreference.getAll();
        assertNotNull(all);
        assertEquals(0, all.size());

        assertTrue(mockPreference.put("test", "test"));
        assertTrue(mockPreference.put("foo", "foo"));

        final Collection<TrayItem> all2 = mockPreference.getAll();
        assertNotNull(all2);
        assertEquals(2, all2.size());
    }

    public void testGetPref() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        assertTrue(mockPreference.put("key", "value"));
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
        assertTrue(pref.put("a", "a"));
        assertEquals("a", pref.getString("a", ""));

        // Int
        assertTrue(pref.put("a", 1));
        assertEquals(1, pref.getInt("a", 0));

        // Long
        assertTrue(pref.put("a", 5l));
        assertEquals(5l, pref.getLong("a", 0l));

        // Float
        assertTrue(pref.put("a", 10f));
        assertEquals(10f, pref.getFloat("a", 0f));

        // Boolean
        assertTrue(pref.put("a", true));
        assertEquals(true, pref.getBoolean("a", false));
    }

    public void testPutFailed() throws Exception {
        final MockSimplePreferences pref = new MockSimplePreferences(1);
        pref.breakStorage();

        // String
        assertFalse(pref.put("a", "a"));
        assertEquals("", pref.getString("a", ""));

        // Int
        assertFalse(pref.put("a", 1));
        assertEquals(0, pref.getInt("a", 0));

        // Long
        assertFalse(pref.put("a", 5l));
        assertEquals(0l, pref.getLong("a", 0l));

        // Float
        assertFalse(pref.put("a", 10f));
        assertEquals(0f, pref.getFloat("a", 0f));

        // Boolean
        assertFalse(pref.put("a", true));
        assertEquals(false, pref.getBoolean("a", false));
    }

    public void testRemove() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        assertTrue(mockPreference.put("test", "test"));
        assertTrue(mockPreference.put("foo", "foo"));

        final Collection<TrayItem> all2 = mockPreference.getAll();
        assertNotNull(all2);
        assertEquals(2, all2.size());

        assertTrue(mockPreference.remove("test"));

        final Collection<TrayItem> all1 = mockPreference.getAll();
        assertNotNull(all1);
        assertEquals(1, all2.size());

        assertTrue(mockPreference.remove("foo"));

        final Collection<TrayItem> all = mockPreference.getAll();
        assertNotNull(all);
        assertEquals(0, all.size());
    }

    public void testRemoveFailed() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        assertTrue(mockPreference.put("test", "test"));

        // This fails because the preference doesn't exist
        assertFalse(mockPreference.remove("foo"));

        // Mock failure due to storage problem
        mockPreference.breakStorage();
        assertFalse(mockPreference.remove("test"));
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

    public void testVersionChangeFailed() throws Exception {

        final TrayStorage storage = Mockito.mock(TrayStorage.class);
        Mockito.when(storage.getVersion())
                .thenThrow(new TrayException("something very very bad happened :-("));
        final MockSimplePreferences mockPreference = new MockSimplePreferences(storage, 1);

        assertFalse(mockPreference.isVersionChangeChecked());
        assertFalse((Boolean) Whitebox.getInternalState(mockPreference, "mChangeVersionSucceeded"));
    }

    public void testWipe() throws Exception {
        final MockSimplePreferences preferences = new MockSimplePreferences(1);
        assertEquals(1, preferences.getVersion());
        assertTrue(preferences.wipe());
        assertEquals(0, preferences.getVersion());
    }

    public void testWipeFailed() throws Exception {
        final TrayStorage storage = Mockito.mock(TrayStorage.class);
        Mockito.when(storage.wipe()).thenReturn(false);
        final MockSimplePreferences preferences = new MockSimplePreferences(storage, 1);
        assertFalse(preferences.wipe());
    }

    public void testEmptyKey() throws Exception {
        final MockSimplePreferences mockPreference = new MockSimplePreferences(1);
        try {
            mockPreference.put("", "test");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Preference key value cannot be empty.", e.getMessage());
        }
    }
}