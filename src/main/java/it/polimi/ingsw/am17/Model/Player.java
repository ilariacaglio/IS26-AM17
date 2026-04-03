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

}