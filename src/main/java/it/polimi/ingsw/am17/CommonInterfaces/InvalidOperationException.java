package it.polimi.ingsw.am17.CommonInterfaces;

import com.fasterxml.jackson.annotation.*;

/**
 * Custom exception used to notify the error occurred to caller method or to client
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "errorType",
        visible = true,
        defaultImpl = InvalidOperationException.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ColorException.class, name = "DUPLICATE_COLOR")
})
@JsonIgnoreProperties({"stackTrace", "cause", "localizedMessage", "suppressed"})
public class InvalidOperationException extends RuntimeException {
    private final ErrorType errorType;

    // constructor for deserialization
    @JsonCreator
    public InvalidOperationException(
            @JsonProperty("errorType") ErrorType errorType,
            @JsonProperty("message") String message) {
        // handle null values
        super(message != null ? message : "");
        this.errorType = errorType != null ? errorType : ErrorType.UNKNOWN;
    }

    public InvalidOperationException(ErrorType errorType) {
        super("");
        this.errorType = errorType;
    }

    public InvalidOperationException(String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN;
    }

    @JsonProperty("errorType")
    public ErrorType getErrorType() {
        return errorType;
    }

    @JsonProperty("message")
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
