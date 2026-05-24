package it.polimi.ingsw.am17.CommonInterfaces;

public class InvalidOperationException extends RuntimeException {
    private final ErrorType errorType;
    public InvalidOperationException(ErrorType errorType) {
        super("");
        this.errorType = errorType;
    }
    public InvalidOperationException(String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN;
    }
    public ErrorType getErrorType() {
        return errorType;
    }
}
