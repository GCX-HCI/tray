package net.grandcentrix.tray.core;

import junit.framework.TestCase;

/**
 * Created by pascalwelsch on 5/17/15.
 */
public class WrongTypeExceptionTest extends TestCase {

    public void testConstructor() throws Exception {
        final WrongTypeException exception0 = new WrongTypeException();
        assertNotNull(exception0);

        assertNotNull(new WrongTypeException(exception0));

        final WrongTypeException exception1 = new WrongTypeException("something");
        assertNotNull(exception1);
        assertEquals("something", exception1.getMessage());

        final WrongTypeException exception2 = new WrongTypeException("something %s", "wrong");
        assertNotNull(exception2);
        assertEquals("something wrong", exception2.getMessage());

        final WrongTypeException exception3 = new WrongTypeException("something", exception0);
        assertNotNull(exception3);
        assertEquals("something", exception3.getMessage());
    }

}