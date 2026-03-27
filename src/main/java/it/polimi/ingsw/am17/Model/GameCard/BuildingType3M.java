package it.polimi.ingsw.am17.Model.GameCard;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private int pointsFromEachCharacter; // Extra pointsFromEachCharacter for each characterType
    private CharacterCard characterType;

    public BuildingType3M(int foodCost, int bonusPoints, CharacterCard characterType, int pointsFromEachCharacter) {
        super(3, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @Override
    public int FinalPoints(List<GameCard> playerTribeRow) {
        // TODO: implement with attribute check

        int characterCount = 0;
        return characterCount * pointsFromEachCharacter;
    }
}
