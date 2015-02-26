package net.grandcentrix.tray.importer;

import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 2/25/15.
 */
public interface TrayImport {

    /**
     * gets the data from the old data store.
     * <p/>
     * Only primitive types are supported. See {@link net.grandcentrix.tray.accessor.Preference#isDataTypeSupported(Object)}
     * <p/>
     * called after {@link #onPreImport()} and before {@link #onPostImport()}
     *
     * @return the data in a valid primitive format
     */
    public Object getImportData();

    /**
     * @return the imported key name. When this name changes, the data gets imported again.
     * Otherwise the data is only imported once
     */
    @NonNull
    public String getImportedKey();

    /**
     * @return the key where the data should be accessable in the future with Tray
     */
    @NonNull
    public String getTrayKey();

    /**
     * this is a good point to delete the old data to free space and prevent accidentally import
     * later which could override newer data saved into Tray after the last import
     */
    public void onPostImport();

    /**
     * called before {@link #getImportData()}. This is a good point to check if the data which
     * should be migrated is available. If not, return true if you want to cancel the import.
     *
     * @return true if the import should be canceled
     */
    public boolean onPreImport();
}
