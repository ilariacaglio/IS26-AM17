package it.polimi.ingsw.am17.Model.GameCard;

public class Builder extends CharacterCard {
    private final int pointBonus;
    private final int foodReduction;

    public int getFoodReduction() {
        return foodReduction;
    }

    public int getPointBonus() {
        return pointBonus;
    }

    public Builder(int era, int minPlayers, int pointsBonus, int foodReduction) {
        super(era, minPlayers,CardType.BUILDER);
        this.pointBonus = pointsBonus;
        this.foodReduction = foodReduction;
    }
}
