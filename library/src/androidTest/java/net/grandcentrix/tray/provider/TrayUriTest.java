package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.core.TrayStorage;

/**
 * Created by pascalwelsch on 8/23/15.
 */
public class TrayUriTest extends TrayProviderTestCase {

    private static String sAuthority = MockProvider.AUTHORITY;

    public void testBuilderEqualsGet() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals(trayUri.get(), trayUri.builder().build());
        assertEquals(trayUri.getInternal(), trayUri.builder().setInternal(true).build());
    }

    public void testGet() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences",
                trayUri.get().toString());
    }

    public void testGetInternal() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/internal_preferences",
                trayUri.getInternal().toString());
    }

    public void testKeyOnly() throws Exception {
        // this doesn't make sense for the current implementation but tray could be used
        //  without modules. So I'll leave it here
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myKey",
                trayUri.builder()
                        .setKey("myKey")
                        .build().toString());

    }

    public void testModule() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myModule",
                trayUri.builder()
                        .setModule("myModule")
                        .build().toString());
    }

    public void testModuleKey() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myModule/myKey",
                trayUri.builder()
                        .setModule("myModule")
                        .setKey("myKey")
                        .build().toString());
    }

    public void testModuleKeyBackupFalse() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myModule/myKey?backup=false",
                trayUri.builder()
                        .setModule("myModule")
                        .setKey("myKey")
                        .setType(TrayStorage.Type.DEVICE)
                        .build().toString());
    }

    public void testModuleKeyBackupNotSetForUndefined() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myModule/myKey",
                trayUri.builder()
                        .setModule("myModule")
                        .setKey("myKey")
                        .setType(TrayStorage.Type.UNDEFINED)
                        .build().toString());
    }

    public void testModuleKeyBackupTrue() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/preferences/myModule/myKey?backup=true",
                trayUri.builder()
                        .setModule("myModule")
                        .setKey("myKey")
                        .setType(TrayStorage.Type.USER)
                        .build().toString());
    }

    public void testModuleKeyInternal() throws Exception {
        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        assertEquals("content://" + sAuthority + "/internal_preferences/myModule/myKey",
                trayUri.builder()
                        .setModule("myModule")
                        .setKey("myKey")
                        .setInternal(true)
                        .build().toString());
    }
}