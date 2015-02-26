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
public class TrayContract {

    public static interface Preferences {

        public static interface Columns extends BaseColumns {

            public static final String ID = BaseColumns._ID;

            public static final String KEY = TrayDBHelper.KEY;

            public static final String VALUE = TrayDBHelper.VALUE;

            public static final String MODULE = TrayDBHelper.MODULE;

            public static final String CREATED = TrayDBHelper.CREATED; // DATE

            public static final String UPDATED = TrayDBHelper.UPDATED; // DATE

            public static final String IMPORTED_KEY = TrayDBHelper.IMPORTED_KEY;
        }

        public static final String BASE_PATH = "preferences";
    }

    public static interface InternalPreferences extends Preferences {

        public static final String BASE_PATH = "internal_preferences";
    }

}
