package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayItem;
import net.grandcentrix.tray.core.TrayStorage;

import android.net.Uri;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by pascalwelsch on 9/17/15.
 */
public class ChangedListenerTest extends TrayProviderTestCase {

    public void testApiLevel15OnChange() throws Exception {
        final CountDownLatch latch = new CountDownLatch(
                2); // first time called in checkChangeListener() second time in this test
        final ArrayList<TrayItem> changed = new ArrayList<>();
        final OnTrayPreferenceChangeListener listener = new OnTrayPreferenceChangeListener() {

            @Override
            public void onTrayPreferenceChanged(final Collection<TrayItem> items) {
                changed.addAll(items);
                latch.countDown();
            }
        };
        final ContentProviderStorage storage = checkChangeListener(true, listener);

        storage.put("some", "value");
        storage.put("foo", "bar");

        new HandlerThread("change") {
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                storage.mObserver.onChange(false);
            }
        }.start();

        latch.await(3000, TimeUnit.MILLISECONDS);
        assertEquals(2, changed.size());
    }

    public void testListenerRegisteredFromLooperThread() throws Exception {
        checkChangeListener(true, null);
    }

    public void testListenerRegisteredFromThreadWithoutLooper() throws Exception {
        checkChangeListener(false, null);
    }

    public void testRegisterNull() throws Exception {
        final ContentProviderStorage storage = new ContentProviderStorage(
                getProviderMockContext(), "testRegisterNull", TrayStorage.Type.USER);
        storage.registerOnTrayPreferenceChangeListener(null);
        assertEquals(0, storage.mListeners.size());
    }

    public void testUnregister() throws Exception {
        final OnTrayPreferenceChangeListener listener = new OnTrayPreferenceChangeListener() {
            @Override
            public void onTrayPreferenceChanged(final Collection<TrayItem> items) {

            }
        };
        final ContentProviderStorage userStorage = checkChangeListener(true, listener);
        assertEquals(1, userStorage.mListeners.size());

        // unregister null should do nothing
        userStorage.unregisterOnTrayPreferenceChangeListener(null);
        assertEquals(1, userStorage.mListeners.size());

        userStorage.unregisterOnTrayPreferenceChangeListener(listener);
        assertEquals(0, userStorage.mListeners.size());
        assertNull(userStorage.mObserver);
        assertNull(userStorage.mObserverThread);

    }

    private ContentProviderStorage checkChangeListener(boolean registerWithLooper,
            final OnTrayPreferenceChangeListener otherListener) throws Exception {

        final ContentProviderStorage userStorage = new ContentProviderStorage(
                getProviderMockContext(), "testChanged", TrayStorage.Type.USER);

        final CountDownLatch listenerCalledLatch = new CountDownLatch(1);
        final boolean[] listenerCalled = {false};

        final Looper[] looperRegisteredOn = {null};

        final OnTrayPreferenceChangeListener listener = new OnTrayPreferenceChangeListener() {
            @Override
            public void onTrayPreferenceChanged(final Collection<TrayItem> items) {
                // check if called on the correct looper if one is set.
                assertEquals(looperRegisteredOn[0], Looper.myLooper());

                listenerCalled[0] = true;
                listenerCalledLatch.countDown();
            }
        };

        final CountDownLatch registerLatch = new CountDownLatch(1);

        if (registerWithLooper) {
            // registers in a thread with a looper
            new HandlerThread("register") {
                @Override
                protected void onLooperPrepared() {
                    super.onLooperPrepared();
                    looperRegisteredOn[0] = Looper.myLooper();
                    userStorage.registerOnTrayPreferenceChangeListener(listener);
                    if (otherListener != null) {
                        userStorage.registerOnTrayPreferenceChangeListener(otherListener);
                    }
                    registerLatch.countDown();
                }
            }.start();
        } else {
            // registers in a thread without a looper
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userStorage.registerOnTrayPreferenceChangeListener(listener);
                    registerLatch.countDown();
                }
            }).start();
        }

        registerLatch.await(1000, TimeUnit.MILLISECONDS);
        assertNotNull(userStorage.mObserver);
        assertNotNull(userStorage.mObserverThread);
        assertFalse(listenerCalled[0]);

        final TrayUri trayUri = new TrayUri(getProviderMockContext());
        final Uri uri = trayUri.get();

        if (registerWithLooper) {
            // if not a looper thread the handler in the onChange method does not fire
            new HandlerThread("change") {
                @Override
                protected void onLooperPrepared() {
                    super.onLooperPrepared();
                    // in a perfect world I could change a value in the storage an it will call the
                    // listener. But this is andorid and testing is hard. Even harder with threading.
                    // The hardest thing here is the Isolated context, and the dependency to the Looper
                    // to get the change from the content provider.
                    //
                    // tl;dr the ContentObserver does not work in a ProviderTestCase2 so I call the
                    // listener myself instead of changing data.
                    //
                    // wasted hours so far: 12

                    // userStorage.put("the", "change");
                    userStorage.mObserver.onChange(false, uri);
                }
            }.start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // see explanation above

                    // userStorage.put("the", "change");
                    userStorage.mObserver.onChange(false, uri);
                }
            }).start();
        }

        listenerCalledLatch.await(3000, TimeUnit.MILLISECONDS);

        assertTrue(listenerCalled[0]);

        userStorage.unregisterOnTrayPreferenceChangeListener(listener);

        return userStorage;
    }
}
