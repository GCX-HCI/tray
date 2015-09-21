package net.grandcentrix.tray.core;

import java.util.Collection;

/**
 * Interface definition for a callback to be invoked when a preference is changed.
 * <p>
 * Created by pascalwelsch on 5/17/15.
 */
public interface OnTrayPreferenceChangeListener {

    /**
     * Called when a tray preference is changed, added, or removed. This may be called even if a
     * preference is set to its existing value.
     * <p>
     * This callback will be run the Looper you registered it i.e. the main Looper
     *
     * @param items The {@link TrayItem}s that received the change.
     */
    void onTrayPreferenceChanged(Collection<TrayItem> items);
}
