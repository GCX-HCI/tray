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
 * Use {@link AppPreferences} instead. This was only a naming thing.
 * <p>
 * Will be removed with version 1.0
 */
@Deprecated
public class TrayAppPreferences extends TrayPreferences {

    private static final int VERSION = 1;

    public TrayAppPreferences(final Context context) {
        super(context, context.getPackageName(), VERSION);
    }
}
