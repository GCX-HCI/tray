package net.grandcentrix.tray.core;

import junit.framework.TestCase;

import static net.grandcentrix.tray.core.TrayLog.logd;
import static net.grandcentrix.tray.core.TrayLog.loge;
import static net.grandcentrix.tray.core.TrayLog.logv;
import static net.grandcentrix.tray.core.TrayLog.logw;
import static net.grandcentrix.tray.core.TrayLog.logwtf;
import static net.grandcentrix.tray.core.TrayLog.setTag;

/**
 * Created by pascalwelsch on 9/24/15.
 */
public class TrayLogTest extends TestCase {

    public void testLogD() throws Exception {
        logd("text");
        logd(null);
    }

    public void testLogE() throws Exception {
        loge("text");
        loge(null);
        loge(new Exception("text"), "text");
        loge(new Exception("text"), null);
    }

    public void testLogV() throws Exception {
        TrayLog.DEBUG = false;
        logv("text");
        logv(null);

        TrayLog.DEBUG = true;
        logv("text");
        logv(null);
    }

    public void testLogW() throws Exception {
        logw("text");
        logw(null);
    }

    public void testLogWtf() throws Exception {
        logwtf("text");
        logwtf(null);
        logwtf(new Exception("text"), "text");
        logwtf(new Exception("text"), null);
    }

    public void testSetTag() throws Exception {
        setTag("myTag");
        setTag(null);
    }

    public void testConstructor() throws Exception {
        try {
            new TrayLog();
        } catch (IllegalStateException e){
            assertTrue(e.getMessage().contains("no instances"));
        }
    }
}