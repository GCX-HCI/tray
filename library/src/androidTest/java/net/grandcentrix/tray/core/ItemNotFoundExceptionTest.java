package net.grandcentrix.tray.core;

import junit.framework.TestCase;

/**
 * Created by pascalwelsch on 5/17/15.
 */
public class ItemNotFoundExceptionTest extends TestCase {

    public void testConstructor() throws Exception {
        final ItemNotFoundException exception0 = new ItemNotFoundException();
        assertNotNull(exception0);

        assertNotNull(new ItemNotFoundException(exception0));

        final ItemNotFoundException exception1 = new ItemNotFoundException("something");
        assertNotNull(exception1);
        assertEquals("something", exception1.getMessage());

        final ItemNotFoundException exception2 = new ItemNotFoundException("something %s", "wrong");
        assertNotNull(exception2);
        assertEquals("something wrong", exception2.getMessage());

        final ItemNotFoundException exception3 = new ItemNotFoundException("something", exception0);
        assertNotNull(exception3);
        assertEquals("something", exception3.getMessage());
    }
}