package it.polimi.ingsw.am17.Model.GameCard;

public class Builder extends CharacterCard {
    private int pointBonus;
    private int foodReduction;

    public int getFoodReduction() {
        return foodReduction;
    }

    public int getPointBonus() {
        return pointBonus;
    }

    public Builder(int era, int minPlayers, int pointsBonus, int foodReduction) {
        super(era, minPlayers);
        this.pointBonus = pointsBonus;
        this.foodReduction = foodReduction;
    }
}
