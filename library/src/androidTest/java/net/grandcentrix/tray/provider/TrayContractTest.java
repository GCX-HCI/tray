package net.grandcentrix.tray.provider;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

/**
 * Created by pascalwelsch on 4/12/15.
 */
public class TrayContractTest extends AndroidTestCase {

    public void testConstruction() throws Exception {
        new TrayContract();
    }

    public void testGenerateContentUri() throws Exception {
        Uri uri = TrayContract.generateContentUri(getContext());
        assertEquals("content://com.example.preferences/preferences", uri.toString());

        TrayContract.setAuthority("asdf");
        uri = TrayContract.generateContentUri(Mockito.mock(Context.class));
        assertEquals("content://asdf/preferences", uri.toString());
    }

    public void testGenerateInternalContentUri() throws Exception {
        Uri uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://com.example.preferences/internal_preferences", uri.toString());

        TrayContract.setAuthority("blubb");
        uri = TrayContract.generateInternalContentUri(Mockito.mock(Context.class));
        assertEquals("content://blubb/internal_preferences", uri.toString());
    }

    public void testGenerateContentUri2() throws Exception {
        MockContext mockContext = new MockContext() {
            @Override
            public Context getApplicationContext() {
                return this;
            }

            @Override
            public String getPackageName() {
                return getContext().getPackageName();
            }

            @Override
            public PackageManager getPackageManager() {
                return new MockPackageManager() {
                    @Override
                    public List<PackageInfo> getInstalledPackages(int flags) {
                        if (flags == PackageManager.GET_PROVIDERS) {
                            PackageInfo packageInfo = new PackageInfo();
                            ProviderInfo providerInfo = new ProviderInfo();
                            providerInfo.authority = getContext().getPackageName() + ".tray";
                            packageInfo.providers = new ProviderInfo[]{providerInfo};
                            return Collections.singletonList(packageInfo);
                        }
                        return super.getInstalledPackages(flags);
                    }
                };
            }
        };
        Uri uri = TrayContract.generateContentUri(mockContext);
        assertEquals("content://" + getContext().getPackageName() + ".tray/preferences", uri.toString());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TrayContract.setAuthority(null);
    }


}