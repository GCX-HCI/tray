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

package net.grandcentrix.tray.accessor;

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockModularizedStorage;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.storage.ModularizedStorage;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public class ModularizedAccessorTest extends TestCase {

    private class MockTrayPreference extends TrayPreference {

        public MockTrayPreference() {
            super(new MockModularizedStorage("ModularizedAccessorTest"), 1);
        }

        @Override
        protected void onCreate(final int newVersion) {

        }

        @Override
        protected void onUpgrade(final int oldVersion, final int newVersion) {

        }
    }

    final boolean TEST_BOOL = true;

    final float TEST_FLOAT = 4.2f;

    final int TEST_INT = 42;

    final String TEST_KEY = "foo";

    final long TEST_LONG = Long.MAX_VALUE - 123l;

    final String TEST_STRING = "fooBar";

    public void testBoolean() throws Exception {
        final MockTrayPreference accessor = new MockTrayPreference();
        accessor.put(TEST_KEY, TEST_BOOL);
        assertEquals(TEST_BOOL, accessor.getBoolean(TEST_KEY, false));
    }

    public void testFloat() throws Exception {
        final MockTrayPreference accessor = new MockTrayPreference();
        accessor.put(TEST_KEY, TEST_FLOAT);
        assertEquals(TEST_FLOAT, accessor.getFloat(TEST_KEY, -1f));
    }

    public void testGetModularizedStorage() throws Exception {
        final MockTrayPreference mockTrayPreference = new MockTrayPreference();
        final ModularizedStorage<TrayItem> modularizedStorage = mockTrayPreference
                .getModularizedStorage();
        assertNotNull(modularizedStorage);
        assertEquals(mockTrayPreference.getStorage(), mockTrayPreference.getModularizedStorage());

    }

    public void testInt() throws Exception {
        final MockTrayPreference accessor = new MockTrayPreference();
        accessor.put(TEST_KEY, TEST_INT);
        assertEquals(TEST_INT, accessor.getInt(TEST_KEY, -1));
    }

    public void testLong() throws Exception {
        final MockTrayPreference accessor = new MockTrayPreference();
        accessor.put(TEST_KEY, TEST_LONG);
        assertEquals(TEST_LONG, accessor.getLong(TEST_KEY, -1l));
    }

    public void testString() throws Exception {
        final MockTrayPreference accessor = new MockTrayPreference();
        accessor.put(TEST_KEY, TEST_STRING);
        assertEquals(TEST_STRING, accessor.getString(TEST_KEY, "unknown"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
