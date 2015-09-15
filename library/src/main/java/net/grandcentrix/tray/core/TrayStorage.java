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

package net.grandcentrix.tray.core;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * storage is now separated in modules and easier to maintain. Could be done with different files,
 * databases...
 */
public abstract class TrayStorage implements PreferenceStorage<TrayItem> {

    private String mModuleName;

    private TrayStorageType mType;

    public TrayStorage(final String moduleName, final TrayStorageType type) {
        mModuleName = moduleName;
        mType = type;
    }

    /**
     * Indicates where the data internally gets stored and how the backup is handled for the data
     *
     * @return the type of storage
     */
    public TrayStorageType getType() {
        return mType;
    }

    /**
     * imports all data from an old storage. The old storage gets wiped afterwards.
     * <p>
     * Use this if you have changed the module name
     *
     * @param oldStorage the old preference
     */
    public abstract void annex(final TrayStorage oldStorage);

    public String getModuleName() {
        return mModuleName;
    }

}
