package net.grandcentrix.tray.provider;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrayItemTest extends TestCase {

    public void testCreated() throws Exception {

    }

    public void testKey() throws Exception {

    }

    public void testMigratedKey() throws Exception {

    }

    public void testModule() throws Exception {

    }

    public void testNullValues() throws Exception {
        final TrayItem item = new TrayItem(null, null, null, null, null, null);
        assertEquals(null, item.key());
        assertEquals(null, item.value());
        assertEquals(null, item.migratedKey());
        assertEquals(null, item.module());
        assertEquals(null, item.updateTime());
        assertEquals(null, item.created());
    }

    public void testToString() throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        final Date created = new Date();
        final Date updated = new Date();
        final TrayItem item = new TrayItem(created, "key", "module", updated, "value",
                "migrateKey");
        final String string = item.toString();
        assertTrue(string.contains(item.key()));
        assertTrue(string.contains(item.value()));
        assertTrue(string.contains(item.migratedKey()));
        assertTrue(string.contains(item.module()));
        assertTrue(string.contains(sf.format(item.updateTime())));
        assertTrue(string.contains(sf.format(item.created())));
    }

    public void testValues() throws Exception {
        final Date created = new Date();
        final Date updated = new Date();
        final TrayItem item = new TrayItem(created, "key", "module", updated, "value",
                "migrateKey");
        assertEquals("key", item.key());
        assertEquals("value", item.value());
        assertEquals("migrateKey", item.migratedKey());
        assertEquals("module", item.module());
        assertEquals(updated, item.updateTime());
        assertEquals(created, item.created());
    }
}