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

import java.util.Collection;

/**
 * Created by pascalwelsch on 11/20/14.
 *
 * basic functionality for every storage implementation
 */
public interface PreferenceStorage<T> {

    public void clear();

    public T get(String key);

    public Collection<T> getAll();

    public void put(String key, Object o);

    public void remove(String key);

}
