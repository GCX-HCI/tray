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

import android.content.Context;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * Default implementation of the {@link net.grandcentrix.tray.accessor.TrayPreference} which uses
 * the app package name as module name.
 * <p>
 * Use this {@link net.grandcentrix.tray.accessor.PreferenceAccessor} to save your preferences of
 * your app independent of a single app module. It's not a good practice to put all preferences in
 * a
 * single module. Extend the {@link TrayModulePreferences} and gain the functionality to remove all
 * data of a single module. This could help keeping the saved data as small as possible across app
 * upgrades.
 */
public class TrayAppPreferences extends TrayModulePreferences {

    private static final int VERSION = 1;

    public TrayAppPreferences(final Context context) {
        super(context, context.getPackageName(), VERSION);
    }

    @Override
    protected void onCreate(final int newVersion) {

    }

    @Override
    protected void onUpgrade(final int oldVersion, final int newVersion) {
        throw new IllegalStateException("Can't upgrade database from version " +
                oldVersion + " to " + newVersion + ", not implemented.");
    }
}
