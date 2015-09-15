package net.grandcentrix.tray.core;

import net.grandcentrix.tray.TrayPreferences;

/**
 * The type of the data indicating their backup strategy to the cloud
 */
public enum TrayStorageType {
    /**
     * don't use {@link #UNDEFINED} when creating a {@link TrayPreferences}.
     * It's used internally to import a preference by moduleName without knowing the location
     * of this preference (user or device). Because of that a undefined TrayStorage lookups the
     * data in both data stores.
     * <p>
     * Because it's not clear where to save data a undefined TrayStorage is only able to read
     * and delete items. Writing with <code>put()</code> is <b>not</b> allowed.
     */
    UNDEFINED,
    /**
     * the data relates to the user and can be saved in the cloud and restored on another
     * device
     */
    USER,
    /**
     * the data is device specific like a GCM push token or settings that is important for this
     * specific device.
     * <p>
     * Such data shouldn't saved to the cloud with auto backup since Android Marshmallow.
     */
    DEVICE
}
