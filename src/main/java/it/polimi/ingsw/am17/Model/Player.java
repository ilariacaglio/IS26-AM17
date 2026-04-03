package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;

public class Player{
    private final String nickname;
    private int pp;
    private int food;
    private final Color color;
    private List<CharacterCard> characterCards;
    private List<BuildingCard> buildingCards;

    public Player(String nickname, Color color)
    {
        this.nickname = nickname;
        this.color = color;
        this.characterCards = new ArrayList<>();
        this.buildingCards = new ArrayList<>();
    }

    public void addPp(int quantity){
        this.pp+=quantity;
    }

    public void addFood(int quantity) {
        int newAmount = food + quantity;
        if (newAmount < 0) {
            throw new IllegalStateException("Not enough food: requested change " + quantity + " having " + food);
        }
        this.food = newAmount;
    }

    public void calculateFinalPoints(){
        boolean iconPresent;

        // add pp of builders
        int pointsBuilders = characterCards.stream()
                    .filter(g ->g.getCardType().equals(CardType.BUILDER))
                    .mapToInt(g -> ((Builder) g).getPointBonus())
                    .sum();

        addPp(pointsBuilders);

        // add pp of inventors and icons
        int inventorCount = (int) characterCards.stream()
                .filter(c -> c.getCardType() == CardType.INVENTOR)
                .count();

        long uniqueIcons = characterCards.stream()
                .filter(c -> c.getCardType() == CardType.INVENTOR)
                .map(c -> ((Inventor) c).getIcon())
                .distinct()
                .count();

        addPp((int) (inventorCount * uniqueIcons));

        // add ten points for each artist couple
        int numArtists = (int) characterCards.stream()
                .filter(g ->g.getCardType().equals(CardType.ARTIST))
                .count();
        int numCouples = Math.floorDiv(numArtists,2);
        addPp(numCouples*10);

        // points of buildings
        int cardPoints = buildingCards.stream()
                .mapToInt(BuildingCard::getBonusPoints)
                .sum();
        addPp(cardPoints);

        // final effects of buildings
        for (BuildingCard card : buildingCards) {
            this.addPp(card.FinalPoints(characterCards));
        }
    }

    public void addCharacter(TribesCard card) {
        if (card.getCardType().isEvent()) {
            throw new IllegalArgumentException(
                    "Cannot add an event card to the player's character list."
            );
        }
        characterCards.add((CharacterCard) card);
    }

    public void addBuilding(BuildingCard card) {
        buildingCards.add(card);
    }


    /**
     * add cards to player (from playerAction)
     * TODO: add FoodBonusFromCardAcquisition
     * @param characterCards
     * @param buildingCards
     */
    public void addCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {

        for(CharacterCard card : characterCards){

            // get bonus food if hunter with icon
            if (card.getCardType().equals(CardType.HUNTER) && ((Hunter)card).isWithIcon()) {
                addFood(getNumberOfHunters());
            }

            addCharacter(card);
        }

        for (BuildingCard card : buildingCards){
            int cost = calculateBuildingCost(card);
            try {
                addFood(-cost);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Not enough food to buy building cards");
            }
            addBuilding(card);
        }
    }

    public int calculateBuildingCost(BuildingCard card) {
        //sum of the food discount of every builder card
        int foodDiscount = characterCards.stream()
                .filter(c->c.getCardType().equals(CardType.BUILDER))
                .map(c-> ((Builder)c).getFoodReduction())
                .reduce(0, Integer::sum);

        //return the price of the building card
        return card.getFoodCost() - foodDiscount;
    }

    private int getNumberOfHunters() {
        return (int) characterCards.stream()
                .filter(c->c.getCardType().equals(CardType.HUNTER))
                .count();
    }
}