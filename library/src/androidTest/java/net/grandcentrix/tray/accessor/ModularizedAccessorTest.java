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

import net.grandcentrix.tray.mock.MockModularizedStringStorage;

import android.test.AndroidTestCase;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public class ModularizedAccessorTest extends AndroidTestCase {

    public class MockModuleAccessor extends Preference<String> {

        public MockModuleAccessor(final String name) {
            super(new MockModularizedStringStorage(name), 1);
        }

        @Override
        protected void onCreate(final int newVersion) {

        }

        @Override
        protected void onUpgrade(final int oldVersion, final int newVersion) {

        }

        @Override
        public boolean getBoolean(final String key, final boolean defaultValue) {
            return Boolean.parseBoolean(getStorage().get(key));
        }

        @Override
        public float getFloat(final String key, final float defaultValue) {
            return Float.parseFloat(getStorage().get(key));
        }

        @Override
        public int getInt(final String key, final int defaultValue) {
            return Integer.parseInt(getStorage().get(key));
        }

        @Override
        public long getLong(final String key, final long defaultValue) {
            return Long.parseLong(getStorage().get(key));
        }

        @Override
        public String getString(final String key, final String defaultValue) {
            return getStorage().get(key);
        }
    }

    final boolean TEST_BOOL = true;

    final float TEST_FLOAT = 4.2f;

    final int TEST_INT = 42;

    final String TEST_KEY = "foo";

    final long TEST_LONG = Long.MAX_VALUE - 123l;

    final String TEST_MODULE = "common";

    final String TEST_STRING = "fooBar";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBoolean() throws Exception {
        final MockModuleAccessor accessor = new MockModuleAccessor(TEST_MODULE);
        accessor.put(TEST_KEY, TEST_BOOL);
        assertEquals(TEST_BOOL, accessor.getBoolean(TEST_KEY, false));
    }

    public void testFloat() throws Exception {
        final MockModuleAccessor accessor = new MockModuleAccessor(TEST_MODULE);
        accessor.put(TEST_KEY, TEST_FLOAT);
        assertEquals(TEST_FLOAT, accessor.getFloat(TEST_KEY, -1f));
    }

    public void testInt() throws Exception {
        final MockModuleAccessor accessor = new MockModuleAccessor(TEST_MODULE);
        accessor.put(TEST_KEY, TEST_INT);
        assertEquals(TEST_INT, accessor.getInt(TEST_KEY, -1));
    }

    public void testLong() throws Exception {
        final MockModuleAccessor accessor = new MockModuleAccessor(TEST_MODULE);
        accessor.put(TEST_KEY, TEST_LONG);
        assertEquals(TEST_LONG, accessor.getLong(TEST_KEY, -1l));
    }

    public void testString() throws Exception {
        final MockModuleAccessor accessor = new MockModuleAccessor(TEST_MODULE);
        accessor.put(TEST_KEY, TEST_STRING);
        assertEquals(TEST_STRING, accessor.getString(TEST_KEY, "unknown"));
    }
}
