package net.grandcentrix.tray.accessor;

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockModularizedStorage;
import net.grandcentrix.tray.provider.TrayContract;
import net.grandcentrix.tray.provider.TrayItem;

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
        assertTrue(Preference.isDataTypeSupported("string"));
        assertTrue(Preference.isDataTypeSupported(1));
        assertTrue(Preference.isDataTypeSupported(1f));
        assertTrue(Preference.isDataTypeSupported(1l));
        assertTrue(Preference.isDataTypeSupported(true));

        assertTrue(Preference.isDataTypeSupported(new String("string")));
        assertTrue(Preference.isDataTypeSupported(new Integer(1)));
        assertTrue(Preference.isDataTypeSupported(new Float(1f)));
        assertTrue(Preference.isDataTypeSupported(new Long(1l)));
        assertTrue(Preference.isDataTypeSupported(new Boolean(true)));

        assertTrue(Preference.isDataTypeSupported(null));

        // not supported
        assertFalse(Preference.isDataTypeSupported(new Object()));
        assertFalse(Preference.isDataTypeSupported(new Date()));
        assertFalse(Preference.isDataTypeSupported(1d));
        assertFalse(Preference.isDataTypeSupported(new Double(1d)));
    }

    public void testClear() throws Exception {
        final MockSimplePreference mockPreference = new MockSimplePreference(1);
        mockPreference.put("a", "a");
        mockPreference.put("b", "b");
        assertEquals(mockPreference.getAll().size(), 2);

        mockPreference.clear();
        assertEquals(mockPreference.getAll().size(), 0);
    }

    public void testGetAll() throws Exception {
        final MockSimplePreference mockPreference = new MockSimplePreference(1);
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
        final MockSimplePreference mockPreference = new MockSimplePreference(1);
        mockPreference.put("key", "value");
        final TrayItem item = mockPreference.getPref("key");
        assertNotNull(item);
        assertEquals("key", item.key());
        assertEquals("value", item.value());
        assertEquals(mockPreference.getModularizedStorage().getModule(), item.module());
        assertEquals(item.created(), item.updateTime());
    }

    public void testInstantiation() throws Exception {
        final HashMap<String, String> map = new HashMap<>();
        new MockSimplePreference(1) {
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
            new MockSimplePreference(version);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(version)));
        }

        version = -1000;
        try {
            new MockSimplePreference(version);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(version)));
        }
    }

    public void testOnDowngradeShouldFail() throws Exception {
        final MockModularizedStorage storage = new MockModularizedStorage("blubb");
        new MockSimplePreference(storage, 2);
        try {
            new MockSimplePreference(storage, 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("downgrade"));
        }
    }

    public void testPut() throws Exception {
        final MockSimplePreference pref = new MockSimplePreference(1);
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
        final MockSimplePreference mockPreference = new MockSimplePreference(1);
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
        final MockSimplePreference mockPreference = new MockSimplePreference(1) {
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
        mockPreference.detectVersionChange(2);
        assertTrue(map.containsKey("up"));
        assertFalse(map.containsKey("down"));
        map.clear();

        mockPreference.detectVersionChange(1);
        assertTrue(map.containsKey("down"));
        assertFalse(map.containsKey("up"));
    }
}