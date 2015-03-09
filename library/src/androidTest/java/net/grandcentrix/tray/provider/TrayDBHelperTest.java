package net.grandcentrix.tray.provider;

public class TrayDBHelperTest extends TrayProviderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testInstantiation() throws Exception {
        new TrayDBHelper(getProviderMockContext());
    }

    public void testOnUpgrade() throws Exception {
        final TrayDBHelper trayDBHelper = new TrayDBHelper(getProviderMockContext());
        try {
            trayDBHelper.onUpgrade(trayDBHelper.getWritableDatabase(), 0, 1);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("version"));
        }
    }
}