package vertgreen.commandmeta;

public class MessagingException extends RuntimeException {

    public MessagingException(String str) {
        super(str);
    }

    public MessagingException(String str, Throwable cause) {
        super(str, cause);
    }
}
