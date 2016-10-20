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

import junit.framework.Assert;

import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.MockProvider;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

import android.net.Uri;
import android.test.mock.MockContentProvider;

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/27/14.
 */
public class TrayTest extends TrayProviderTestCase {

    private Tray mTray;

    private TrayPreferences mTrayModulePref;

    public void testClear() throws Exception {
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        module2.put("blubb", "hello");
        mTrayModulePref.put("test", "test");
        assertUserDatabaseSize(2);
        Tray.clear(mTrayModulePref);
        assertUserDatabaseSize(1);

        Tray.clear(module2);
        assertUserDatabaseSize(0);
    }

    public void testClearAll() throws Exception {
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        module2.put("blubb", "hello");
        mTrayModulePref.put("test", "test");
        assertUserDatabaseSize(2);
        assertTrue(mTray.clear());
        assertUserDatabaseSize(0);
    }

    public void testClearAllFails() throws Exception {
        final MockContentProvider mockContentProvider = new MockContentProvider(
                getProviderMockContext()) {
            @Override
            public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
                throw new IllegalStateException("something serious is wrong");
            }
        };
        getProviderMockContext().addProvider(MockProvider.AUTHORITY, mockContentProvider);
        getProviderMockContext().enableMockResolver(true);
        final Tray tray = new Tray(getProviderMockContext());

        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        module2.put("blubb", "hello");
        mTrayModulePref.put("test", "test");
        assertUserDatabaseSize(2);
        assertFalse(tray.clear());
        assertUserDatabaseSize(2);
    }

    public void testClearBut() throws Exception {

        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        mTrayModulePref.put("test", "test");
        module2.put("test", "test");
        mTrayModulePref.put("test2", "test");
        module2.put("test2", "test");
        assertUserDatabaseSize(4);

        assertTrue(mTray.clearBut(module2));
        assertUserDatabaseSize(2);

        final AppPreferences appPrefs = new AppPreferences(getProviderMockContext());
        appPrefs.put("test", "value");

        assertUserDatabaseSize(3);

        assertTrue(mTray.clearBut(appPrefs));
        assertUserDatabaseSize(1);

        assertTrue(mTray.clear());
        assertUserDatabaseSize(0);
    }


    public void testClearButFails() throws Exception {
        final MockContentProvider mockContentProvider = new MockContentProvider(
                getProviderMockContext()) {
            @Override
            public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
                throw new IllegalStateException("something serious is wrong");
            }
        };
        getProviderMockContext().addProvider(MockProvider.AUTHORITY, mockContentProvider);
        getProviderMockContext().enableMockResolver(true);
        final Tray tray = new Tray(getProviderMockContext());

        tray.clearBut(new AppPreferences(getProviderMockContext()));
    }

    public void testEmptyKey() throws Exception {
        final TestTrayModulePreferences module =
                new TestTrayModulePreferences(getProviderMockContext(), "module");
        try {
            module.put("", "test");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Preference key value cannot be empty.", e.getMessage());
        }
    }

    public void testClearModules() throws Exception {
        final TestTrayModulePreferences module1 =
                new TestTrayModulePreferences(getProviderMockContext(), "module1");
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");

        module1.put("test", "test");
        module2.put("test", "test");
        module1.put("test2", "test");
        module2.put("test2", "test");
        assertUserDatabaseSize(4);

        Tray.clear(new TestTrayModulePreferences(getProviderMockContext(), "module1"));
        assertUserDatabaseSize(2);

        Tray.clear(new TestTrayModulePreferences(getProviderMockContext(), "module2"));
        assertUserDatabaseSize(0);

        module1.put("test", "test");
        module2.put("test", "test");

        Tray.clear((TrayPreferences) null);
        assertUserDatabaseSize(2);
    }

    public void testGetAll() throws Exception {
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");

        mTrayModulePref.put("test", "test");
        module2.put("test", "test");
        mTrayModulePref.put("test2", "test");
        module2.put("test2", "test");
        assertUserDatabaseSize(4);

        final Collection<TrayItem> all = mTray.getAll();
        assertEquals(4, all.size());
    }

    public void testWipe() throws Exception {
        assertEquals(1, mTrayModulePref.getVersion());
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        assertEquals(1, module2.getVersion());

        assertTrue(mTray.wipe());

        assertEquals(0, mTrayModulePref.getVersion());
        assertEquals(0, module2.getVersion());
    }

    public void testWipeFails() throws Exception {
        final MockContentProvider mockContentProvider = new MockContentProvider(
                getProviderMockContext()) {
            @Override
            public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
                throw new IllegalStateException("something serious is wrong");
            }
        };
        getProviderMockContext().addProvider(MockProvider.AUTHORITY, mockContentProvider);
        final Tray tray = new Tray(getProviderMockContext());
        getProviderMockContext().enableMockResolver(true);

        assertFalse(tray.wipe());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTrayModulePref = new TestTrayModulePreferences(getProviderMockContext(), "module");
        mTray = new Tray(getProviderMockContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mTrayModulePref = null;
        mTray = null;
    }
}
