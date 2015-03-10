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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p/>
 * basic functionality for every storage implementation
 */
public interface PreferenceStorage<T> {

    public void clear();

    public T get(@NonNull final String key);

    public Collection<T> getAll();

    public int getVersion();

    public void put(@NonNull final String key, @Nullable final String migrationKey,
            @Nullable final Object o);

    public void put(@NonNull final String key, @Nullable final Object o);

    public void remove(@NonNull final String key);

    public void setVersion(final int version);

}
