package net.grandcentrix.tray.publicapi;

import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 5/13/15.
 */
public class SaveNull extends TrayProviderTestCase {

    public static final String KEY = "key";

    private TestTrayModulePreferences mPref;

    public void testDeleteNull() throws Exception {
        testSaveNullAsString();
        mPref.remove(KEY);
        assertEquals("default", mPref.getString(KEY, "default"));
    }

    public void testOverrideWithNull() throws Exception {
        mPref.put(KEY, "value");
        assertEquals("value", mPref.getString(KEY));
        mPref.put(KEY, null);
        assertEquals(null, mPref.getString(KEY));
    }

    public void testSaveNullAsString() throws Exception {
        assertEquals("default", mPref.getString(KEY, "default"));
        mPref.put(KEY, null);
        final TrayItem pref = mPref.getPref(KEY);
        assertNotNull(pref);
        assertEquals(null, pref.value());
        assertEquals(null, mPref.getString(KEY));
    }

    public void testUpdateNull() throws Exception {
        testSaveNullAsString();
        mPref.put(KEY, "otherValue");
        assertEquals("otherValue", mPref.getString(KEY, "default"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPref = new TestTrayModulePreferences(getProviderMockContext(), "publictest");
    }
}
