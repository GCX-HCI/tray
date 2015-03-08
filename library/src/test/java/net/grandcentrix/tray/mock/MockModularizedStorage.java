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

package net.grandcentrix.tray.mock;

import net.grandcentrix.tray.provider.TrayItem;
import net.grandcentrix.tray.storage.ModularizedStorage;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pascalwelsch on 11/21/14.
 */
public class MockModularizedStorage extends ModularizedStorage<TrayItem> {

    private HashMap<String, TrayItem> data = new HashMap<>();

    private int version = 0;

    public MockModularizedStorage(final String module) {
        super(module);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public TrayItem get(final String key) {
        return data.get(key);
    }

    @Override
    public Collection<TrayItem> getAll() {
        return data.values();
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void put(final String key, final Object o) {
        final String value = String.valueOf(o);
        final TrayItem item = new TrayItem(new Date(), key, getModule(), new Date(), value, null);
        data.put(key, item);
    }

    @Override
    public void remove(final String key) {
        data.remove(key);
    }

    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
}
