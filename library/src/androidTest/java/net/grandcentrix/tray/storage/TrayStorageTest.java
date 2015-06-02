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

import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderHelper;
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
        final String MODULE2 = "test2";
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), MODULE2);
        storage2.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY, TEST_STRING);
        assertDatabaseSize(2);

        mStorage.clear();
        final TrayProviderHelper trayProviderHelper = new TrayProviderHelper(getMockContext());
        assertDatabaseSize(trayProviderHelper.getUri(MODULE2), 1, true);
    }

    public void testGet() throws Exception {
        assertNull(mStorage.get("something"));

        mStorage.put("test", "foo");
        final TrayItem item = mStorage.get("test");
        assertNotNull(item);
        assertEquals("test", item.key());
        assertEquals("foo", item.value());
    }

    public void testGetAll() throws Exception {
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), "test2");
        storage2.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY, TEST_STRING);
        assertDatabaseSize(2);
        assertEquals(1, mStorage.getAll().size());
    }

    public void testPut() throws Exception {
        mStorage.put(TEST_KEY, TEST_STRING);
        assertDatabaseSize(1);
    }

    public void testPutMultipleModules() throws Exception {
        final TrayStorage storage2 = new TrayStorage(getProviderMockContext(), "test2");
        storage2.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY, TEST_STRING);
        assertDatabaseSize(2);
    }

    public void testPutNullValue() throws Exception {
        //noinspection ConstantConditions
        mStorage.put(TEST_KEY, null);
        assertDatabaseSize(1);
    }

    public void testRemove() throws Exception {
        mStorage.put(TEST_KEY, TEST_STRING);
        mStorage.put(TEST_KEY2, TEST_STRING2);
        assertDatabaseSize(2);
        mStorage.remove(TEST_KEY);
        assertDatabaseSize(1);
        final TrayItem item = mStorage.get(TEST_KEY2);
        assertNotNull(item);
        assertEquals(TEST_STRING2, item.value());
    }

    public void testRemoveIfItemIsNotThere() {
        mStorage.put(TEST_KEY2, TEST_STRING2);
        mStorage.remove(TEST_KEY);
        assertDatabaseSize(1);
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

    public void testVersion() throws Exception {
        // default version, not set yet
        assertEquals(0, mStorage.getVersion());

        mStorage.setVersion(25);
        assertEquals(25, mStorage.getVersion());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStorage = new TrayStorage(getProviderMockContext(), "test");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mStorage = null;
    }
}
