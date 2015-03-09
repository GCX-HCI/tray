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

package net.grandcentrix.tray.provider;

import android.provider.BaseColumns;

/**
 * Created by jannisveerkamp on 17.09.14.
 */
public interface TrayContract {

    interface Preferences {

        interface Columns extends BaseColumns {

            final String ID = BaseColumns._ID;

            final String KEY = TrayDBHelper.KEY;

            final String VALUE = TrayDBHelper.VALUE;

            final String MODULE = TrayDBHelper.MODULE;

            final String CREATED = TrayDBHelper.CREATED; // DATE

            final String UPDATED = TrayDBHelper.UPDATED; // DATE

            final String MIGRATED_KEY = TrayDBHelper.MIGRATED_KEY;
        }

        final String BASE_PATH = "preferences";
    }

    interface InternalPreferences extends Preferences {

        final String BASE_PATH = "internal_preferences";
    }

}
