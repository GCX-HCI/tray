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

package net.grandcentrix.tray.core;

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockTrayStorage;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public class ModularizedAccessorTest extends TestCase {

    private class MockTrayPreferences extends AbstractTrayPreference<MockTrayStorage> {

        public MockTrayPreferences() {
            super(new MockTrayStorage("ModularizedAccessorTest"), 1);
        }
    }

    final boolean TEST_BOOL = true;

    final float TEST_FLOAT = 4.2f;

    final int TEST_INT = 42;

    final String TEST_KEY = "foo";

    final long TEST_LONG = Long.MAX_VALUE - 123l;

    final String TEST_STRING = "fooBar";

    public void testBoolean() throws Exception {
        final MockTrayPreferences accessor = new MockTrayPreferences();
        accessor.put(TEST_KEY, TEST_BOOL);
        assertEquals(TEST_BOOL, accessor.getBoolean(TEST_KEY, false));
    }

    public void testFloat() throws Exception {
        final MockTrayPreferences accessor = new MockTrayPreferences();
        accessor.put(TEST_KEY, TEST_FLOAT);
        assertEquals(TEST_FLOAT, accessor.getFloat(TEST_KEY, -1f));
    }

    public void testGetModularizedStorage() throws Exception {
        final MockTrayPreferences mockTrayPreference = new MockTrayPreferences();
        final TrayStorage trayStorage = mockTrayPreference.getStorage();
        assertNotNull(trayStorage);
        assertEquals(mockTrayPreference.getStorage(), mockTrayPreference.getStorage());
    }

    public void testInt() throws Exception {
        final MockTrayPreferences accessor = new MockTrayPreferences();
        accessor.put(TEST_KEY, TEST_INT);
        assertEquals(TEST_INT, accessor.getInt(TEST_KEY, -1));
    }

    public void testLong() throws Exception {
        final MockTrayPreferences accessor = new MockTrayPreferences();
        accessor.put(TEST_KEY, TEST_LONG);
        assertEquals(TEST_LONG, accessor.getLong(TEST_KEY, -1l));
    }

    public void testString() throws Exception {
        final MockTrayPreferences accessor = new MockTrayPreferences();
        accessor.put(TEST_KEY, TEST_STRING);
        assertEquals(TEST_STRING, accessor.getString(TEST_KEY, "unknown"));
    }
}
