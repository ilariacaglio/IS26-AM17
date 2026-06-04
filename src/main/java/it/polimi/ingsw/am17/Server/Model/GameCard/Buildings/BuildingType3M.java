package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private final Integer pointsFromEachCharacter; // amount from each characterType
    private final CardType characterType;
    private static final GameState era = GameState.ERA3;

    private static final Logger logger = Logger.getLogger(BuildingType3M.class.getName());

    @JsonCreator
    public BuildingType3M(
            @JsonProperty("foodCost") Integer foodCost,
            @JsonProperty("bonusPoints") Integer bonusPoints,
            @JsonProperty("characterType") CardType characterType,
            @JsonProperty("pointsFromEachCharacter") Integer pointsFromEachCharacter) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @SuppressWarnings("unused") // needed for jackson
    public Integer getPointsFromEachCharacter() {
        return pointsFromEachCharacter;
    }

    @SuppressWarnings("unused") // needed for jackson
    public CardType getCharacterType() {
        return characterType;
    }

    @Override
    public int GetAdditionalFinalPoints(List<CharacterCard> playerCharacterCards) {
        int characterCount = (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
        int totalPoints = pointsFromEachCharacter * characterCount;
        logger.info("Adding " + totalPoints + " points to the player from BuildingType3M");
        return totalPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingType3M that = (BuildingType3M) o;
        return pointsFromEachCharacter.equals(that.pointsFromEachCharacter) && characterType == that.characterType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsFromEachCharacter, characterType);
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: + " + this.pointsFromEachCharacter + "PP/each " + characterType.toString() + "] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_3_"+characterType.toString().toLowerCase() +".png";
    }

}
