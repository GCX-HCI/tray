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

import net.grandcentrix.tray.core.PreferenceAccessor;

import android.content.Context;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * Default implementation of the {@link TrayPreferences} which uses
 * the app package name as module name.
 * <p>
 * Use this {@link PreferenceAccessor} to save your preferences of
 * your app independent of a single app module. It's not a good practice to put all preferences in
 * a
 * single module. Extend the {@link TrayPreferences} and gain the functionality to remove all
 * data of a single module. This could help keeping the saved data as small as possible across app
 * upgrades.
 */
public class AppPreferences extends TrayPreferences {

    private static final int VERSION = 1;

    public AppPreferences(final Context context) {
        super(context, context.getPackageName(), VERSION);
    }
}
