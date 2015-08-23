/*
 * Copyright (C) 2015 grandcentrix GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.tray.storage;

import junit.framework.Assert;

import net.grandcentrix.tray.TrayRuntimeException;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class TrayStorageTest extends TrayProviderTestCase {

    final String TEST_KEY = "foo";

    final String TEST_KEY2 = "foo2";

    final String TEST_STRING = "fooBar";

    final String TEST_STRING2 = "fooBar2";

    private TrayStorage mStorage;

    public void testClear() throws Exception {
        final TrayStorage storage1 = new TrayStorage(getProviderMockContext(), "testClear1",
                TrayStorage.Type.USER);
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), "testClear2",
                TrayStorage.Type.USER);
        final TrayStorage storage3 = new TrayStorage(getProviderMockContext(), "testClear3",
                TrayStorage.Type.DEVICE);
        final TrayStorage storage4 = new TrayStorage(getProviderMockContext(), "testClear4",
                TrayStorage.Type.DEVICE);
        storage1.put(TEST_KEY, TEST_STRING);
        storage2.put(TEST_KEY, TEST_STRING);
        storage3.put(TEST_KEY, TEST_STRING);
        storage4.put(TEST_KEY, TEST_STRING);
        assertUserDatabaseSize(2);
        assertDeviceDatabaseSize(2);

        storage1.clear();

        assertEquals(0, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());

        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(2);

        storage3.clear();

        assertEquals(0, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(0, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());

        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(1);
    }

    public void testGetAll() throws Exception {
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), "test2",
                TrayStorage.Type.USER);
        storage2.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY, TEST_STRING);
        assertUserDatabaseSize(2);
        assertEquals(1, mStorage.getAll().size());
    }

    public void testGetDevice() throws Exception {
        final TrayStorage storage = new TrayStorage(getProviderMockContext(), "testGetDevice",
                TrayStorage.Type.DEVICE);
        assertNull(storage.get("test"));

        storage.put("test", "foo");
        final TrayItem item = storage.get("test");
        assertNotNull(item);
        assertEquals("test", item.key());
        assertEquals("foo", item.value());
    }

    public void testGetUser() throws Exception {
        final TrayStorage storage = new TrayStorage(getProviderMockContext(), "testGetUser",
                TrayStorage.Type.USER);
        assertNull(storage.get("test"));

        storage.put("test", "foo");
        final TrayItem item = storage.get("test");
        assertNotNull(item);
        assertEquals("test", item.key());
        assertEquals("foo", item.value());
    }

    public void testPutDevice() throws Exception {
        final TrayStorage storage =
                new TrayStorage(getProviderMockContext(), "device", TrayStorage.Type.DEVICE);
        storage.put(TEST_KEY, TEST_STRING);
        assertDeviceDatabaseSize(1);
        assertUserDatabaseSize(0);
    }

    public void testPutMultipleModules() throws Exception {
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), "test2",
                TrayStorage.Type.USER);
        storage2.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY, TEST_STRING);
        assertUserDatabaseSize(2);
    }

    public void testPutNullValue() throws Exception {
        //noinspection ConstantConditions
        mStorage.put(TEST_KEY, null);
        assertUserDatabaseSize(1);
    }

    public void testPutUser() throws Exception {
        final TrayStorage storage =
                new TrayStorage(getProviderMockContext(), "device", TrayStorage.Type.USER);
        storage.put(TEST_KEY, TEST_STRING);
        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(0);
    }

    public void testReadDataWithUndefinedStorageFromDeviceStore() throws Exception {
        final TrayStorage original = new TrayStorage(getProviderMockContext(), "storageName",
                TrayStorage.Type.DEVICE);
        original.setVersion(26);
        original.put(TEST_KEY, "someValue");
        assertNotNull(original.get(TEST_KEY));

        checkReadDataWithUndefined(original);
    }

    public void testReadDataWithUndefinedStorageFromUserStore() throws Exception {
        final TrayStorage original = new TrayStorage(getProviderMockContext(), "storageName",
                TrayStorage.Type.USER);
        original.setVersion(25);
        original.put(TEST_KEY, TEST_STRING);

        checkReadDataWithUndefined(original);
    }

    public void testRemove() throws Exception {
        mStorage.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY2, TEST_STRING2);
        assertUserDatabaseSize(2);
        mStorage.remove(TEST_KEY);
        assertUserDatabaseSize(1);
        final TrayItem item = mStorage.get(TEST_KEY2);
        assertNotNull(item);
        assertEquals(TEST_STRING2, item.value());
    }

    public void testRemoveIfItemIsNotThere() {
        mStorage.put(TEST_KEY2, TEST_STRING2);
        mStorage.remove(TEST_KEY);
        assertUserDatabaseSize(1);
    }

    public void testRemoveWithoutKey() throws Exception {
        try {
            //noinspection ConstantConditions
            mStorage.remove(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * writing data and version should fail
     */
    public void testUndefinedTypeAccessErrors() throws Exception {
        final TrayStorage storage = new TrayStorage(getProviderMockContext(), "undefined",
                TrayStorage.Type.UNDEFINED);

        // put
        try {
            storage.put(TEST_KEY2, TEST_STRING);
            fail();
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("UNDEFINED"));
        }
        try {
            storage.put(new TrayItem("undefined", TEST_KEY2, null, TEST_STRING, null, null));
            fail();
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("UNDEFINED"));
        }
        try {
            final TrayStorage someModule = new TrayStorage(
                    getProviderMockContext(), "someModule", TrayStorage.Type.USER);
            // without value -> no data reading -> no exception
            someModule.put(TEST_KEY, TEST_STRING);
            storage.annex(someModule);
            fail();
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("UNDEFINED"));
        }

        // setVersion
        try {
            storage.setVersion(10);
            fail();
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("UNDEFINED"));
        }

        assertEquals(TrayStorage.Type.UNDEFINED, storage.getType());
        storage.get(TEST_KEY);
        storage.getAll();
        storage.getVersion();
        storage.getModuleName();

    }

    public void testVersion() throws Exception {
        // default version, not set yet
        assertEquals(0, mStorage.getVersion());

        mStorage.setVersion(25);
        assertEquals(25, mStorage.getVersion());
    }

    public void testVersionAfterClearDevice() throws Exception {
        final TrayStorage storage = new TrayStorage(getProviderMockContext(),
                "testVersionAfterClearDevice",
                TrayStorage.Type.DEVICE);
        checkVersionAfterClear(storage);
    }

    public void testVersionAfterClearUser() throws Exception {
        final TrayStorage storage = new TrayStorage(getProviderMockContext(),
                "testVersionAfterClearUser",
                TrayStorage.Type.USER);
        checkVersionAfterClear(storage);
    }

    public void testWipe() throws Exception {
        mStorage.put("key", "value");
        assertEquals("value", mStorage.get("key").value());
        mStorage.setVersion(1);
        assertEquals(1, mStorage.getVersion());

        mStorage.wipe();
        assertNull(mStorage.get("key"));
        assertEquals(0, mStorage.getAll().size());
        assertEquals(0, mStorage.getVersion());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStorage = new TrayStorage(getProviderMockContext(), "test", TrayStorage.Type.USER);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mStorage = null;
    }

    private void checkReadDataWithUndefined(final TrayStorage original) {
        final TrayStorage undefined = new TrayStorage(getProviderMockContext(),
                original.getModuleName(),
                TrayStorage.Type.UNDEFINED);

        assertEquals(TrayStorage.Type.UNDEFINED, undefined.getType());
        assertEquals(original.getAll().size(), undefined.getAll().size());
        final TrayItem item = undefined.get(TEST_KEY);
        assertNotNull(item);
        assertEquals(original.get(TEST_KEY).value(), item.value());
        assertEquals(original.getVersion(), undefined.getVersion());
        assertEquals(original.getModuleName(), undefined.getModuleName());
    }

    private void checkVersionAfterClear(final TrayStorage storage) {
        storage.put("key", "value");
        final TrayItem key = storage.get("key");
        assertNotNull(key);
        assertEquals("value", key.value());
        storage.setVersion(1);
        assertEquals(1, storage.getVersion());

        storage.clear();
        assertNull(storage.get("key"));
        assertEquals(0, storage.getAll().size());
        assertEquals(1, storage.getVersion());
    }
}
