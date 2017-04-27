package net.grandcentrix.tray.provider;

import org.mockito.Mockito;

import android.content.Context;
import android.net.Uri;
import android.test.AndroidTestCase;

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
        uri = TrayContract.generateContentUri(Mockito.mock(Context.class));
        assertEquals("content://asdf/preferences", uri.toString());
    }

    public void testGenerateInternalContentUri() throws Exception {
        Uri uri = TrayContract.generateInternalContentUri(getContext());
        assertEquals("content://net.grandcentrix.tray.preferences.test/internal_preferences",
                uri.toString());

        TrayContract.setAuthority("blubb");
        uri = TrayContract.generateInternalContentUri(Mockito.mock(Context.class));
        assertEquals("content://blubb/internal_preferences", uri.toString());
    }

    public void testGenerateInternalContentUri_WithoutProviderAuthority_AppShouldCrash()
            throws Exception {
        TrayContentProvider.mAuthority = null;

        try {
            TrayContract.generateInternalContentUri(Mockito.mock(Context.class));
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Internal tray error"));
        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TrayContract.setAuthority(null);
    }


}