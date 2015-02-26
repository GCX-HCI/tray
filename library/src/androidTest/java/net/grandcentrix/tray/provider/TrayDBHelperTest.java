package net.grandcentrix.tray.provider;

import android.test.AndroidTestCase;

public class TrayDBHelperTest extends AndroidTestCase {

    public void testInstantiation() throws Exception {
        new TrayDBHelper(getContext());
    }

    public void testOnUpgrade() throws Exception {
        final TrayDBHelper trayDBHelper = new TrayDBHelper(getContext());
        try {
            trayDBHelper.onUpgrade(trayDBHelper.getWritableDatabase(), 0, 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("version"));
        }

    }
}