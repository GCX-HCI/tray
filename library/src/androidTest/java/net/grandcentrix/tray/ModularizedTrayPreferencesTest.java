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

package net.grandcentrix.tray;

import junit.framework.TestCase;

import net.grandcentrix.tray.mock.MockModularizedStorage;

public class ModularizedTrayPreferencesTest extends TestCase {

    private static final String KEY = "key";

    private static final String WRONG_KEY = "wrong_key";

    final boolean TEST_BOOL = true;

    final float TEST_FLOAT = 4.2f;

    final int TEST_INT = 42;

    final long TEST_LONG = Long.MAX_VALUE - 123l;

    final String TEST_STRING = "fooBar";

    private ModularizedTrayPreferences mTrayAccessor;

    public void testAnnexModule() throws Exception {
        final MockSimplePreferences modulePreferences = new MockSimplePreferences(
                new MockModularizedStorage("test"), 1);
        assertEquals(0, modulePreferences.getAll().size());
        modulePreferences.annex(new MockModularizedStorage("other"));
        assertEquals(0, modulePreferences.getAll().size());
        final MockModularizedStorage oldStorage = new MockModularizedStorage("old");
        final MockSimplePreferences oldPrefs = new MockSimplePreferences(oldStorage, 1);
        oldPrefs.put("key", "value");
        assertEquals(1, oldPrefs.getAll().size());
        modulePreferences.annex(oldStorage);
        assertEquals(1, modulePreferences.getAll().size());
        assertEquals(0, oldPrefs.getAll().size());
    }

    public void testBoolean() throws Exception {
        mTrayAccessor.put(KEY, TEST_BOOL);
        assertEquals(TEST_BOOL, mTrayAccessor.getBoolean(KEY, false));
        assertEquals(false, mTrayAccessor.getBoolean(WRONG_KEY, false));
    }

    public void testFloat() throws Exception {
        mTrayAccessor.put(KEY, TEST_FLOAT);
        assertEquals(TEST_FLOAT, mTrayAccessor.getFloat(KEY, 0f));
        assertEquals(0f, mTrayAccessor.getFloat(WRONG_KEY, 0f));
    }

    public void testInt() throws Exception {
        mTrayAccessor.put(KEY, TEST_INT);
        assertEquals(TEST_INT, mTrayAccessor.getInt(KEY, 0));
        assertEquals(0, mTrayAccessor.getInt(WRONG_KEY, 0));
    }

    public void testLong() throws Exception {
        mTrayAccessor.put(KEY, TEST_LONG);
        assertEquals(TEST_LONG, mTrayAccessor.getLong(KEY, 0l));
        assertEquals(0l, mTrayAccessor.getLong(WRONG_KEY, 0l));
    }

    public void testString() throws Exception {
        mTrayAccessor.put(KEY, TEST_STRING);
        assertEquals(TEST_STRING, mTrayAccessor.getString(KEY, ""));
        assertEquals("", mTrayAccessor.getString(WRONG_KEY, ""));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTrayAccessor = new ModularizedTrayPreferences<MockModularizedStorage>(
                new MockModularizedStorage("test"), 1) {
        };

    }

}