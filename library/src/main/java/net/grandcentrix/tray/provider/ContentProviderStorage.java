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

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayException;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayLog;
import net.grandcentrix.tray.core.TrayRuntimeException;
import net.grandcentrix.tray.core.TrayStorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by pascalwelsch on 11/20/14.
 * <p>
 * Implements the functionality between the {@link TrayPreferences}
 * and the {@link TrayContentProvider}. Uses functions of the {@link
 * TrayProviderHelper} for simple and unified access to the
 * provider.
 * <p>
 * This class represents a simple key value storage solution based on a {@link
 * android.content.ContentProvider}. Replacing this class with a {@link java.util.HashMap}
 * implementation for testing works seamless.
 */
public class ContentProviderStorage extends TrayStorage {

    /**
     * Forwards changes of this storage to the registered listeners
     */
    @VisibleForTesting
    class TrayContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public TrayContentObserver(@NonNull final Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(final boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(final boolean selfChange, Uri uri) {
            if (uri == null) {
                // for sdk version 15 and below we cannot detect which exact data was changed. This will
                // return all data for this module
                uri = mTrayUri.builder().setModule(getModuleName()).build();
            }

            // query only the changed items
            final List<TrayItem> trayItems = mProviderHelper.queryProviderSafe(uri);

            // clone to get around ConcurrentModificationException
            final Set<Map.Entry<OnTrayPreferenceChangeListener, Handler>> entries
                    = new HashSet<>(mListeners.entrySet());

            // notify all registered listeners
            for (final Map.Entry<OnTrayPreferenceChangeListener, Handler> entry : entries) {
                final OnTrayPreferenceChangeListener listener = entry.getKey();
                final Handler handler = entry.getValue();
                if (handler != null) {
                    // call the listener on the thread where the listener was registered
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onTrayPreferenceChanged(trayItems);
                        }
                    });
                } else {
                    listener.onTrayPreferenceChanged(trayItems);
                }
            }
        }
    }

    public static final String VERSION = "version";

    /**
     * weak references to the listeners. Only the keys are used.
     */
    @VisibleForTesting
    WeakHashMap<OnTrayPreferenceChangeListener, Handler> mListeners = new WeakHashMap<>();

    /**
     * observes data changes for this storage
     */
    @VisibleForTesting
    TrayContentObserver mObserver;

    /**
     * the looper thread which runs the {@link #mObserver}. Only started when listeners registered
     */
    @VisibleForTesting
    HandlerThread mObserverThread;

    private final Context mContext;

    private final TrayProviderHelper mProviderHelper;

    private volatile boolean mRegisteredContentObserver = false;

    private final TrayUri mTrayUri;

    public ContentProviderStorage(@NonNull final Context context, @NonNull final String module,
            @NonNull final Type type) {
        super(module, type);
        mContext = context.getApplicationContext();
        mTrayUri = new TrayUri(mContext);
        mProviderHelper = new TrayProviderHelper(mContext);
    }

    @Override
    public void annex(final TrayStorage oldStorage) {
        for (final TrayItem trayItem : oldStorage.getAll()) {
            put(trayItem);
        }
        // ignore result
        oldStorage.wipe();
    }

    @Override
    public boolean clear() {
        final Uri uri = mTrayUri.builder()
                .setModule(getModuleName())
                .setType(getType())
                .build();
        return mProviderHelper.remove(uri);
    }

    @Override
    @Nullable
    public TrayItem get(@NonNull final String key) {
        final Uri uri = mTrayUri.builder()
                .setType(getType())
                .setModule(getModuleName())
                .setKey(key)
                .build();
        final List<TrayItem> prefs = mProviderHelper.queryProviderSafe(uri);
        final int size = prefs.size();
        if (size > 1) {
            TrayLog.w("found more than one item for key '" + key
                    + "' in module " + getModuleName() + ". "
                    + "This can be caused by using the same name for a device and user specific preference.");
            for (int i = 0; i < prefs.size(); i++) {
                final TrayItem pref = prefs.get(i);
                TrayLog.d("item #" + i + " " + pref);
            }
        }
        return size > 0 ? prefs.get(0) : null;
    }

    @NonNull
    @Override
    public Collection<TrayItem> getAll() {
        final Uri uri = mTrayUri.builder()
                .setType(getType())
                .setModule(getModuleName())
                .build();
        return mProviderHelper.queryProviderSafe(uri);
    }

    /**
     * @return the context {@link android.app.Application} bound to this storage to communicate via
     * {@link android.content.ContentResolver}
     */
    public Context getContext() {
        return mContext;
    }

    @Override
    public int getVersion() throws TrayException {
        final Uri internalUri = mTrayUri.builder()
                .setInternal(true)
                .setType(getType())
                .setModule(getModuleName())
                .setKey(VERSION)
                .build();
        final List<TrayItem> trayItems = mProviderHelper.queryProvider(internalUri);
        if (trayItems.size() == 0) {
            // fallback, not found
            return 0;
        }
        return Integer.valueOf(trayItems.get(0).value());
    }

    @Override
    public boolean put(final TrayItem item) {
        return put(item.key(), item.migratedKey(), item.value());
    }

    @Override
    public boolean put(@NonNull final String key, @Nullable final Object data) {
        return put(key, null, data);
    }

    /**
     * same as {@link #put(String, Object)} but with an additional migration key to save where the
     * data came from. Putting data twice with the same param migraionKey does not override the
     * already saved data. This should prevent migrating data multiple times while the data my be
     * edited with {@link #put(String, Object)}.
     *
     * @param key          where to save
     * @param migrationKey where the data came from
     * @param data         what to save
     * @return whether the put was successful
     */
    @Override
    public boolean put(@NonNull final String key, @Nullable final String migrationKey,
            @Nullable final Object data) {
        if (getType() == Type.UNDEFINED) {
            throw new TrayRuntimeException(
                    "writing data into a storage with type UNDEFINED is forbidden. Only Read and delete is allowed.");
        }

        final String value = data == null ? null : String.valueOf(data);

        final Uri uri = mTrayUri.builder()
                .setType(getType())
                .setModule(getModuleName())
                .setKey(key)
                .build();
        return mProviderHelper.persist(uri, value, migrationKey);
    }

    /**
     * registers a listener for changed data which gets called asynchronously when a change from
     * the {@link TrayContentProvider} was detected
     * <p>
     * sdk version 15 is only partially supported. the listener will provide all data for this
     * module and not only the changed ones because {@link ContentObserver#onChange(boolean, Uri)}
     * was introduced in sdk version 16
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public synchronized void registerOnTrayPreferenceChangeListener(
            @NonNull final OnTrayPreferenceChangeListener listener) {
        // noinspection ConstantConditions
        if (listener == null) {
            return;
        }

        // save a handler associated with the calling looper to call the callback on the same thread
        // noinspection ConstantConditions
        Handler handler = null;
        final Looper looper = Looper.myLooper();
        if (looper != null) {
            handler = new Handler(looper);
        }
        //noinspection ConstantConditions
        mListeners.put(listener, handler);

        final Collection<OnTrayPreferenceChangeListener> listeners = mListeners.keySet();

        if (listeners.size() == 1) {

            // registering a TrayContentObserver requires a LooperThread
            mObserverThread = new HandlerThread("observer") {
                @Override
                protected void onLooperPrepared() {
                    super.onLooperPrepared();
                    mObserver = new TrayContentObserver(new Handler(getLooper()));

                    // register observer
                    final Uri observingUri = mTrayUri.builder()
                            .setType(getType())
                            .setModule(getModuleName())
                            .build();
                    mContext.getContentResolver()
                            .registerContentObserver(observingUri, true, mObserver);
                    mRegisteredContentObserver = true;
                }
            };
            mObserverThread.start();

            // wait synchronously until the mObserverThread registered the mObserver
            // cannot use Thread.join(); because the Looper of the HandlerThread runs forever until killed
            while (true) {
                if (mRegisteredContentObserver) {
                    mRegisteredContentObserver = false;
                    break;
                }
            }
        }
    }

    @Override
    public boolean remove(@NonNull final String key) {
        //noinspection ConstantConditions
        if (key == null) {
            throw new IllegalArgumentException(
                    "null is not valid. use clear or wipe to delete all preferences");
        }
        final Uri uri = mTrayUri.builder()
                .setType(getType())
                .setModule(getModuleName())
                .setKey(key)
                .build();
        return mProviderHelper.removeAndCount(uri) > 0;
    }

    @Override
    public boolean setVersion(final int version) {
        if (getType() == Type.UNDEFINED) {
            throw new TrayRuntimeException(
                    "writing data into a storage with type UNDEFINED is forbidden. Only Read and delete is allowed.");
        }
        final Uri uri = mTrayUri.builder()
                .setInternal(true)
                .setType(getType())
                .setModule(getModuleName())
                .setKey(VERSION)
                .build();
        return mProviderHelper.persist(uri, String.valueOf(version));
    }

    public void unregisterOnTrayPreferenceChangeListener(
            @NonNull final OnTrayPreferenceChangeListener listener) {
        // noinspection ConstantConditions
        if (listener == null) {
            return;
        }
        mListeners.remove(listener);

        if (mListeners.size() == 0) {
            mContext.getContentResolver().unregisterContentObserver(mObserver);
            // cleanup
            mObserver = null;
            mObserverThread.quit();
            mObserverThread = null;
        }
    }

    /**
     * clear the data inside the preference and all evidence this preference has ever existed
     * <p>
     * also cleans internal information like the version for this preference
     *
     * @see #clear()
     */
    public boolean wipe() {
        final boolean cleared = clear();

        if (!cleared) {
            // clear wasn't successful, don't even start clearing the internal stuff
            return false;
        }

        final Uri uri = mTrayUri.builder()
                .setInternal(true)
                .setType(getType())
                .setModule(getModuleName())
                .build();
        return mProviderHelper.remove(uri);
    }
}
