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

package net.grandcentrix.tray;

import net.grandcentrix.tray.core.AbstractTrayPreference;
import net.grandcentrix.tray.core.Preferences;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderHelper;

import android.content.Context;

import java.util.List;

/**
 * Created by Jannis Veerkamp &amp; Pascal Welsch on 17.09.14.
 * <p>
 * This class works as root library class and first interaction point.
 * <p>
 * Use this library like you would use the {@link android.content.SharedPreferences}, but this
 * works in a multiprocess environment. Saving information in a SyncAdapter#onPerformSync() cycle
 * and reading the information instant in the ui thread is possible.
 * <p>
 * This library works with a {@link android.content.ContentProvider} to persist all data. This
 * requires you to add the provider to the manifest of your app.
 * <p>
 * The {@link android.content.SharedPreferences} uses files to group different preferences. This
 * library uses so called modules. It's common to create a new class extending {@link
 * TrayPreferences} for every new module. For simple Apps and the most common preferences is
 * the class {@link AppPreferences} a good start which uses the app package name to group the
 * preferences.
 */
public class Tray {

    private final TrayProviderHelper mProviderHelper;

    /**
     * clears stated modules.
     *
     * @param modules modules excluded when deleting preferences
     */
    public static void clear(TrayPreferences... modules) {
        for (Preferences module : modules) {
            if (module == null) {
                continue;
            }
            module.clear();
        }
    }

    public Tray(final Context context) {
        mProviderHelper = new TrayProviderHelper(context);
    }

    /**
     * clears <b>all</b> saved preferences. Module independent. <b>Erases everything</b>.
     *
     * @return true when successfully cleared all modules
     */
    public boolean clear() {
        return mProviderHelper.clear();
    }

    /**
     * clears <b>all</b> saved preferences, but the stated modules.
     *
     * @param modules modules excluded when deleting preferences
     * @return true when successfully cleared the not stated modules
     */
    public boolean clearBut(AbstractTrayPreference... modules) {
        return mProviderHelper.clearBut(modules);
    }

    /**
     * Builds a list of all preferences saved in all modules.
     *
     * @return all preferences as list.
     */
    public List<TrayItem> getAll() {
        return mProviderHelper.getAll();
    }

    /**
     * clears <b>all</b> saved preferences. Module independent. <b>Erases everything</b>.
     *
     * @return true when successfully wiped everything
     */
    public boolean wipe() {
        return mProviderHelper.wipe();
    }
}
