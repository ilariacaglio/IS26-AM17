package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;

public class InvalidOperationException extends RuntimeException {
    private final ErrorType errorType;
    public InvalidOperationException(ErrorType errorType) {
        this.errorType = errorType;
    }
    public ErrorType getErrorType() {
        return errorType;
    }
}
