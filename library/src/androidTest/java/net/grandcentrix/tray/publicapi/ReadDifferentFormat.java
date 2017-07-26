package net.grandcentrix.tray.publicapi;

import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.WrongTypeException;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 5/15/15.
 */
public class ReadDifferentFormat extends TrayProviderTestCase {

    public static final String KEY = "key";

    private TestTrayModulePreferences mPref;

    public void testReadBooleanAsWrongType() throws Exception {
        mPref.put(KEY, true);
        assertEquals(true, mPref.getBoolean(KEY));
        assertEquals("true", mPref.getString(KEY));
        try {
            assertEquals(25f, mPref.getFloat(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("true"));
        }
        try {
            assertEquals(25l, mPref.getLong(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("true"));
        }
        try {
            assertEquals(25, mPref.getInt(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("true"));
        }
    }

    public void testReadFloatAsWrongType() throws Exception {
        mPref.put(KEY, 25.1f);
        assertEquals(25.1f, mPref.getFloat(KEY));
        assertEquals(false, mPref.getBoolean(KEY));
        assertEquals("25.1", mPref.getString(KEY));
        try {
            assertEquals(25l, mPref.getLong(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("25.1"));
        }
        try {
            assertEquals(25, mPref.getInt(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("25.1"));
        }
    }

    public void testReadIntAsWrongType() throws Exception {
        mPref.put(KEY, 25);
        assertEquals(25, mPref.getInt(KEY));
        assertEquals(false, mPref.getBoolean(KEY));
        assertEquals("25", mPref.getString(KEY));
        assertEquals(25f, mPref.getFloat(KEY));
        assertEquals(25l, mPref.getLong(KEY));
    }

    public void testReadLongAsWrongType() throws Exception {
        mPref.put(KEY, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, mPref.getLong(KEY));
        assertEquals("9223372036854775807", mPref.getString(KEY));
        assertEquals(false, mPref.getBoolean(KEY));

        // this is kind of "false". 9.223... is not really the expected value
        assertEquals(Float.parseFloat(String.valueOf(Long.MAX_VALUE)), mPref.getFloat(KEY));
        try {
            assertEquals(Long.MAX_VALUE, mPref.getInt(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("int"));
        }
    }

    public void testReadNullStringAsWrongType() throws Exception {
        assertEquals("default", mPref.getString(KEY, "default"));
        mPref.put(KEY, null);
        final TrayItem pref = mPref.getPref(KEY);
        assertNotNull(pref);
        assertEquals(null, pref.value());
        assertEquals(null, mPref.getString(KEY));

        assertEquals(false, mPref.getBoolean(KEY));
        try {
            assertEquals(0.0f, mPref.getFloat(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("Float"));
            assertTrue(e.getMessage().contains("null"));
        }
        try {
            assertEquals(0l, mPref.getLong(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("Long"));
            assertTrue(e.getMessage().contains("null"));
        }
        try {
            assertEquals(0, mPref.getInt(KEY));
            fail();
        } catch (WrongTypeException e) {
            assertTrue(e.getMessage().contains("Integer"));
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPref = new TestTrayModulePreferences(getProviderMockContext(), "publictest");
    }

}
