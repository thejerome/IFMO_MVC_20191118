package com.efimchik.ifmo.web.mvc;

public class OutOfRangeException extends IllegalArgumentException {

    public OutOfRangeException() {
        super();
    }

    public OutOfRangeException(String s) {
        super(s);
    }

    public OutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfRangeException(Throwable cause) {
        super(cause);
    }
}
