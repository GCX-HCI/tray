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

import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.storage.ModularizedStorage;

/**
 * Created by pascalwelsch on 11/20/14.
 */
public abstract class TrayPreference extends Preference<TrayItem> {

    public TrayPreference(final ModularizedStorage<TrayItem> storage) {
        super(storage);
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
}
