package info.ata4.bsplib;

public class LumpContentReadException extends Exception {
    public LumpContentReadException() {
    }

    public LumpContentReadException(String message) {
        super(message);
    }

    public LumpContentReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LumpContentReadException(Throwable cause) {
        super(cause);
    }

    public LumpContentReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
