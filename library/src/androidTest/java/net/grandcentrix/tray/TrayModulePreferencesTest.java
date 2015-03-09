package net.grandcentrix.tray;

import net.grandcentrix.tray.provider.TrayProviderTestCase;

public class TrayModulePreferencesTest extends TrayProviderTestCase {

    public void testGetContext() throws Exception {
        final TrayModulePreferences modulePreferences = new TrayModulePreferences(
                getProviderMockContext(), "test", 1) {

            @Override
            protected void onCreate(final int newVersion) {

            }

            @Override
            protected void onUpgrade(final int oldVersion, final int newVersion) {

            }
        };

        assertEquals(getProviderMockContext().getApplicationContext(),
                modulePreferences.getContext());
    }
}