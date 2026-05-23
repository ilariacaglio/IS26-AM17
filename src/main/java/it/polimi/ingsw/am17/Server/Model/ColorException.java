package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;

import java.util.List;

public class ColorException extends Exception {
    private final List<Color> availableColors;

    public ColorException(String message, List<Color> availableColors) {
        super(message);
        this.availableColors = availableColors;
    }

    public List<Color> getAvailableColors() {
        return availableColors;
    }
}
