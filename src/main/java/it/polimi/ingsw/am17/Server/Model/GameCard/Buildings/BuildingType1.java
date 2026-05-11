package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.util.List;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
 */
public class BuildingType1 extends BuildingCard {
    private static final int era = 3;
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

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_1.png";
    }
}
