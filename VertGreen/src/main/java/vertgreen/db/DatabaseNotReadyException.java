package vertgreen.db;

import vertgreen.commandmeta.MessagingException;

public class DatabaseNotReadyException extends MessagingException {

    private static final String DEFAULT_MESSAGE = "The database is not available currently. Please try again in a moment.";

    DatabaseNotReadyException(String str, Throwable cause) {
        super(str, cause);
    }

    DatabaseNotReadyException(String str) {
        super(str);
    }

    DatabaseNotReadyException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    DatabaseNotReadyException() {
        super(DEFAULT_MESSAGE);
    }
}
