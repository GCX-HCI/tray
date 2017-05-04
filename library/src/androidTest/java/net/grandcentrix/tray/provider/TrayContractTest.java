package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.R;
import net.grandcentrix.tray.core.TrayRuntimeException;

import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by pascalwelsch on 4/12/15.
 */
public class TrayContractTest extends TrayProviderTestCase {


    public void testConstruction() throws Exception {
        new TrayContract();
    }

    public void testGenerateContentUri() throws Exception {
        Uri uri = TrayContract.generateContentUri(getContext());
        assertEquals("content://net.grandcentrix.tray.preferences.test/preferences",
                uri.toString());

        TrayContract.sAuthority = "asdf";
        uri = TrayContract.generateContentUri(getContext());
        assertEquals("content://asdf/preferences", uri.toString());
    }

    public void testGenerateInternalContentUri() throws Exception {
        Uri uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://net.grandcentrix.tray.preferences.test/internal_preferences",
                uri.toString());

        TrayContract.sAuthority = "blubb";
        uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://blubb/internal_preferences", uri.toString());
    }

    public void testGenerateInternalContentUri_WithCorrectProvider_ShouldWork()
            throws Exception {

        final List<ProviderInfo> mockProviders = new ArrayList<>();

        ProviderInfo wrongInfo = new ProviderInfo();
        wrongInfo.authority = "wrong";
        wrongInfo.name = "wrong";
        mockProviders.add(wrongInfo);

        ProviderInfo info = new ProviderInfo();
        info.authority = "my.custom.authority";
        info.name = TrayContentProvider.class.getName();
        mockProviders.add(info);

        getProviderMockContext().setProviderInfos(mockProviders);

        TrayContract.generateInternalContentUri(getProviderMockContext());

        assertEquals("my.custom.authority", TrayContract.sAuthority);
    }

    public void testGenerateInternalContentUri_WithWrongProviders_AppShouldCrash()
            throws Exception {

        final List<ProviderInfo> mockProviders = new ArrayList<>();
        ProviderInfo info = new ProviderInfo();
        info.name = "wrongName";
        mockProviders.add(info);

        getProviderMockContext().setProviderInfos(mockProviders);

        try {
            TrayContract.generateInternalContentUri(getProviderMockContext());
            fail("did not throw");
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("Internal tray error"));
        }
    }

    public void testGenerateInternalContentUri_WithoutEmptyProviders_AppShouldCrash()
            throws Exception {

        final List<ProviderInfo> mockProviders = new ArrayList<>();
        getProviderMockContext().setProviderInfos(mockProviders);

        try {
            TrayContract.generateInternalContentUri(getProviderMockContext());
            fail("did not throw");
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("Internal tray error"));
        }
    }

    public void testGenerateInternalContentUri_WithoutProvider_AppShouldCrash()
            throws Exception {

        try {
            TrayContract.generateInternalContentUri(getProviderMockContext());
            fail("did not throw");
        } catch (TrayRuntimeException e) {
            assertTrue(e.getMessage().contains("Internal tray error"));
        }
    }

    public void testLogcatOutput_ShouldPrintIfTrayAuthorityIsNotDefault() throws Exception {

        final List<ProviderInfo> mockProviders = new ArrayList<>();
        ProviderInfo info = new ProviderInfo();
        info.authority = "my.custom.authority";
        info.name = TrayContentProvider.class.getName();
        mockProviders.add(info);

        getProviderMockContext().setProviderInfos(mockProviders);

        final Resources mockResources = mock(Resources.class);
        when(mockResources.getString(R.string.tray__authority))
                .thenReturn("NOTlegacyTrayAuthority");
        getProviderMockContext().setResources(mockResources);

        TrayContract.generateInternalContentUri(getProviderMockContext());

        // no further assertions possible just executing the logging branch

        verify(mockResources).getString(R.string.tray__authority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TrayContract.sAuthority = null;
    }
}