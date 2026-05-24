package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;

import java.util.List;

public class ColorException extends InvalidOperationException {
    private final List<Color> availableColors;

    public ColorException(ErrorType type, List<Color> availableColors) {
        super(type);
        this.availableColors = availableColors;
    }

    public List<Color> getAvailableColors() {
        return availableColors;
    }
}
