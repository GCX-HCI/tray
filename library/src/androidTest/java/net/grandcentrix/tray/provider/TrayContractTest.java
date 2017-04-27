package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.R;

import android.content.res.Resources;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by pascalwelsch on 4/12/15.
 */
public class TrayContractTest extends AndroidTestCase {

    public void testConstruction() throws Exception {
        new TrayContract();
    }

    public void testGenerateContentUri() throws Exception {
        Uri uri = TrayContract.generateContentUri(getContext());
        assertEquals("content://net.grandcentrix.tray.preferences.test/preferences",
                uri.toString());

        TrayContract.setAuthority("asdf");
        uri = TrayContract.generateContentUri(getContext());
        assertEquals("content://asdf/preferences", uri.toString());
    }

    public void testGenerateInternalContentUri() throws Exception {
        Uri uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://net.grandcentrix.tray.preferences.test/internal_preferences",
                uri.toString());

        TrayContract.setAuthority("blubb");
        uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://blubb/internal_preferences", uri.toString());
    }

    public void testGenerateInternalContentUri_WithoutProviderAuthority_AppShouldCrash()
            throws Exception {
        final String authority = TrayContentProvider.mAuthority;
        TrayContentProvider.mAuthority = null;

        try {
            TrayContract.generateInternalContentUri(getContext());
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Internal tray error"));
        }

        TrayContentProvider.mAuthority = authority;
    }

    public void testLogcatOutput_ShouldPrintIfTrayAuthorityIsNotDefault() throws Exception {
        final Resources mockResources = mock(Resources.class);
        when(mockResources.getString(R.string.tray__authority))
                .thenReturn("notDefaultTrayAuthority");
        final MockContext mockContext = new MockContext() {
            @Override
            public Resources getResources() {
                return mockResources;
            }
        };
        TrayContract.generateInternalContentUri(mockContext);

        verify(mockResources).getString(R.string.tray__authority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TrayContract.setAuthority(null);
    }


}