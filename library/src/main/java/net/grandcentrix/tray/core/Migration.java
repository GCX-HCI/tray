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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Representing something which should be migrated with getters for the data and additional
 * information
 * <p>
 * Created by pascalwelsch on 2/25/15.
 */
public interface Migration<T> {

    /**
     * gets the data from the old data store.
     * <p>
     * Only primitive types are supported. See {@link Preferences#isDataTypeSupported(Object)}
     * <p>
     * called after {@link #shouldMigrate()} and before {@link #onPostMigrate(Object)}
     *
     * @return the data in a valid primitive format
     */
    @Nullable
    Object getData();

    /**
     * @return the imported key name. When this name changes, the data gets imported again.
     * Otherwise the data is only imported once
     */
    @NonNull
    String getPreviousKey();

    /**
     * @return the key where the data should be accessible in the future with Tray
     */
    @NonNull
    String getTrayKey();

    /**
     * this is a good point to delete the old data to free space and prevent accidentally import
     * later which could override newer data saved into Tray after the last import
     *
     * @param importedItem the imported item in tray. <code>null</code> if the import did not work.
     *                     A invalid data type my be the reason
     */
    void onPostMigrate(@Nullable final T importedItem);

    /**
     * called before {@link #getData()}. This is a good point to check if the data which should be
     * migrated is available. If not, return true if you want to cancel the import.
     * <p>
     * This check is very important, because the migration data should be deleted in {@link
     * #onPostMigrate(Object)}. When starting this migration a second time this method should
     * return {@code true}, to skip the migration, or the previous written data will be overridden
     * with the value from {@link #getData()} which should be {@code null} after the first
     * migration
     *
     * @return true if the import should be canceled
     */
    boolean shouldMigrate();
}
