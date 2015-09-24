package net.grandcentrix.tray.core;

import junit.framework.TestCase;

/**
 * Created by pascalwelsch on 9/24/15.
 */
public class TrayLogTest extends TestCase {

    public void testConstructor() throws Exception {
        try {
            new TrayLog();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("no instances"));
        }
    }

    public void testLogD() throws Exception {
        TrayLog.d("text");
        TrayLog.d(null);
    }

    public void testLogE() throws Exception {
        TrayLog.e("text");
        TrayLog.e(null);
        TrayLog.e(new Exception("text"), "text");
        TrayLog.e(new Exception("text"), null);
    }

    public void testLogV() throws Exception {
        TrayLog.DEBUG = false;
        TrayLog.v("text");
        TrayLog.v(null);

        TrayLog.DEBUG = true;
        TrayLog.v("text");
        TrayLog.v(null);
    }

    public void testLogW() throws Exception {
        TrayLog.w("text");
        TrayLog.w(null);
    }

    public void testLogWtf() throws Exception {
        TrayLog.wtf("text");
        TrayLog.wtf(null);
        TrayLog.wtf(new Exception("text"), "text");
        TrayLog.wtf(new Exception("text"), null);
    }

    public void testSetTag() throws Exception {
        TrayLog.setTag("myTag");
        TrayLog.setTag(null);
    }
}