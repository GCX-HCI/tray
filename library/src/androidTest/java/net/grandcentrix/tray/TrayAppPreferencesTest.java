package net.grandcentrix.tray;

import net.grandcentrix.tray.provider.TrayProviderTestCase;

public class TrayAppPreferencesTest extends TrayProviderTestCase {

    public void testInstantiation() throws Exception {
        new TrayAppPreferences(getProviderMockContext());
    }

    public void testOnUpgrade() throws Exception {
        final TrayAppPreferences appPreferences = new TrayAppPreferences(getProviderMockContext());
        try {
            appPreferences.onUpgrade(0, 1);
            fail();
        } catch (IllegalStateException e) {
            // not implemented yet
        }
    }
}