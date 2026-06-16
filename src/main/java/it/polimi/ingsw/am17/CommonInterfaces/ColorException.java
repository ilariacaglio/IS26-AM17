package it.polimi.ingsw.am17.CommonInterfaces;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.Color;

import java.util.List;

/**
 * Exception thrown when a player tries to join a game and the color he has is already chosen
 * It is a subtype because it contains the list of the available colors of that game
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorException extends InvalidOperationException {
    private final List<Color> availableColors;

    @JsonCreator
    public ColorException(
            @JsonProperty("errorType") ErrorType type,
            @JsonProperty("availableColors") List<Color> availableColors) {
        super(type);
        this.availableColors = availableColors;
    }

    @JsonProperty("availableColors")
    public List<Color> getAvailableColors() {
        return availableColors;
    }
}
