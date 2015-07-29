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

import net.grandcentrix.tray.mock.MockModularizedStorage;

import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 3/9/15.
 */
public class MockSimplePreferences extends ModularizedTrayPreferences<MockModularizedStorage> {

    public MockSimplePreferences(final int version) {
        super(new MockModularizedStorage("test"), version);
    }

    public MockSimplePreferences(
            @NonNull final MockModularizedStorage storage,
            final int version) {
        super(storage, version);
    }
}