package net.grandcentrix.tray.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.grandcentrix.tray.util.SqlSelectionHelper.extendSelection;
import static net.grandcentrix.tray.util.SqlSelectionHelper.extendSelectionArgs;

public class SqlSelectionHelperTest extends TestCase {

    public void testExtendSelection() throws Exception {
        assertEquals("(a) AND (b)", extendSelection("a", "b"));
        assertEquals("b", extendSelection(null, "b"));
        assertEquals("b", extendSelection("", "b"));
        assertEquals("a", extendSelection("a", null));
        assertEquals("a", extendSelection("a", ""));
    }

    public void testExtendSelectionArgs() throws Exception {
        assertTrue(Arrays.equals(new String[]{"a", "b", "c"},
                extendSelectionArgs("a", new String[]{"b", "c"})));

        assertTrue(Arrays.equals(new String[]{"b", "c"},
                extendSelectionArgs("", new String[]{"b", "c"})));

        assertTrue(Arrays.equals(new String[]{"a"},
                extendSelectionArgs("a", new String[]{})));

        assertTrue(Arrays.equals(new String[]{"a"},
                extendSelectionArgs("a", null)));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, new String[]{})));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, new ArrayList<String>())));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, (String[]) null)));

        assertTrue(Arrays.equals(new String[]{"a", "b"},
                extendSelectionArgs(new String[]{"a", "b"}, (List<String>) null)));

        assertEquals(null, extendSelectionArgs((String[]) null, (String[]) null));

        assertEquals(null, extendSelectionArgs(null, (List<String>) null));
    }

    public void testUselessConstructorCall() throws Exception {
        // make sure the test coverage is at 100%
        new SqlSelectionHelper();
    }
}