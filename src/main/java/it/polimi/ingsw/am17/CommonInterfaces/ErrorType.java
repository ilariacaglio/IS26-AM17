package it.polimi.ingsw.am17.CommonInterfaces;

public enum ErrorType {
    // handling game and players
    INVALID_NUMBER_OF_PLAYERS ("Wrong number of players!"),
    GAME_ALREADY_STARTED("The game has already started!"),
    DUPLICATE_NICKNAME("The game has already a player with the same nickname!"),
    DUPLICATE_COLOR("The game has already a player with the same color!"),
    INVALID_GAME_STATE("Invalid game state"),

    // offering card selection
    MISSING_OFFERING_CARD_LETTER ("OfferingCardLetter can't be null!"),
    INVALID_OFFERING_CARD_LETTER("Illegal card selection (Card not found)!"),
    UNAVAILABLE_OFFERING_CARD("Illegal card selection (Card already selected by another player)!"),
    OFFERING_CARD_ALREADY_SELECTED("Illegal card selection (Offering Card already selected)!"),

    // card selection
    INVALID_CHARACTER_CARD("Illegal character selection (Card not in any row)!"),
    INVALID_BUILDING_CARD("Illegal building selection (Card not in any row)!"),
    INVALID_CARDS_NUMBER("Illegal card selection (Wrong number of cards)!"),

    // game moves
    OUT_OF_TURN ("It is not your turn!"),
    INSUFFICIENT_FOOD("Not enough food to buy building cards"),

    // default
    UNKNOWN("There was an error handling your request");

    private final String message;

    ErrorType(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
