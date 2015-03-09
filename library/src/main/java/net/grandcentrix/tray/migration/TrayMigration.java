package net.grandcentrix.tray.migration;

import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 2/25/15.
 */
public interface TrayMigration {

    /**
     * gets the data from the old data store.
     * <p/>
     * Only primitive types are supported. See {@link net.grandcentrix.tray.accessor.Preference#isDataTypeSupported(Object)}
     * <p/>
     * called after {@link #shouldMigrate()} and before {@link #onPostMigrate()}
     *
     * @return the data in a valid primitive format
     */
    public Object getData();

    /**
     * @return the imported key name. When this name changes, the data gets imported again.
     * Otherwise the data is only imported once
     */
    @NonNull
    public String getPreviousKey();

    /**
     * @return the key where the data should be accessable in the future with Tray
     */
    @NonNull
    public String getTrayKey();

    /**
     * this is a good point to delete the old data to free space and prevent accidentally import
     * later which could override newer data saved into Tray after the last import
     *
     * @param successful true if the data was imported, false otherwise
     */
    public void onPostMigrate(final boolean successful);

    /**
     * called before {@link #getData()}. This is a good point to check if the data which should be
     * migrated is available. If not, return true if you want to cancel the import.
     *
     * @return true if the import should be canceled
     */
    public boolean shouldMigrate();
}
