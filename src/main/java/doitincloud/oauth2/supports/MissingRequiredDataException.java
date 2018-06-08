package doitincloud.oauth2.supports;

public class MissingRequiredDataException extends RuntimeException {

    public MissingRequiredDataException(String message) {
        super(message);
    }
}
