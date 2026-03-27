package it.polimi.ingsw.am17.Model.GameCard;

/**
 * Get double the points from Builders
 * Final effect
 * SINGLETON
 */
public class BuildingType6 extends BuildingCard {
    public BuildingType6() {
        super(2, 5, 6);
    }

    @Override
    public int FinalPoints(List<GameCard> playerTribeRow) {
        // TODO: get builders points and return them (not doubled)
        return 0;
    }
}
