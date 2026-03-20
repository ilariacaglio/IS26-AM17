package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.GameCard;
import java.util.List;

public class Player extends Subject{
    private final String nickname;
    private int pp;
    private int food;
    private final Color color;
    private final PlayerRow playerRow;
    private OfferingCard offeringCard;

    public Player(String nickname, Color color)
    {
        this.nickname = nickname;
        this.color = color;
        playerRow = new PlayerRow();
    }

    public void addPp(int quantity){
        this.pp+=quantity;
    }

    public void addFood(int quantity) {
        this.food+=quantity;
    }

    public OfferingCard getOfferingCard() {
        return offeringCard;
    }

    public int getPp()
    {
        return pp;
    }

    public int getFood()
    {
        return food;
    }

    public Color getColor()
    {
        return color;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setOfferingCard(OfferingCard offeringCard) {
        this.offeringCard = offeringCard;
    }

    public void freeOfferingCard() {
        this.offeringCard = null;
    }

    public void playTurn(List<GameCard> cards, Game game){
        for(GameCard card : cards){
            if(card instanceof BuildingCard){
                buyBuilding((BuildingCard) card);
            }
            game.removeCardFromRow(card);
            playerRow.addCard(card);
        }
    }

    public void buyBuilding(BuildingCard card){
        addFood(card.getFoodCost()*(-1));
    }
}
