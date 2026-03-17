package it.polimi.ingsw.am17.Model.GameCard;

public class BuildingCard extends GameCard{
    private int foodCost;
    private int bonusPoints;

    public BuildingCard(int era, int foodCost, int bonusPoints) {
        super(era);
        this.foodCost = foodCost;
        this.bonusPoints = bonusPoints;
    }


    public void buyBuilding(){}
}
