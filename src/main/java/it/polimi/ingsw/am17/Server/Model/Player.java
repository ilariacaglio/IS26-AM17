package it.polimi.ingsw.am17.Server.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;

import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    private String nickname;
    private int pp;
    private int food;
    private Color color;
    private List<CharacterCard> characterCards;
    private List<BuildingCard> buildingCards;

    @JsonCreator
    public Player(@JsonProperty("nickname") String nickname, @JsonProperty("color") Color color) {
        this.nickname = nickname;
        this.color = color;
        this.characterCards = new ArrayList<>();
        this.buildingCards = new ArrayList<>();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c)
    {
        this.color = c;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname=nickname;
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
            this.addPp(card.GetAdditionalFinalPoints(characterCards));
        }
    }

    public void addCharacter(TribesCard card) {
        if (card.getCardType().isEvent()) {
            throw new IllegalArgumentException(
                    "Cannot add an event card to the player's character list."
            );
        }
        //if player has buildingType14 (and all conditions from building are met add food)
        int foodFromBuildingType14 = 0;
        for(BuildingCard buildingCard : buildingCards)
        {
            foodFromBuildingType14+= buildingCard.GetFoodBonusFromCardAcquisition(characterCards, (CharacterCard)card);
        }
        addFood(foodFromBuildingType14);
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

            // add the Hunter card to the player's tribe first (as it is supposed to be counted towards food)
            addCharacter(card);

            // get bonus food for each Hunter if the Hunter has the icon
            if (card.getCardType().equals(CardType.HUNTER) && ((Hunter)card).isWithIcon()) {
                addFood(getNumberOfHunters());
            }
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

    public boolean hasBuilding2(){
        for(BuildingCard b : buildingCards){
           if(b.hasOneMoreMove())
               return true;
        }
        return false;
    }

    public int addFoodToTurnFood()
    {
        // if the player has building type 11 has an additional food point
        int food = 0;
        for(BuildingCard card : buildingCards){
            food+= card.GetFoodBonusFromTurnOrder();
        }
        return food;
    }

    /**
     * Calculate how much food player has to pay and removes it or removes pp
     * @param pointLost number of point lost if player doesn't have enough food
     */
   public void solveFoodEvent(int pointLost)
   {
       //count number of Binder
       long numBinder = this.characterCards.stream()
               .filter(c -> c.getCardType().equals(CardType.BINDER))
               .count();
       //count foodDiscount given by BuildingCard
       int foodDiscount =0;

       for(BuildingCard c: this.buildingCards){
           foodDiscount += c.GetFoodDiscountInFoodEvent(this.characterCards);
       }
       //count totalDiscount given by numBinder and foodDiscount
       int totalDiscount = Math.toIntExact((3*numBinder) + foodDiscount);
       //count totalCards, witch are all the player cards
       int totalCards = this.characterCards.size();
       //find foodPrice, witch is what the player has to pay
       int foodPrice = totalCards - totalDiscount;
       //if foodPrice<0, the player doesn't lose pp nor food
       if(foodPrice<=0){
           addPp(0);
           addFood(0);
       }//if food is not enough, player loses pp and all the food he has
       else if (food < foodPrice) {
           int remaining = foodPrice - food;

           int lostPp = pointLost * remaining;

           addPp(lostPp * (-1));
           addFood(food * (-1));
       } //if food is enough
       else {
           addFood(foodPrice * (-1));
       }
   }

   public void solveHuntingEvent(int pointEarned)
   {
       int totalFood=0;
       int totalPP=0;
       //count number of hunter
       long numHunter = this.characterCards.stream()
               .filter(c -> c.getCardType().equals(CardType.HUNTER))
               .count();
       //if player has hunter cards, they get food and PP
       if(numHunter!=0){
           totalFood+= Math.toIntExact(numHunter);
           totalPP = Math.toIntExact(pointEarned * numHunter);
       }
       //find additional food and PP given by buildingCard
       for(BuildingCard c: this.buildingCards){
           totalFood += c.AddFoodPerHunterInHuntingEvent(this.characterCards);
           totalPP += c.AddPointPerHunterInHuntingEvent(this.characterCards);
       }
       //add food and points
       this.addFood(totalFood);
       this.addPp(totalPP);
   }

   public void solvePaintingEvent(int numMax, int pointsMax, int pointsLow){
       //count number of artists
       int numArtist = (int) characterCards.stream()
               .filter(c-> c.getCardType().equals(CardType.ARTIST))
               .count();
       //assign PP based on number of artists
       if(numArtist>=numMax){
           addPp(numArtist * pointsMax);
       }
       else {
           addPp(pointsLow*(-1));
       }

       int additionalFood=0;


       //find additional food given by buildingCard
       for(BuildingCard c: buildingCards){
           additionalFood += c.AddFoodPerArtistInPaintingEvent(this.characterCards);
       }
       //add additionalFood
       addFood(additionalFood);
   }

   public int calculateStarPoints()
   {
       int starBonus=0;

       //additional stars given by BuildingType9
       for(BuildingCard c: this.buildingCards){
           starBonus += c.GiveBonusStarInRitualEvent(this.characterCards);//BuildingType9
       }
       //count number of star icons
       int stars = this.characterCards.stream()
               .filter(c -> c.getCardType().equals(CardType.SHAMAN))
               .mapToInt(c -> ((Shaman)c).getStars())
               .sum();
       //add starBonus given by BuildingType9
       return stars + starBonus;
   }

   public boolean hasDoubleRitualEventPoints()
   {
       for(BuildingCard c: this.buildingCards){
           if( c.hasDoubleRitualEventPoints())//BuildingType8
               return true;
       }
       return false;
   }

   public boolean hasShieldFromRitualEvent(){
        for(BuildingCard c: this.buildingCards){
            if(c.isShieldedFromRitualEvent())
                return true;
        }
        return  false;
   }
    /**
     *Only For Test
     * @return pp of player
     */
   public int getPp()
   {
       return pp;
   }

    /**
     * Only For Test
     * @return food of player
     */
    public int getFood()
   {
       return food;
   }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(nickname, player.nickname) && color == player.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, pp, food, color, characterCards, buildingCards);
    }

    @Override
    public String toString() {
        String player = "Nickname: " + nickname +
                "\nPp: " + pp +
                "\nFood: " + food;
        if(!characterCards.isEmpty() || !buildingCards.isEmpty())
               player+= "\nCards: ";
        for(CharacterCard c: this.characterCards){
            player = player.concat(c.toString() +" ");
        }
        for(BuildingCard c: this.buildingCards){
            player = player.concat(c.toString() +" ");
        }
        return player;
    }

    public List<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    public List<BuildingCard> getBuildingCards() {
        return buildingCards;
    }
}