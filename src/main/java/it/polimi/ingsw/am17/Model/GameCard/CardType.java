package it.polimi.ingsw.am17.Model.GameCard;

public enum CardType {
    INVENTOR,
    BINDER,
    SHAMAN,
    ARTIST,
    HUNTER,
    BUILDER,
    RITUAL_EVENT,
    HUNTING_EVENT,
    PAINTING_EVENT,
    FOOD_EVENT;
    public boolean isCharacter() {
        return !this.name().endsWith("_EVENT");
    }

    public boolean isEvent() {
        return !isCharacter();
    }
}
