package net.grandcentrix.tray.storage;

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockModularizedStorage;

public class ModularizedStorageTest extends TestCase {

    public void testGetModule() throws Exception {
        final MockModularizedStorage storage = new MockModularizedStorage("test");
        assertEquals("test", storage.getModule());
    }
}