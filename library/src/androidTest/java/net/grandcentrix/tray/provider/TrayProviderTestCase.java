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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.IsolatedContext;
import android.test.ProviderTestCase2;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class TrayProviderTestCase extends ProviderTestCase2<TrayProvider> {

    public static final String AUTHORITY = "net.grandcentrix.tray.test";

    private IsolatedContext mIsolatedContext;

    public TrayProviderTestCase() {
        super(TrayProvider.class, AUTHORITY);
        TrayProvider.setAuthority(AUTHORITY);
    }

    public IsolatedContext getProviderMockContext() {
        return mIsolatedContext;
    }

    /**
     * checks the database size by querying the given {@param contentUri}
     *
     * @param expectedSize the number of items you expect
     */
    protected void assertDatabaseSize(final long expectedSize) {
        assertDatabaseSize(TrayProvider.CONTENT_URI, expectedSize, true);
    }

    /**
     * checks the database size by querying the given {@param contenUri}
     *
     * @param contentUri   uri to query
     * @param expectedSize the number of items you expect
     * @param closeCursor  should the returned cursor be closed?
     */
    protected Cursor assertDatabaseSize(final Uri contentUri, final long expectedSize,
            final boolean closeCursor) {
        Cursor cursor = getMockContentResolver().query(contentUri, null, null, null, null);
        // Move to first, or the cursor count is always 0
        cursor.moveToFirst();
        assertEquals(expectedSize, cursor.getCount());

        if (closeCursor) {
            cursor.close();
        }

        return cursor;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getMockContentResolver().delete(TrayProvider.CONTENT_URI, null, null);

        assertDatabaseSize(0);

        mIsolatedContext = buildIsolatedContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getMockContentResolver().delete(TrayProvider.CONTENT_URI, null, null);
    }

    public IsolatedContext buildIsolatedContext() {
        return new IsolatedContext(
                getMockContext().getContentResolver(), getMockContext()) {
            @Override
            public String getPackageName() {
                return "package.test";
            }

            @Override
            public Context getApplicationContext() {
                return this;
            }
        };
    }
}
