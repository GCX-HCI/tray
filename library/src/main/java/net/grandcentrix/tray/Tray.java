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

import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.provider.TrayProviderHelper;

import android.content.Context;

import java.util.List;

/**
 * Created by Jannis Veerkamp & Pascal Welsch on 17.09.14.
 * <p/>
 * This class works as root library class and first interaction point.
 * <p/>
 * Use this library like you would use the {@link android.content.SharedPreferences}, but this works
 * in a multiprocess environment. Saving information in a SyncAdapter#onPerformSync() cycle and
 * reading the information instant in the ui thread is possible.
 * <p/>
 * This library works with a {@link android.content.ContentProvider} to persist all data. This
 * requires you to add the provider to the manifest of your app.
 * <p/>
 * <pre>
 * {@code
 * <provider
 *      android:name="net.grandcentrix.tray.provider.TrayProvider"
 *      android:authorities="net.grandcentrix.tray.test"
 *      android:exported="false"
 *      android:multiprocess="false" />
 * }
 *
 * <ul>
 *     <li>authorities: add your own authority. The authority has to be unique. Include your app
 * package name</li>
 *     <li>exported: should be false. You don't want to give access to your data to other apps</li>
 *     <li>muliprocess: requires <b>false</b>. This makes the communication between processes
 * working. So it becomes a singleton and queues read and write requests</li>
 * </ul>
 * </pre>
 * <p/>
 * After adding the provider you have to add a property for the Authority of the internal
 * ContentProvider for Tray to your build.gradle.
 * <pre>
 * {@code
 * ext {
 *         trayAuthority = "net.grandcentrix.tray.test"
 *     }
 * }
 * </pre>
 * Not adding this property results in a build error: Error: Could not find property 'trayAuthority'
 * on project ':Tray:library'.
 * <p/>
 * This main class gives you access to global functions, deleting and retrieving all saved data.
 * These functions are only imporant in special cases. For the daily business using a {@link
 * net.grandcentrix.tray.accessor.PreferenceAccessor} is enough.
 * <p/>
 * The {@link android.content.SharedPreferences} uses files to group different preferences. This
 * library uses so called modules. It's common to create a new class extending {@link
 * TrayModulePreferences} for every new module. For simple Apps and the most common preferences is
 * the class {@link TrayAppPreferences} a good start which uses the app package name to group the
 * preferences.
 */
public class Tray {

    private final TrayProviderHelper mProviderHelper;

    public Tray(final Context context) {
        mProviderHelper = new TrayProviderHelper(context);
    }

    /**
     * clears <b>all</b> saved preferences. Module independent. <b>Erases everything</b>.
     */
    public void clear() {
        mProviderHelper.clear();
    }

    /**
     * clears stated modules.
     *
     * @param modules modules excluded when deleting preferences
     */
    public void clear(TrayPreference... modules) {
        mProviderHelper.clear(modules);
    }

    /**
     * clears <b>all</b> saved preferences, but the stated modules.
     *
     * @param modules modules excluded when deleting preferences
     */
    public void clearBut(TrayPreference... modules) {
        mProviderHelper.clearBut(modules);
    }

    /**
     * Builds a list of all preferences saved in all modules.
     *
     * @return all preferences as list.
     */
    public List<TrayItem> getAll() {
        return mProviderHelper.getAll();
    }
}
