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

import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

public class TrayModulePreferencesTest extends TrayProviderTestCase {

    public void testGetContext() throws Exception {
        final TrayModulePreferences modulePreferences = new TrayModulePreferences(
                getProviderMockContext(), "test", 1) {

            @Override
            protected void onCreate(final int newVersion) {

            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {

            }
        };

        assertEquals(getProviderMockContext().getApplicationContext(),
                modulePreferences.getContext());
    }

    public void testAnnexModule() throws Exception {
        final TrayModulePreferences modulePreferences = new TrayModulePreferences(
                getProviderMockContext(), "test", 1) {

            @Override
            protected void onCreate(final int newVersion) {

            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {

            }
        };
        assertEquals(0, modulePreferences.getAll().size());
        modulePreferences.annexModule("nothing");
        assertEquals(0, modulePreferences.getAll().size());

        final TrayModulePreferences oldPrefs = new TestTrayModulePreferences(
                getProviderMockContext(), "old");
        oldPrefs.put("key", "value");
        assertEquals(1, oldPrefs.getAll().size());

        modulePreferences.annexModule("old");
        assertEquals(1, modulePreferences.getAll().size());
        assertEquals(0, oldPrefs.getAll().size());
    }
}