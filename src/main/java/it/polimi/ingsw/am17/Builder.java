package it.polimi.ingsw.am17;

public class Builder extends CharacterCard{
    private int pointsBonus;
    private int foodReduction;

    public Builder(int era, int minPlayers, int pointsBonus, int foodReduction) {
        super(era, minPlayers);
        this.pointsBonus = pointsBonus;
        this.foodReduction = foodReduction;
    }
}
