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

package net.grandcentrix.tray.storage;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * storage is now separated in modules and easier to maintain. Could be done with different files,
 * databases...
 */
public abstract class ModularizedStorage<T> implements PreferenceStorage<T> {

    private String mModuleName;

    public ModularizedStorage(final String moduleName) {
        mModuleName = moduleName;
    }

    public String getModuleName() {
        return mModuleName;
    }
}
