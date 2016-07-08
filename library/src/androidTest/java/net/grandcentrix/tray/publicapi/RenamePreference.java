package net.grandcentrix.tray.publicapi;

import net.grandcentrix.tray.core.TrayStorage;
import net.grandcentrix.tray.mock.TestTrayModulePreferences;
import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 6/3/15.
 */
public class RenamePreference extends TrayProviderTestCase {

    public void testRename_Device() throws Exception {
        rename(TrayStorage.Type.DEVICE);
    }

    public void testRename_User() throws Exception {
        rename(TrayStorage.Type.USER);
    }

    private void rename(final TrayStorage.Type type) throws Exception {
        //create old Preference
        final TestTrayModulePreferences oldOne =
                new TestTrayModulePreferences(getProviderMockContext(), "oldOne", type);
        oldOne.put("key", "value");
        assertEquals(1, oldOne.getAll().size());
        assertEquals(1, oldOne.getInternalStorage().getVersion());

        final TestTrayModulePreferences newOne =
                new TestTrayModulePreferences(getProviderMockContext(), "newOne") {
                    @Override
                    protected void onCreate(final int newVersion) {
                        super.onCreate(newVersion);
                        annexModule("oldOne");
                    }
                };
        assertEquals(1, newOne.getAll().size());
        assertEquals(0, oldOne.getAll().size());

        // TestTrayModulePreferences default version is 1. 0 means all metadata is deleted
        assertEquals(0, oldOne.getInternalStorage().getVersion());
    }
}
