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

import net.grandcentrix.tray.mock.MockPreferences;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

import android.test.IsolatedContext;

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/27/14.
 */
public class TrayTest extends TrayProviderTestCase {

    private Tray mTray;

    private TrayModulePreferences mTrayModulePref;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTrayModulePref = new MockPreferences(getMockContext(), "module");
        mTray = new Tray(getMockContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mTrayModulePref = null;
        mTray = null;
    }

    public void testClearAll() throws Exception {
        final IsolatedContext context = new IsolatedContext(
                getMockContext().getContentResolver(), getMockContext()) {
            @Override
            public String getPackageName() {
                return "package.test";
            }
        };
        final TrayModulePreferences module2 = new MockPreferences(context, "module2");
        module2.put("blubb", "hello");
        mTrayModulePref.put("test", "test");
        assertDatabaseSize(2);
        mTray.clear();
        assertDatabaseSize(0);
    }

    public void testGetAll() throws Exception {
        // We need a package name in this test, thus creating our own mock context
        final IsolatedContext context = new IsolatedContext(
                getMockContext().getContentResolver(), getMockContext()) {
            @Override
            public String getPackageName() {
                return "package.test";
            }
        };
        final TrayModulePreferences module2 = new MockPreferences(context, "module2");

        mTrayModulePref.put("test", "test");
        module2.put("test", "test");
        mTrayModulePref.put("test2", "test");
        module2.put("test2", "test");
        assertDatabaseSize(4);

        final Collection<TrayItem> all = mTray.getAll();
        assertEquals(4, all.size());
    }

    public void testClearBut() throws Exception {

        // We need a package name in this test, thus creating our own mock context
        final IsolatedContext context = new IsolatedContext(
                getMockContext().getContentResolver(), getMockContext()) {
            @Override
            public String getPackageName() {
                return "package.test";
            }
        };

        final TrayModulePreferences module2 = new MockPreferences(context, "module2");
        mTrayModulePref.put("test", "test");
        module2.put("test", "test");
        mTrayModulePref.put("test2", "test");
        module2.put("test2", "test");
        assertDatabaseSize(4);

        mTray.clearBut(module2);
        assertDatabaseSize(2);

        final TrayAppPreferences appPrefs = new TrayAppPreferences(context);
        appPrefs.put("test", "value");

        assertDatabaseSize(3);

        mTray.clearBut(appPrefs);
        assertDatabaseSize(1);

        mTray.clear();
        assertDatabaseSize(0);
    }
}
