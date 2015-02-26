package net.grandcentrix.tray;

public class TrayModulePreferencesTest extends TrayTest {

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