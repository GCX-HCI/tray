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

package net.grandcentrix.tray.provider;

import junit.framework.Assert;

import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayRuntimeException;
import net.grandcentrix.tray.core.TrayStorage;

import org.mockito.internal.util.reflection.Whitebox;

import android.net.Uri;

import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class ContentProviderStorageTest extends TrayProviderTestCase {

    final String TEST_KEY = "foo";

    final String TEST_KEY2 = "foo2";

    final String TEST_STRING = "fooBar";

    final String TEST_STRING2 = "fooBar2";

    public void testClear() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testClear1", TrayStorage.Type.USER);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testClear2", TrayStorage.Type.USER);
        final ContentProviderStorage storage3 = new ContentProviderStorage(getProviderMockContext(),
                "testClear3", TrayStorage.Type.DEVICE);
        final ContentProviderStorage storage4 = new ContentProviderStorage(getProviderMockContext(),
                "testClear4", TrayStorage.Type.DEVICE);
        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertTrue(storage2.put(TEST_KEY, TEST_STRING));
        assertTrue(storage3.put(TEST_KEY, TEST_STRING));
        assertTrue(storage4.put(TEST_KEY, TEST_STRING));
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

        // fill up again
        storage1.put(TEST_KEY, TEST_STRING);
        storage3.put(TEST_KEY, TEST_STRING);

        // test clear for undefined.
        // tricky because it's not clear which database has to be updated
        final ContentProviderStorage undefinedDevice = new ContentProviderStorage(
                getProviderMockContext(), "testClear4", TrayStorage.Type.UNDEFINED);
        final ContentProviderStorage undefinedUser = new ContentProviderStorage(
                getProviderMockContext(), "testClear2", TrayStorage.Type.UNDEFINED);

        undefinedDevice.clear();

        assertEquals(1, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(0, storage4.getAll().size());

        assertUserDatabaseSize(2);
        assertDeviceDatabaseSize(1);

        undefinedUser.clear();

        assertEquals(1, storage1.getAll().size());
        assertEquals(0, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(0, storage4.getAll().size());

        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(1);

    }

    public void testDeviceAndUserWithSameName() throws Exception {
        final ContentProviderStorage userStorage = new ContentProviderStorage(
                getProviderMockContext(), "sameName", TrayStorage.Type.USER);
        userStorage.setVersion(100);
        final ContentProviderStorage deviceStorage = new ContentProviderStorage(
                getProviderMockContext(), "sameName", TrayStorage.Type.DEVICE);
        deviceStorage.setVersion(200);

        assertEquals(100, userStorage.getVersion());
        assertEquals(200, deviceStorage.getVersion());

        // put
        assertTrue(userStorage.put(TEST_KEY, "A"));
        assertTrue(userStorage.put(TEST_KEY2, "a"));
        assertUserDatabaseSize(2);
        assertTrue(deviceStorage.put(TEST_KEY, "B"));
        assertTrue(deviceStorage.put(TEST_KEY2, "b"));
        assertDeviceDatabaseSize(2);

        // get
        final TrayItem itemA = userStorage.get(TEST_KEY);
        assertNotNull(itemA);
        assertEquals("A", itemA.value());

        final TrayItem itemB = deviceStorage.get(TEST_KEY);
        assertNotNull(itemB);
        assertEquals("B", itemB.value());

        // remove
        userStorage.remove(TEST_KEY);
        assertNull(userStorage.get(TEST_KEY));

        deviceStorage.remove(TEST_KEY);
        assertNull(deviceStorage.get(TEST_KEY));

        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(1);

        // fill up again
        assertTrue(userStorage.put(TEST_KEY, "A"));
        assertTrue(deviceStorage.put(TEST_KEY, "B"));
        assertUserDatabaseSize(2);
        assertDeviceDatabaseSize(2);

        /**
         * undefined
         */
        final ContentProviderStorage undefinedStorage = new ContentProviderStorage(
                getProviderMockContext(), "sameName", TrayStorage.Type.UNDEFINED);
        // get returns now two items!!!! order is not defined
        final TrayItem trayItem1 = undefinedStorage.get(TEST_KEY);
        assertNotNull(trayItem1);
        assertTrue(trayItem1.value().equals("A") || trayItem1.value().equals("B"));

        final TrayItem trayItem2 = undefinedStorage.get(TEST_KEY);
        assertNotNull(trayItem2);
        assertTrue(trayItem2.value().equals("A") || trayItem2.value().equals("B"));

        // remove removes both items
        undefinedStorage.remove(TEST_KEY);
        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(1);

        undefinedStorage.clear();
        assertUserDatabaseSize(0);
        assertDeviceDatabaseSize(0);

        assertNull(undefinedStorage.get(TEST_KEY));

        assertEquals(100, userStorage.getVersion());
        assertEquals(200, deviceStorage.getVersion());

        // undefinedStorage.getVersion() would return random 100 or 200. more likely 100 because it was inserted before

        assertTrue(undefinedStorage.wipe());
        assertEquals(0, userStorage.getVersion());
        assertEquals(0, deviceStorage.getVersion());
    }

    public void testFailedPutDevice() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testPut_Device", TrayStorage.Type.DEVICE);

        TrayProviderHelper mockHelper = mock(TrayProviderHelper.class);
        when(mockHelper.persist(any(Uri.class), anyString(), anyString())).thenReturn(false);
        Whitebox.setInternalState(storage, "mProviderHelper", mockHelper);

        assertFalse(storage.put(TEST_KEY, TEST_STRING));
        assertDeviceDatabaseSize(0);
        assertUserDatabaseSize(0);
    }

    public void testFailedPutUser() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testPut_Device", TrayStorage.Type.USER);

        TrayProviderHelper mockHelper = mock(TrayProviderHelper.class);
        when(mockHelper.persist(any(Uri.class), anyString(), anyString())).thenReturn(false);
        Whitebox.setInternalState(storage, "mProviderHelper", mockHelper);

        assertFalse(storage.put(TEST_KEY, TEST_STRING));
        assertDeviceDatabaseSize(0);
        assertUserDatabaseSize(0);
    }

    public void testGetAll() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testGetAll1", TrayStorage.Type.USER);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testGetAll2", TrayStorage.Type.USER);
        final ContentProviderStorage storage3 = new ContentProviderStorage(getProviderMockContext(),
                "testGetAll3", TrayStorage.Type.DEVICE);
        final ContentProviderStorage storage4 = new ContentProviderStorage(getProviderMockContext(),
                "testGetAll4", TrayStorage.Type.DEVICE);
        assertTrue(storage1.put(TEST_KEY, "1"));
        assertTrue(storage2.put(TEST_KEY, "2"));
        assertTrue(storage3.put(TEST_KEY, "3"));
        assertTrue(storage4.put(TEST_KEY, "4"));
        assertUserDatabaseSize(2);
        assertDeviceDatabaseSize(2);

        final Collection<TrayItem> all1 = storage1.getAll();
        assertEquals(1, all1.size());
        assertEquals("1", all1.iterator().next().value());

        final Collection<TrayItem> all2 = storage2.getAll();
        assertEquals(1, all2.size());
        assertEquals("2", all2.iterator().next().value());

        final Collection<TrayItem> all3 = storage3.getAll();
        assertEquals(1, all3.size());
        assertEquals("3", all3.iterator().next().value());

        final Collection<TrayItem> all4 = storage4.getAll();
        assertEquals(1, all4.size());
        assertEquals("4", all4.iterator().next().value());

        final ContentProviderStorage undefinedDevice = new ContentProviderStorage(
                getProviderMockContext(), "testGetAll4", TrayStorage.Type.UNDEFINED);
        final ContentProviderStorage undefinedUser = new ContentProviderStorage(
                getProviderMockContext(), "testGetAll2", TrayStorage.Type.UNDEFINED);

        final Collection<TrayItem> allD = undefinedDevice.getAll();
        assertEquals(1, allD.size());
        assertEquals("4", allD.iterator().next().value());

        final Collection<TrayItem> allU = undefinedUser.getAll();
        assertEquals(1, allU.size());
        assertEquals("2", allU.iterator().next().value());
    }

    public void testGetDevice() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testGet_Device", TrayStorage.Type.DEVICE);
        assertNull(storage.get("test"));

        assertTrue(storage.put("test", "foo"));
        final TrayItem item = storage.get("test");
        assertNotNull(item);
        assertEquals("test", item.key());
        assertEquals("foo", item.value());
    }

    public void testGetUser() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testGet_User", TrayStorage.Type.USER);
        assertNull(storage.get("test"));

        assertTrue(storage.put("test", "foo"));
        final TrayItem item = storage.get("test");
        assertNotNull(item);
        assertEquals("test", item.key());
        assertEquals("foo", item.value());
    }

    public void testPutDevice() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testPut_Device", TrayStorage.Type.DEVICE);
        assertTrue(storage.put(TEST_KEY, TEST_STRING));
        assertDeviceDatabaseSize(1);
        assertUserDatabaseSize(0);
    }

    public void testPutMultipleModules() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testPutMultipleModules1", TrayStorage.Type.USER);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testPutMultipleModules2", TrayStorage.Type.USER);
        final ContentProviderStorage storage3 = new ContentProviderStorage(getProviderMockContext(),
                "testPutMultipleModules3", TrayStorage.Type.DEVICE);
        final ContentProviderStorage storage4 = new ContentProviderStorage(getProviderMockContext(),
                "testPutMultipleModules4", TrayStorage.Type.DEVICE);
        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertTrue(storage2.put(TEST_KEY, TEST_STRING));
        assertTrue(storage3.put(TEST_KEY, TEST_STRING));
        assertTrue(storage4.put(TEST_KEY, TEST_STRING));
        assertUserDatabaseSize(2);
        assertDeviceDatabaseSize(2);
    }

    public void testPutNullValue() throws Exception {
        final ContentProviderStorage user = new ContentProviderStorage(getProviderMockContext(),
                "testPutNullValue_User", TrayStorage.Type.USER);
        assertTrue(user.put(TEST_KEY, null));
        assertUserDatabaseSize(1);

        final ContentProviderStorage device = new ContentProviderStorage(getProviderMockContext(),
                "testPutNullValue_Device", TrayStorage.Type.DEVICE);
        assertTrue(device.put(TEST_KEY, null));
        assertDeviceDatabaseSize(1);
    }

    public void testPutUser() throws Exception {
        final ContentProviderStorage storage =
                new ContentProviderStorage(getProviderMockContext(), "testPut_User",
                        TrayStorage.Type.USER);
        assertTrue(storage.put(TEST_KEY, TEST_STRING));
        assertUserDatabaseSize(1);
        assertDeviceDatabaseSize(0);
    }

    public void testReadDataWithUndefinedStorageFromDeviceStore() throws Exception {
        final ContentProviderStorage original = new ContentProviderStorage(getProviderMockContext(),
                "storageName", TrayStorage.Type.DEVICE);
        original.setVersion(26);
        original.put(TEST_KEY, "someValue");
        assertNotNull(original.get(TEST_KEY));

        checkReadDataWithUndefined(original);
    }

    public void testReadDataWithUndefinedStorageFromUserStore() throws Exception {
        final ContentProviderStorage original = new ContentProviderStorage(getProviderMockContext(),
                "storageName", TrayStorage.Type.USER);
        original.setVersion(25);
        assertTrue(original.put(TEST_KEY, TEST_STRING));

        checkReadDataWithUndefined(original);
    }

    public void testRemove() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove1", TrayStorage.Type.USER);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove2", TrayStorage.Type.USER);
        final ContentProviderStorage storage3 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove3", TrayStorage.Type.DEVICE);
        final ContentProviderStorage storage4 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove4", TrayStorage.Type.DEVICE);
        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertTrue(storage1.put(TEST_KEY2, TEST_STRING2));
        assertTrue(storage2.put(TEST_KEY, TEST_STRING));
        assertTrue(storage2.put(TEST_KEY2, TEST_STRING2));
        assertTrue(storage3.put(TEST_KEY, TEST_STRING));
        assertTrue(storage3.put(TEST_KEY2, TEST_STRING2));
        assertTrue(storage4.put(TEST_KEY, TEST_STRING));
        assertTrue(storage4.put(TEST_KEY2, TEST_STRING2));
        assertUserDatabaseSize(4);
        assertDeviceDatabaseSize(4);

        // remove from user storage
        storage1.remove(TEST_KEY);
        assertEquals(1, storage1.getAll().size());
        assertEquals(2, storage2.getAll().size());
        assertEquals(2, storage3.getAll().size());
        assertEquals(2, storage4.getAll().size());
        assertNotNull(storage1.get(TEST_KEY2));

        // remove from device storage
        storage3.remove(TEST_KEY);
        assertEquals(1, storage1.getAll().size());
        assertEquals(2, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(2, storage4.getAll().size());
        assertNotNull(storage3.get(TEST_KEY2));

        final ContentProviderStorage undefinedDevice = new ContentProviderStorage(
                getProviderMockContext(), "testRemove4", TrayStorage.Type.UNDEFINED);
        final ContentProviderStorage undefinedUser = new ContentProviderStorage(
                getProviderMockContext(), "testRemove2", TrayStorage.Type.UNDEFINED);

        undefinedDevice.remove(TEST_KEY);
        assertEquals(1, storage1.getAll().size());
        assertEquals(2, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());
        assertNotNull(undefinedDevice.get(TEST_KEY2));

        undefinedUser.remove(TEST_KEY);
        assertEquals(1, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());
        assertNotNull(undefinedUser.get(TEST_KEY2));
    }

    public void testRemoveFailed() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove1", TrayStorage.Type.USER);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testRemove2", TrayStorage.Type.DEVICE);
        assertFalse(storage1.remove(TEST_KEY));
        assertFalse(storage2.remove(TEST_KEY));
    }

    public void testRemoveIfItemIsNotThere() {
        final ContentProviderStorage storageUser = new ContentProviderStorage(
                getProviderMockContext(), "testRemoveIfItemIsNotThere_User", TrayStorage.Type.USER);
        assertTrue(storageUser.put(TEST_KEY2, TEST_STRING));
        storageUser.remove(TEST_KEY);
        assertUserDatabaseSize(1);

        final ContentProviderStorage storageDevice = new ContentProviderStorage(
                getProviderMockContext(), "testRemoveIfItemIsNotThere_Device",
                TrayStorage.Type.DEVICE);
        assertTrue(storageDevice.put(TEST_KEY2, TEST_STRING2));
        storageDevice.remove(TEST_KEY);
        assertUserDatabaseSize(1);
    }

    // not important if Type.USER or Type.DEVICE
    public void testRemoveWithoutKey() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testRemoveWithoutKey", TrayStorage.Type.USER);
        try {
            //noinspection ConstantConditions
            storage.remove(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            final String msg = e.getMessage();
            assertTrue(msg.contains("null"));
            assertTrue(msg.contains("clear"));
            assertTrue(msg.contains("wipe"));
        }
    }

    // not really necessary but required for 100% test coverage
    public void testStorageType() throws Exception {
        final TrayStorage.Type[] values = TrayStorage.Type.values();
        assertEquals(3, values.length);

        final TrayStorage.Type typeUser = TrayStorage.Type.valueOf("USER");
        assertEquals(TrayStorage.Type.USER, typeUser);
        final TrayStorage.Type typeDevice = TrayStorage.Type.valueOf("DEVICE");
        assertEquals(TrayStorage.Type.DEVICE, typeDevice);
        final TrayStorage.Type typeUndefined = TrayStorage.Type.valueOf("UNDEFINED");
        assertEquals(TrayStorage.Type.UNDEFINED, typeUndefined);
    }

    /**
     * writing data and version should fail for an undefined storage type
     */
    public void testUndefinedTypeAccessErrors() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "undefined",
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
            final ContentProviderStorage someModule = new ContentProviderStorage(
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
        final ContentProviderStorage storageUser = new ContentProviderStorage(
                getProviderMockContext(),
                "testVersion_User", TrayStorage.Type.USER);
        // default version, not set yet
        assertEquals(0, storageUser.getVersion());

        storageUser.setVersion(25);
        assertEquals(25, storageUser.getVersion());

        final ContentProviderStorage storageDevice = new ContentProviderStorage(
                getProviderMockContext(),
                "testVersion_Device", TrayStorage.Type.DEVICE);
        // default version, not set yet
        assertEquals(0, storageDevice.getVersion());

        storageDevice.setVersion(25);
        assertEquals(25, storageDevice.getVersion());
    }

    public void testVersionAfterClearDevice() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testVersionAfterClear_Device", TrayStorage.Type.DEVICE);
        checkVersionAfterClear(storage);
    }

    public void testVersionAfterClearUser() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(getProviderMockContext(),
                "testVersionAfterClear_User", TrayStorage.Type.USER);
        checkVersionAfterClear(storage);
    }

    public void testWipe() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testWipe1", TrayStorage.Type.USER);
        storage1.setVersion(1);
        final ContentProviderStorage storage2 = new ContentProviderStorage(getProviderMockContext(),
                "testWipe2", TrayStorage.Type.USER);
        storage2.setVersion(1);
        final ContentProviderStorage storage3 = new ContentProviderStorage(getProviderMockContext(),
                "testWipe3", TrayStorage.Type.DEVICE);
        storage3.setVersion(1);
        final ContentProviderStorage storage4 = new ContentProviderStorage(getProviderMockContext(),
                "testWipe4", TrayStorage.Type.DEVICE);
        storage4.setVersion(1);

        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertTrue(storage2.put(TEST_KEY, TEST_STRING));
        assertTrue(storage3.put(TEST_KEY, TEST_STRING));
        assertTrue(storage4.put(TEST_KEY, TEST_STRING));

        assertEquals(1, storage1.getVersion());
        assertEquals(1, storage2.getVersion());
        assertEquals(1, storage3.getVersion());
        assertEquals(1, storage4.getVersion());

        assertTrue(storage1.wipe());

        assertEquals(0, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());

        assertEquals(0, storage1.getVersion());
        assertEquals(1, storage2.getVersion());
        assertEquals(1, storage3.getVersion());
        assertEquals(1, storage4.getVersion());

        assertTrue(storage3.wipe());

        assertEquals(0, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(0, storage3.getAll().size());
        assertEquals(1, storage4.getAll().size());

        assertEquals(0, storage1.getVersion());
        assertEquals(1, storage2.getVersion());
        assertEquals(0, storage3.getVersion());
        assertEquals(1, storage4.getVersion());

        // fill up again
        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertTrue(storage1.setVersion(1));
        assertTrue(storage3.put(TEST_KEY, TEST_STRING));
        assertTrue(storage3.setVersion(1));

        // test clear for undefined.
        // tricky because it's not clear which database has to be updated
        final ContentProviderStorage undefinedDevice = new ContentProviderStorage(
                getProviderMockContext(), "testWipe4", TrayStorage.Type.UNDEFINED);
        final ContentProviderStorage undefinedUser = new ContentProviderStorage(
                getProviderMockContext(), "testWipe2", TrayStorage.Type.UNDEFINED);

        assertTrue(undefinedDevice.wipe());

        assertEquals(1, storage1.getAll().size());
        assertEquals(1, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(0, storage4.getAll().size());

        assertEquals(1, storage1.getVersion());
        assertEquals(1, storage2.getVersion());
        assertEquals(1, storage3.getVersion());
        assertEquals(0, storage4.getVersion());

        assertTrue(undefinedUser.wipe());

        assertEquals(1, storage1.getAll().size());
        assertEquals(0, storage2.getAll().size());
        assertEquals(1, storage3.getAll().size());
        assertEquals(0, storage4.getAll().size());

        assertEquals(1, storage1.getVersion());
        assertEquals(0, storage2.getVersion());
        assertEquals(1, storage3.getVersion());
        assertEquals(0, storage4.getVersion());
    }

    public void testWipeFails() throws Exception {
        final ContentProviderStorage storage1 = new ContentProviderStorage(getProviderMockContext(),
                "testWipe1", TrayStorage.Type.USER) {
            @Override
            public boolean clear() {
                return false;
            }
        };
        storage1.setVersion(1);
        assertTrue(storage1.put(TEST_KEY, TEST_STRING));
        assertEquals(1, storage1.getVersion());

        assertFalse(storage1.wipe());
    }

    private void checkReadDataWithUndefined(final ContentProviderStorage original)
            throws Exception {
        final ContentProviderStorage undefined = new ContentProviderStorage(
                getProviderMockContext(), original.getModuleName(), TrayStorage.Type.UNDEFINED);

        assertEquals(TrayStorage.Type.UNDEFINED, undefined.getType());
        assertEquals(original.getAll().size(), undefined.getAll().size());
        final TrayItem item = undefined.get(TEST_KEY);
        assertNotNull(item);
        assertEquals(original.get(TEST_KEY).value(), item.value());
        assertEquals(original.getVersion(), undefined.getVersion());
        assertEquals(original.getModuleName(), undefined.getModuleName());
    }

    private void checkVersionAfterClear(final ContentProviderStorage storage) throws Exception {
        assertTrue(storage.put("key", "value"));
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
