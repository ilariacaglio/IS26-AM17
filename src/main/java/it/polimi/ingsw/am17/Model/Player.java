package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player extends Subject{
    private final String nickname;
    private int pp;
    private int food;
    private final Color color;
    private final CardRow playerTribeRow;
    private final CardRow playerBuildingRow;
    private OfferingCard offeringCard;

    public Player(String nickname, Color color, CardRow playerBuildingRow)
    {
        this.nickname = nickname;
        this.color = color;
        this.playerBuildingRow = new CardRow();
        this.playerTribeRow = new CardRow();
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
        //trovare modo per togliere instanceof
        for(GameCard card : cards){
            if(card instanceof BuildingCard){
                buyBuilding((BuildingCard) card);
            }
            game.removeCardFromRow(card);
            playerTribeRow.addCard(card);
        }
    }

    public void buyBuilding(BuildingCard card){
        addFood(card.getFoodCost()*(-1));
    }

    public List<GameCard> getPlayerTribeCards(){
        return playerTribeRow.getCards();
    }

    public List<GameCard> getPlayerBuildingCards(){return playerBuildingRow.getCards();}

    public void calculateFinalPoints(){
        //trovare modo per togliere instanceof
        boolean iconPresent;
        // add pp of builders
        int pointsBuilders = playerTribeRow.getCards().stream()
                    .filter(g -> g instanceof Builder)
                    .mapToInt(g -> ((Builder) g).getPointBonus())
                    .sum();
        addFood(pointsBuilders);
        // add pp of inventors and icons
        List<Inventor> inventorsList = playerTribeRow.getCards().stream()
                    .filter(g -> g instanceof Inventor)
                    .map(g -> (Inventor)g)
                    .toList();
        Set<Inventor> inventorIcons = new HashSet<>();
        for(Inventor inventor : inventorsList){
            iconPresent = false;
            for (Inventor i: inventorIcons){
                if(inventor.getIcon().equals(i.getIcon())){
                    iconPresent = true;
                }
            }
            if(!iconPresent){
                inventorIcons.add(inventor);
            }
        }
        addPp(inventorsList.size()*inventorIcons.size());
        // add 10 point for artist couples
        int numArtists = (int) playerTribeRow.getCards().stream()
                .filter(g -> g instanceof Artist)
                .count();
        int numCouples = Math.floorDiv(numArtists,2);
        addFood(numCouples*10);
        // points of buildings
        List<BuildingCard> buildingsList = playerBuildingRow.getCards().stream()
                .map (g -> (BuildingCard)g)
                .toList();
        int cardPoints = buildingsList.stream()
                .mapToInt(c -> c.getBonusPoints())
                .sum();
        addPp(cardPoints);
        // final effects of buildings
        for (BuildingCard card : buildingsList) {
            card.resolveEffect(this);
        }
    }
}