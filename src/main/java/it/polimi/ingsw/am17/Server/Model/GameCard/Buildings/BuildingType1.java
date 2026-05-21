package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.List;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
 */
public class BuildingType1 extends BuildingCard {
    private static final GameState era = GameState.ERA3;
    private static final int foodCost = 10;
    private static final int bonusPoints = 0;

    public BuildingType1() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetAdditionalFinalPoints(List<CharacterCard> playerCharacterCards) {
        return 25;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +25PP] ";
    }
}
