package net.grandcentrix.tray.accessor;

import junit.framework.TestCase;

import android.annotation.SuppressLint;

import java.util.Date;

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
}