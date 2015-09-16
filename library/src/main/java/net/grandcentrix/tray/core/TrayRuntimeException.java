package net.grandcentrix.tray.core;

/**
 * Generic RuntimeException for the Tray library
 * <p>
 * Created by pascalwelsch on 5/13/15.
 */
public class TrayRuntimeException extends RuntimeException {

    public TrayRuntimeException() {
    }

    public TrayRuntimeException(final String detailMessage) {
        super(detailMessage);
    }

    public TrayRuntimeException(final String detailMessage, Object... args) {
        super(String.format(detailMessage, args));
    }

    public TrayRuntimeException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public TrayRuntimeException(final Throwable throwable) {
        super(throwable);
    }
}
