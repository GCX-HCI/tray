package net.grandcentrix.tray.core;

/**
 * Generic Exception for the Tray library
 * <p>
 * Created by pascalwelsch on 5/13/15.
 */
public class TrayException extends Exception {

    public TrayException() {
    }

    public TrayException(final String detailMessage) {
        super(detailMessage);
    }

    public TrayException(final String detailMessage, Object... args) {
        super(String.format(detailMessage, args));
    }

    public TrayException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public TrayException(final Throwable throwable) {
        super(throwable);
    }
}
