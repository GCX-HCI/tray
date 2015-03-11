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

public class TrayDBHelperTest extends TrayProviderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testInstantiation() throws Exception {
        new TrayDBHelper(getProviderMockContext());
    }

    public void testOnUpgrade() throws Exception {
        final TrayDBHelper trayDBHelper = new TrayDBHelper(getProviderMockContext());
        try {
            trayDBHelper.onUpgrade(trayDBHelper.getWritableDatabase(), 0, 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("version"));
        }
    }
}