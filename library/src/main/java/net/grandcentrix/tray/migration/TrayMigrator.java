package net.grandcentrix.tray.migration;

import net.grandcentrix.tray.accessor.Preference;
import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.provider.TrayItem;

import java.util.List;

/**
 * Created by pascalwelsch on 2/26/15.
 */
public class TrayMigrator {

    private TrayPreference mTrayPreference;

    public TrayMigrator(final TrayPreference trayPreference) {
        mTrayPreference = trayPreference;
    }

    public void performMigration(List<TrayMigration> migrations) {
        if (migrations == null) {
            return;
        }
        for (TrayMigration migration : migrations) {
            if (isAlreadyImported(migration)) {
                continue;
            }
            final boolean cancel = migration.onPreMigrate();
            if (cancel) {
                continue;
            }
            final Object data = migration.getData();
            if (Preference.isDataTypeSupported(data)) {
                mTrayPreference.getStorage().put(migration.getTrayKey(), data);
                migration.onPostMigrate();
            }
        }
    }

    /**
     * checks if the given {@param migration} was imported before
     *
     * @param migration the import operation object
     * @return true if the item should be remigrated
     */
    private boolean isAlreadyImported(final TrayMigration migration) {
        // annotations are good but it's important to be sure
        // noinspection ConstantConditions
        if (migration.getPreviousKey() == null) {
            throw new IllegalArgumentException("the previousKey must not be null");
        }

        final String trayKey = migration.getTrayKey();
        final TrayItem item = mTrayPreference.getStorage().get(trayKey);
        if (item.migratedKey() == null) {
            // the tray item was available before the migration because it has no migrationKey
            return false;
        }

        final String migratedKey = migration.getPreviousKey();
        // for better documentation
        // noinspection RedundantIfStatement
        if (item.migratedKey().equals(migratedKey)) {
            // the keys are the same. so the item was imported before
            return true;
        } else {
            // the key has changed since the last import. import again
            return false;
        }
    }
}
