package net.grandcentrix.tray.publicapi;

import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 5/13/15.
 */
public class SaveNull extends TrayProviderTestCase {

    public static final String KEY = "key";

    public static final String VALUE = "value";

    public static final String DEFAULT_VALUE = "default";

    private TestTrayModulePreferences mPref;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPref = new TestTrayModulePreferences(getProviderMockContext(), "publictest");
    }

    public void testSaveNull() throws Exception {
        assertEquals(DEFAULT_VALUE, mPref.getString(KEY, DEFAULT_VALUE));
        mPref.put(KEY, null);
        assertEquals(null, mPref.getString(KEY, DEFAULT_VALUE));
    }

    public void testOverrideWithNull() throws Exception {
        mPref.put(KEY, VALUE);
        assertEquals(VALUE, mPref.getString(KEY, DEFAULT_VALUE));
        mPref.put(KEY, null);
        assertEquals(null, mPref.getString(KEY, DEFAULT_VALUE));
    }

    public void testDeleteNull() throws Exception {
        testSaveNull();
        mPref.remove(KEY);
        assertEquals(DEFAULT_VALUE, mPref.getString(KEY, DEFAULT_VALUE));
    }

    public void testUpdateNull() throws Exception {
        testSaveNull();
        mPref.put(KEY, "otherValue");
        assertEquals("otherValue", mPref.getString(KEY, DEFAULT_VALUE));
    }
}
