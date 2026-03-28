package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;

public class Player{
    private final String nickname;
    private int pp;
    private int food;
    private final Color color;
    private final TribeCardRow playerTribeRow;
    private final BuildingCardRow playerBuildingRow;
    private OfferingCard offeringCard;

    public Player(String nickname, Color color)
    {
        this.nickname = nickname;
        this.color = color;
        this.playerBuildingRow = new BuildingCardRow();
        this.playerTribeRow = new TribeCardRow();
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

    public void playTurn(List<TribesCard> tribeCards, List<BuildingCard> buildingCards, Game game){
        if(!tribeCards.equals(Collections.emptyList())){
            for(TribesCard card : tribeCards){
                //aggiungere extra food in base ai cacciatori?
                game.removeTribeCardFromRow(card);
                playerTribeRow.addCard(card);
            }
        }
        if(!buildingCards.equals(Collections.emptyList())){
            for(BuildingCard card : buildingCards){
                //aggiungere riduzione costo in base ai costruttori?
                buyBuilding((BuildingCard) card);
                game.removeBuildingCardFromRow(card);
                playerBuildingRow.addCard(card);
            }
        }
    }

    public void buyBuilding(BuildingCard card){
        addFood(card.getFoodCost()*(-1));
    }

    public List<TribesCard> getPlayerTribeCards(){
        return playerTribeRow.getCards();
    }

    public List<BuildingCard> getPlayerBuildingCards(){return playerBuildingRow.getCards();}

    public void calculateFinalPoints(){
        boolean iconPresent;
        // add pp of builders
        int pointsBuilders = playerTribeRow.getCards().stream()
                    .filter(g ->g.getCardType().equals(CardType.BUILDER))
                    .mapToInt(g -> ((Builder) g).getPointBonus())
                    .sum();
        addFood(pointsBuilders);
        // add pp of inventors and icons
        List<Inventor> inventorsList = playerTribeRow.getCards().stream()
                    .filter(g ->g.getCardType().equals(CardType.INVENTOR))
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
                .filter(g ->g.getCardType().equals(CardType.ARTIST))
                .count();
        int numCouples = Math.floorDiv(numArtists,2);
        addFood(numCouples*10);
        // points of buildings
        List<BuildingCard> buildingsList = playerBuildingRow.getCards();
        int cardPoints = buildingsList.stream()
                .mapToInt(BuildingCard::getBonusPoints)
                .sum();
        addPp(cardPoints);
        // final effects of buildings
        var characterList = playerTribeRow.getCards().stream()
                .filter(c -> c.getCardType().isCharacter())
                .map(c-> (CharacterCard)c).toList();
        for (BuildingCard card : buildingsList) {
            this.addPp(card.FinalPoints(characterList));
        }
    }
}