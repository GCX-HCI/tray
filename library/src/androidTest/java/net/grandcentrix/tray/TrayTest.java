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

import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

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
        mTray.clear(mTrayModulePref);
        assertUserDatabaseSize(1);

        mTray.clear(module2);
        assertUserDatabaseSize(0);
    }

    public void testClearAll() throws Exception {
        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        module2.put("blubb", "hello");
        mTrayModulePref.put("test", "test");
        assertUserDatabaseSize(2);
        mTray.clear();
        assertUserDatabaseSize(0);
    }

    public void testClearBut() throws Exception {

        final TestTrayModulePreferences module2 =
                new TestTrayModulePreferences(getProviderMockContext(), "module2");
        mTrayModulePref.put("test", "test");
        module2.put("test", "test");
        mTrayModulePref.put("test2", "test");
        module2.put("test2", "test");
        assertUserDatabaseSize(4);

        mTray.clearBut(module2);
        assertUserDatabaseSize(2);

        final AppPreferences appPrefs = new AppPreferences(getProviderMockContext());
        appPrefs.put("test", "value");

        assertUserDatabaseSize(3);

        mTray.clearBut(appPrefs);
        assertUserDatabaseSize(1);

        mTray.clear();
        assertUserDatabaseSize(0);
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

        mTray.wipe();

        assertEquals(0, mTrayModulePref.getVersion());
        assertEquals(0, module2.getVersion());
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
