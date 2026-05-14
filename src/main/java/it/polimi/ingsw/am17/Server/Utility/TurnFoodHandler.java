package it.polimi.ingsw.am17.Server.Utility;

import java.util.logging.Logger;

public class TurnFoodHandler {
    private static final Logger logger = Logger.getLogger(TurnFoodHandler.class.getName());
    public static int[] getTurnFoodPoints(int numPlayers) {
        logger.fine("Getting turn food points for " + numPlayers + " players.");

        return switch (numPlayers) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{2, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            case 5 -> new int[]{3, 1, 0, 0, -1};
            default -> throw new IllegalStateException("Wrong number of players");
        };
    }
}
