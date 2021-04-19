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

import net.grandcentrix.tray.BuildConfig;
import net.grandcentrix.tray.core.TrayStorage;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.test.IsolatedContext;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.test.mock.MockPackageManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public abstract class TrayProviderTestCase extends ProviderTestCase2<TrayContentProvider> {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static class TrayIsolatedContext extends IsolatedContext {

        boolean mThrowNameNotFoundFromPm = false;
        
        boolean mHasMockResolver = false;

        private List<ProviderInfo> mProviderInfos;

        private HashMap<String, ContentProvider> mProviders = new HashMap<>();

        private Resources mResources;

        private final Context mTargetContext;

        IsolatedContext innerContext = new IsolatedContext(getContentResolver(), this);

        public TrayIsolatedContext(final ContentResolver resolver, final Context targetContext) {
            super(resolver, targetContext);

            mTargetContext = targetContext;
        }

        public void addProvider(String name, ContentProvider provider) {
            mProviders.put(name, provider);
        }

        public void enableMockResolver(final boolean enabled) {
            mHasMockResolver = enabled;
        }

        public void setThrowNameNotFoundFromPackageManager(final boolean shouldThrow){
            mThrowNameNotFoundFromPm = shouldThrow;
        }
        
        @Override
        public Context getApplicationContext() {
            return innerContext;
        }

        @Override
        public ContentResolver getContentResolver() {
            if (isHasMockResolver()) {
                return getMockResolver();
            } else {
                return super.getContentResolver();
            }
        }

        public ContentResolver getMockResolver() {
            final MockContentResolver mockContentResolver = new MockContentResolver(mTargetContext);
            for (String authority : mProviders.keySet()) {
                mockContentResolver.addProvider(authority, mProviders.get(authority));
            }
            return mockContentResolver;
        }

        @Override
        public String getPackageName() {
            return "package.test";
        }

        @Override
        public SharedPreferences getSharedPreferences(final String name, final int mode) {
            return super.getSharedPreferences(name, mode);
        }

        public boolean isHasMockResolver() {
            return mHasMockResolver;
        }

        public void setProviderInfos(List<ProviderInfo> providerInfos) {
            mProviderInfos = providerInfos;
        }

        @Override
        public PackageManager getPackageManager() {
            return new MockPackageManager() {
                @Override
                public List<ProviderInfo> queryContentProviders(final String processName,
                        final int uid, final int flags) {
                    return mProviderInfos;
                }
    
                @Override
                public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
                    if(mThrowNameNotFoundFromPm) throw new NameNotFoundException();
                    else {
                        final PackageInfo pkgInfo = new PackageInfo();
                        if (null != mProviderInfos) {
                            pkgInfo.providers = mProviderInfos.toArray(new ProviderInfo[mProviderInfos.size()]);
                        }
                        return pkgInfo;
                    }
                }
            };
        }

        @Override
        public Resources getResources() {
            return mResources != null ? mResources : super.getResources();
        }

        public void setResources(final Resources resources) {
            mResources = resources;
        }
    }

    private TrayIsolatedContext mIsolatedContext;

    public TrayProviderTestCase() {
        super(TrayContentProvider.class, MockProvider.AUTHORITY);
    }

    public TrayIsolatedContext getProviderMockContext() {
        return mIsolatedContext;
    }

    public ProviderInfo getMockProviderInfo() {
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = getProviderMockContext().getPackageName() + ".tray";
        return providerInfo;
    }

    protected void assertDatabaseSize(final TrayStorage.Type type, final long expectedSize) {
        switch (type) {
            default:
            case UNDEFINED:
            case USER:
                assertUserDatabaseSize(expectedSize);
                break;
            case DEVICE:
                assertDeviceDatabaseSize(expectedSize);
                break;
        }
    }

    /**
     * checks the database size by querying the given {@param contentUri}
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

    /**
     * checks the database size by querying the given {@param contentUri}
     *
     * @param expectedSize the number of items you expect
     */
    protected void assertDeviceDatabaseSize(final long expectedSize) {
        assertDatabaseSize(MockProvider.getDeviceContentUri(), expectedSize, true);
    }

    /**
     * checks the database size by querying the given {@param contentUri}
     *
     * @param expectedSize the number of items you expect
     */
    protected void assertUserDatabaseSize(final long expectedSize) {
        assertDatabaseSize(MockProvider.getUserContentUri(), expectedSize, true);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("dexmaker.dexcache",
                "/data/data/" + BuildConfig.APPLICATION_ID + ".test/cache");
        cleanupProvider();

        mIsolatedContext = new TrayIsolatedContext(getMockContext().getContentResolver(),
                getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanupProvider();
    }

    private void cleanupProvider() {
        TrayContract.sAuthority = MockProvider.AUTHORITY;
        TrayContentProvider.setAuthority(MockProvider.AUTHORITY);
        try {
            getMockContentResolver().delete(MockProvider.getUserContentUri(), null, null);
            getMockContentResolver().delete(MockProvider.getDeviceContentUri(), null, null);
            getMockContentResolver().delete(MockProvider.getInternalUserContentUri(), null, null);
            getMockContentResolver().delete(MockProvider.getInternalDeviceContentUri(), null, null);

            assertUserDatabaseSize(0);
            assertDeviceDatabaseSize(0);
            assertDatabaseSize(MockProvider.getInternalUserContentUri(), 0, true);
            assertDatabaseSize(MockProvider.getInternalDeviceContentUri(), 0, true);
        } catch (SQLiteException e) {
            // the table is unknown. no problem
        }
    }
}
