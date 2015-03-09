package net.grandcentrix.tray.accessor;

import net.grandcentrix.tray.mock.MockModularizedStorage;
import net.grandcentrix.tray.provider.TrayItem;

/**
 * Created by pascalwelsch on 3/9/15.
 */
public class MockSimplePreference extends Preference<TrayItem> {

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

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        final TrayItem trayItem = getStorage().get(key);
        return Boolean.valueOf(trayItem.value());
    }

    @Override
    public float getFloat(final String key, final float defaultValue) {
        final TrayItem trayItem = getStorage().get(key);
        return Float.valueOf(trayItem.value());
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        final TrayItem trayItem = getStorage().get(key);
        return Integer.valueOf(trayItem.value());
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        final TrayItem trayItem = getStorage().get(key);
        return Long.valueOf(trayItem.value());
    }

    public MockModularizedStorage getModularizedStorage() {
        return (MockModularizedStorage) getStorage();
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        final TrayItem trayItem = getStorage().get(key);
        return trayItem.value();
    }
}
