package net.grandcentrix.tray.accessor;

import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.mock.MockModularizedStorage;

/**
 * Created by pascalwelsch on 3/9/15.
 */
public class MockSimplePreference extends TrayPreference {

    public MockSimplePreference(final int version) {
        super(new MockModularizedStorage("test"), version);
    }

    public MockSimplePreference(final MockModularizedStorage storage, final int version) {
        super(storage, version);
    }

    @Override
    protected void onCreate(final int newVersion) {

    }

    @Override
    protected void onUpgrade(final int oldVersion, final int newVersion) {

    }

    public MockModularizedStorage getModularizedStorage() {
        return (MockModularizedStorage) getStorage();
    }
}
