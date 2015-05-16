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

import net.grandcentrix.tray.TrayAppPreferences;
import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.test.IsolatedContext;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class TrayProviderHelperTest extends TrayProviderTestCase {

    final String KEY_A = "foo";

    final String KEY_B = "foo2";

    final String MODULE_A = "common";

    final String MODULE_B = "common2";

    final String MODULE_C = "common3";

    final String STRING_A = "fooBar";

    final String STRING_B = "fooBar2";

    private TrayProviderHelper mProviderHelper;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mProviderHelper = new TrayProviderHelper(getProviderMockContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mProviderHelper = null;
    }

    public void testClear() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_B, STRING_B);
        assertDatabaseSize(4);

        mProviderHelper.clear();
        assertDatabaseSize(0);
    }

    public void testClearBut() throws Exception {
        // We need a package name in this test, thus creating our own mock context
        final IsolatedContext context = getProviderMockContext();

        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_C, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_C, KEY_B, STRING_B);
        mProviderHelper.persist(context.getPackageName(), KEY_A, STRING_A);
        mProviderHelper.persist(context.getPackageName(), KEY_B, STRING_B);
        assertDatabaseSize(8);

        mProviderHelper.clearBut(new TrayAppPreferences(context),
                new TestTrayModulePreferences(context, MODULE_A),
                new TestTrayModulePreferences(context, MODULE_B));
        assertDatabaseSize(6);

        mProviderHelper.clearBut(new TestTrayModulePreferences(context, MODULE_A),
                new TestTrayModulePreferences(context, MODULE_B));
        assertDatabaseSize(4);

        mProviderHelper.clearBut(new TestTrayModulePreferences(context, MODULE_A));
        assertDatabaseSize(2);

        mProviderHelper.clearBut((TrayPreference) null);
        assertDatabaseSize(0);

        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(context.getPackageName(), KEY_A, STRING_A);
        mProviderHelper.persist(context.getPackageName(), KEY_B, STRING_B);
        mProviderHelper.clearBut(new TrayAppPreferences(context));
        assertDatabaseSize(2);

        // Also test empty values (= clear everything)
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);

        mProviderHelper.clearBut((TrayPreference) null);
        assertDatabaseSize(0);
    }

    public void testClearModules() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_B, STRING_B);
        assertDatabaseSize(4);

        mProviderHelper.clear(new TestTrayModulePreferences(getProviderMockContext(), MODULE_A));
        assertDatabaseSize(2);

        mProviderHelper.clear(new TestTrayModulePreferences(getProviderMockContext(), MODULE_B));
        assertDatabaseSize(0);

        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);

        mProviderHelper.clear((TrayPreference) null);
        assertDatabaseSize(2);
    }

    public void testCreatedTime() throws Exception {
        final long start = System.currentTimeMillis();
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list);
        assertEquals(1, list.size());
        TrayItem itemA = list.get(0);
        assertNotNull(itemA.created());
        assertEqualsWithin(start, itemA.created().getTime(), 50l);
    }

    public void testCreatedTimeDoesNotChange() throws Exception {
        testCreatedTime();
        final TrayItem insertedItem = mProviderHelper.getAll().get(0);
        final long createdTime = insertedItem.created().getTime();

        Thread.sleep(50);

        // save again
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_B);
        final long updatedCreatedTime = mProviderHelper.getAll().get(0).created().getTime();
        assertEquals(createdTime, updatedCreatedTime);

    }

    public void testGetAll() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        final List<TrayItem> all = mProviderHelper.getAll();
        assertEquals(1, all.size());
        assertEquals(STRING_A, all.get(0).value());
        assertEquals(KEY_A, all.get(0).key());
    }

    public void testGetAllMultiple() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_B, STRING_B);
        final List<TrayItem> all = mProviderHelper.getAll();
        assertEquals(4, all.size());
    }

    public void testGetUriWithoutModule() throws Exception {

        try {
            mProviderHelper.getUri(null, "key");
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("module"));
        }
    }

    public void testPersist() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        assertDatabaseSize(1);
    }

    public void testPersistNull() throws Exception {
        //noinspection ConstantConditions
        mProviderHelper.persist(MODULE_A, KEY_A, null);
        assertDatabaseSize(1);
    }

    public void testPersistOverride() {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_B);
        assertDatabaseSize(1);
    }

    public void testPersistSameTwoModules() {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        assertDatabaseSize(2);
    }

    public void testPersistTwoKeys() {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_A);
        assertDatabaseSize(2);
    }

    public void testQueryAll() throws Exception {
        buildQueryDatabase();
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri());
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    public void testQueryFailed() throws Exception {
        buildQueryDatabase();

    }

    public void testQueryModule() throws Exception {
        buildQueryDatabase();
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertNotSame(list.get(0).value(), list.get(1).value());
    }

    public void testQueryProviderWithUnregisteredProvider() throws Exception {
        final Context context = mock(Context.class);
        final ContentResolver contentResolver = mock(ContentResolver.class);
        when(context.getContentResolver()).thenReturn(contentResolver);
        final TrayProviderHelper trayProviderHelper = new TrayProviderHelper(context);
        final Uri uri = trayProviderHelper.getUri();
        try {
            trayProviderHelper.queryProvider(uri);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains(uri.toString()));
        }
    }

    public void testQuerySingle() throws Exception {
        buildQueryDatabase();
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(STRING_A, list.get(0).value());
    }

    public void testReadParsedProperties() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list);
        assertEquals(1, list.size());
        TrayItem itemA = list.get(0);

        assertEquals(STRING_A, itemA.value());
        assertEquals(KEY_A, itemA.key());
        assertEquals(MODULE_A, itemA.module());
    }

    public void testSpecialChars() {
        final String key = "^&*ü";
        specialCharTest(MODULE_A, key);
        final String module = "!@#$ä";
        specialCharTest(module, KEY_A);
    }

    public void testSpecialChars2() {
        final String testString = "test/blubb/one";
        specialCharTest(MODULE_A, testString);
        specialCharTest(testString, KEY_A);
    }

    public void testSpecialChars3() {
        final String testString = "test'blubb";
        specialCharTest(MODULE_A, testString);
        specialCharTest(testString, KEY_A);
    }

    public void testUpdateChanges() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list);
        assertEquals(1, list.size());
        TrayItem itemA = list.get(0);
        assertNotNull(itemA.created());
        assertNotNull(itemA.updateTime());

        Thread.sleep(10);
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_B);
        final List<TrayItem> list2 = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list2);
        assertEquals(1, list2.size());
        TrayItem itemB = list2.get(0);
        //Log.v("", "diff: " + (itemA.updateTime().getTime() - itemB.updateTime().getTime()));
        assertNotSame(itemA.updateTime().getTime(), itemB.updateTime().getTime());
    }

    public void testUpdateEqualsCreatedAtFirst() throws Exception {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(MODULE_A, KEY_A));
        assertNotNull(list);
        assertEquals(1, list.size());
        TrayItem itemA = list.get(0);
        assertNotNull(itemA.created());
        assertNotNull(itemA.updateTime());
        assertEquals(itemA.updateTime(), itemA.created());
    }

    private void assertEqualsWithin(long expected, long value, long fudgeFactor) {
        long diff = Math.abs(expected - value);
        final String message = "expected: " + expected + " value: " + value
                + " diff (" + diff + ") is not in fudgeFactor: " + fudgeFactor;
        assertTrue(message, diff < fudgeFactor);
    }

    private void buildQueryDatabase() {
        mProviderHelper.persist(MODULE_A, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_A, KEY_B, STRING_B);
        mProviderHelper.persist(MODULE_B, KEY_A, STRING_A);
        mProviderHelper.persist(MODULE_B, KEY_B, STRING_B);
        assertDatabaseSize(4);
    }

    private void specialCharTest(final String module, final String key) {
        mProviderHelper.persist(module, key, STRING_A);
        assertDatabaseSize(1);

        final List<TrayItem> list = mProviderHelper
                .queryProvider(mProviderHelper.getUri(module));
        assertEquals(1, list.size());
        assertEquals(module, list.get(0).module());
        assertEquals(key, list.get(0).key());

        mProviderHelper.clear();
        assertDatabaseSize(0);
    }
}
