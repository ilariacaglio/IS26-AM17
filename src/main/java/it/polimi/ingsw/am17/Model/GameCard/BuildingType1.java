package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
 */
public class BuildingType1 extends BuildingCard {
    public BuildingType1() {
        super(3, 10, 0);
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {
        return 25;
    }
}
