package net.grandcentrix.tray.core;

/**
 * Thrown when accessing an item but there is no item for the given key
 * <p>
 * Created by pascalwelsch on 5/13/15.
 */
public class ItemNotFoundException extends TrayException {

    public ItemNotFoundException() {
    }

    public ItemNotFoundException(final String detailMessage) {
        super(detailMessage);
    }

    public ItemNotFoundException(final String detailMessage, final Object... args) {
        super(detailMessage, args);
    }

    public ItemNotFoundException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ItemNotFoundException(final Throwable throwable) {
        super(throwable);
    }
}
