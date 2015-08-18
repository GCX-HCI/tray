package net.grandcentrix.tray;

import net.grandcentrix.tray.provider.TrayProviderTestCase;

/**
 * Created by pascalwelsch on 6/5/15.
 */
public class TrayPreferencesTest extends TrayProviderTestCase {

    public void testAnnexModule() throws Exception {
        final TrayPreferences prefs = new TrayPreferences(
                getProviderMockContext(), "test", 1) {
        };

        final TrayPreferences others = new TrayPreferences(
                getProviderMockContext(), "test2", 1) {
        };
        others.put("key", "value");
        assertEquals(1, others.getAll().size());

        prefs.annexModule("test2");
        assertEquals(1, prefs.getAll().size());
        assertEquals(0, others.getAll().size());
    }

    public void testGetContext() throws Exception {
        final TrayPreferences prefs = new TrayPreferences(
                getProviderMockContext(), "test", 1) {
        };

        assertEquals(getProviderMockContext().getApplicationContext(),
                prefs.getContext());
    }

    public void testInstantiation() throws Exception {
        new TrayPreferences(getProviderMockContext(), "test", 1) {

        };
    }


    public void testLegacyInstantiation() throws Exception {
        new TrayModulePreferences(getProviderMockContext(), "test", 1);
    }
}
