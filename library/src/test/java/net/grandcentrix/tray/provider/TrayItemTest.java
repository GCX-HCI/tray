package net.grandcentrix.tray.provider;

import junit.framework.TestCase;

import android.annotation.SuppressLint;
import android.database.MatrixCursor;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrayItemTest extends TestCase {

    public void testContentValues() throws Exception {

        final MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                TrayContract.Preferences.Columns.KEY,
                TrayContract.Preferences.Columns.VALUE,
                TrayContract.Preferences.Columns.MODULE,
                TrayContract.Preferences.Columns.CREATED,
                TrayContract.Preferences.Columns.UPDATED,
                TrayContract.Preferences.Columns.MIGRATED_KEY
        });
        final Date created = new Date();
        final Date updated = new Date();
        matrixCursor.addRow(new Object[]{
                "key",
                "value",
                "module",
                created.getTime(),
                updated.getTime(),
                "migratedKey"
        });
        assertTrue(TextUtils.isEmpty(""));
        assertFalse(TextUtils.isEmpty("asdf"));
        assertTrue(matrixCursor.moveToFirst());

        final TrayItem item = new TrayItem(matrixCursor);
        assertEquals("key", item.key());
        assertEquals("value", item.value());
        assertEquals("migratedKey", item.migratedKey());
        assertEquals("module", item.module());
        assertEquals(updated, item.updateTime());
        assertEquals(created, item.created());
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
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        final Date created = new Date();
        final Date updated = new Date();
        final TrayItem item = new TrayItem(created, "key", "module", updated, "value",
                "migratedKey");
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
                "migratedKey");
        assertEquals("key", item.key());
        assertEquals("value", item.value());
        assertEquals("migratedKey", item.migratedKey());
        assertEquals("module", item.module());
        assertEquals(updated, item.updateTime());
        assertEquals(created, item.created());
    }
}