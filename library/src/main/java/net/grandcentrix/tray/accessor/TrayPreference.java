/*
 * Copyright (C) 2015 grandcentrix GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.tray.accessor;

import net.grandcentrix.tray.importer.TrayImport;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.storage.ModularizedStorage;

import java.util.List;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public abstract class TrayPreference extends Preference<TrayItem> {

    public TrayPreference(final ModularizedStorage<TrayItem> storage, final int version) {
        super(storage, version);
    }

    /**
     * migrates the data from {@link #getImports(int)} into tray
     */
    @Override
    protected void onUpgrade(final int version) {
        final List<TrayImport> imports = getImports(version);
        if (imports == null) {
            return;
        }
        for (TrayImport singleImport : imports) {
            if (isAlreadyImported(singleImport)) {
                continue;
            }
            final boolean cancel = singleImport.onPreImport();
            if (cancel) {
                continue;
            }
            final Object data = singleImport.getImportData();
            isDataTypeSupported(data);
            getStorage().put(singleImport.getTrayKey(), data);
            singleImport.onPostImport();
        }
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        final TrayItem pref = getPref(key);
        return pref == null ? defaultValue : Boolean.parseBoolean(pref.value());
    }

    @Override
    public float getFloat(final String key, final float defaultValue) {
        final TrayItem pref = getPref(key);
        return pref == null ? defaultValue : Float.parseFloat(pref.value());
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        final TrayItem pref = getPref(key);
        return pref == null ? defaultValue : Integer.parseInt(pref.value());
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        final TrayItem pref = getPref(key);
        return pref == null ? defaultValue : Long.parseLong(pref.value());
    }

    public ModularizedStorage<TrayItem> getModularizedStorage() {
        return (ModularizedStorage<TrayItem>) super.getStorage();
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        final TrayItem pref = getPref(key);
        return pref == null ? defaultValue : pref.value();
    }


    /**
     * checks if the given {@param importItem} was imported before
     *
     * @param importItem the import operation object
     * @return true if the item should be reimported
     */
    private boolean isAlreadyImported(final TrayImport importItem) {
        // annotations are good but it's important to be sure
        // noinspection ConstantConditions
        if (importItem.getImportedKey() == null) {
            throw new IllegalArgumentException("the imported key must not be null");
        }

        final String trayKey = importItem.getTrayKey();
        final TrayItem preference = getStorage().get(trayKey);
        if (preference.importedKey() == null) {
            // the tray item was available before the import because it has no importedKey
            return false;
        }

        final String importedKey = importItem.getImportedKey();
        // for better documentation
        // noinspection RedundantIfStatement
        if (preference.importedKey().equals(importedKey)) {
            // the keys are the same. so the item was imported before
            return true;
        } else {
            // the key has changed since the last import. import again
            return false;
        }
    }

}
