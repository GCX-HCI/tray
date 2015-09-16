package net.grandcentrix.tray.core;

/**
 * Accessed values which where saved as a different type
 *
 * Created by pascalwelsch on 5/13/15.
 */
public class WrongTypeException extends TrayRuntimeException {

    public WrongTypeException() {
    }

    public WrongTypeException(final String detailMessage) {
        super(detailMessage);
    }

    public WrongTypeException(final String detailMessage, final Object... args) {
        super(detailMessage, args);
    }

    public WrongTypeException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WrongTypeException(final Throwable throwable) {
        super(throwable);
    }
}
