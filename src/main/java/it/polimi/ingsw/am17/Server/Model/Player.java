package it.polimi.ingsw.am17.Server.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class Player implements Serializable {
    private String nickname;
    private int pp;
    private int food;
    private Color color;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "JacksonTribeCardType",
            defaultImpl = CharacterCard.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Inventor.class, name = "inventor"),
            @JsonSubTypes.Type(value = Binder.class, name = "binder"),
            @JsonSubTypes.Type(value = Shaman.class, name = "shaman"),
            @JsonSubTypes.Type(value = Artist.class, name = "artist"),
            @JsonSubTypes.Type(value = Hunter.class, name = "hunter"),
            @JsonSubTypes.Type(value = Builder.class, name = "builder")
    })
    private List<CharacterCard> characterCards;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "JacksonBuildingCardType",
            defaultImpl = BuildingCard.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BuildingType1.class, name = "building1"),
            @JsonSubTypes.Type(value = BuildingType2.class, name = "building2"),
            @JsonSubTypes.Type(value = BuildingType3M.class, name = "building3M"),
            @JsonSubTypes.Type(value = BuildingType4.class, name = "building4"),
            @JsonSubTypes.Type(value = BuildingType5.class, name = "building5"),
            @JsonSubTypes.Type(value = BuildingType6.class, name = "building6"),
            @JsonSubTypes.Type(value = BuildingType7.class, name = "building7"),
            @JsonSubTypes.Type(value = BuildingType8.class, name = "building8"),
            @JsonSubTypes.Type(value = BuildingType9.class, name = "building9"),
            @JsonSubTypes.Type(value = BuildingType10.class, name = "building10"),
            @JsonSubTypes.Type(value = BuildingType11.class, name = "building11"),
            @JsonSubTypes.Type(value = BuildingType12.class, name = "building12"),
            @JsonSubTypes.Type(value = BuildingType13M.class, name = "building13M"),
            @JsonSubTypes.Type(value = BuildingType14.class, name = "building14")
    })
    private List<BuildingCard> buildingCards;

    private static final Logger logger = Logger.getLogger(Player.class.getName());


    /**
     * Used for (de)serialization.
     */
    @JsonCreator
    public Player(@JsonProperty("nickname") String nickname,
                  @JsonProperty("color") Color color,

                  @JsonProperty("characterCards")
                  @JsonTypeInfo(
                          use = JsonTypeInfo.Id.NAME,
                          include = JsonTypeInfo.As.PROPERTY,
                          property = "JacksonTribeCardType",
                          defaultImpl = CharacterCard.class)
                  @JsonSubTypes({
                          @JsonSubTypes.Type(value = Inventor.class, name = "inventor"),
                          @JsonSubTypes.Type(value = Binder.class, name = "binder"),
                          @JsonSubTypes.Type(value = Shaman.class, name = "shaman"),
                          @JsonSubTypes.Type(value = Artist.class, name = "artist"),
                          @JsonSubTypes.Type(value = Hunter.class, name = "hunter"),
                          @JsonSubTypes.Type(value = Builder.class, name = "builder")
                  })
                  List<CharacterCard> characterCards,

                  @JsonProperty("buildingCards")
                  @JsonTypeInfo(
                          use = JsonTypeInfo.Id.NAME,
                          include = JsonTypeInfo.As.PROPERTY,
                          property = "JacksonBuildingCardType",
                          defaultImpl = BuildingCard.class)
                  @JsonSubTypes({
                          @JsonSubTypes.Type(value = BuildingType1.class, name = "building1"),
                          @JsonSubTypes.Type(value = BuildingType2.class, name = "building2"),
                          @JsonSubTypes.Type(value = BuildingType3M.class, name = "building3M"),
                          @JsonSubTypes.Type(value = BuildingType4.class, name = "building4"),
                          @JsonSubTypes.Type(value = BuildingType5.class, name = "building5"),
                          @JsonSubTypes.Type(value = BuildingType6.class, name = "building6"),
                          @JsonSubTypes.Type(value = BuildingType7.class, name = "building7"),
                          @JsonSubTypes.Type(value = BuildingType8.class, name = "building8"),
                          @JsonSubTypes.Type(value = BuildingType9.class, name = "building9"),
                          @JsonSubTypes.Type(value = BuildingType10.class, name = "building10"),
                          @JsonSubTypes.Type(value = BuildingType11.class, name = "building11"),
                          @JsonSubTypes.Type(value = BuildingType12.class, name = "building12"),
                          @JsonSubTypes.Type(value = BuildingType13M.class, name = "building13M"),
                          @JsonSubTypes.Type(value = BuildingType14.class, name = "building14")
                  })
                  List<BuildingCard> buildingCards) {
        this.nickname = nickname;
        this.color = color;
        this.characterCards = characterCards != null ? characterCards : new ArrayList<>();
        this.buildingCards = buildingCards != null ? buildingCards : new ArrayList<>();
    }


    /**
     * Used for actual creation of player.
     */
    public Player(String nickname, Color color) {
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

    public List<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    public List<BuildingCard> getBuildingCards() {
        return buildingCards;
    }

    public void addPp(int quantity){
        this.pp+=quantity;
    }

    public void addFood(int quantity) {
        int newAmount = food + quantity;
        logger.info("Player ["+ this.getNickname() +"] initial food: " + food + ". New quantity: " + quantity + ". New food amount: " + newAmount);
        if (newAmount < 0) {
            throw new InvalidOperationException(ErrorType.INSUFFICIENT_FOOD);
        }
        this.food = newAmount;
        logger.info("Food added successfully. ");
    }

    public void calculateFinalPoints(){
        // add pp of builders
        int pointsBuilders = characterCards.stream()
                    .filter(g ->g.getCardType().equals(CardType.BUILDER))
                    .mapToInt(g -> ((Builder) g).getPointBonus())
                    .sum();

        addPp(pointsBuilders);
        logger.info("Player " + this.getNickname() + " has " + pointsBuilders + "  PP from builders at the end Game. ");

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
        logger.info("Player " + this.getNickname() + " has " + ((int) (inventorCount * uniqueIcons)) + " PP from inventors at the end Game. ");

        // add ten points for each artist couple
        int numArtists = (int) characterCards.stream()
                .filter(g ->g.getCardType().equals(CardType.ARTIST))
                .count();
        int numCouples = Math.floorDiv(numArtists,2);
        addPp(numCouples*10);
        logger.info("Player " + this.getNickname() + " has " + (numCouples*10) + " PP from each artist couple at the end Game. ");

        // points of buildings
        int cardPoints = buildingCards.stream()
                .mapToInt(BuildingCard::getBonusPoints)
                .sum();
        addPp(cardPoints);
        logger.info("Player " + this.getNickname() + " has " + (cardPoints) + " PP from buildings at the end Game. ");

        // final effects of buildings
        for (BuildingCard card : buildingCards) {
            this.addPp(card.GetAdditionalFinalPoints(characterCards));
        }
    }

    public void addCharacter(CharacterCard card) {
        //if player has buildingType14 or buildingType10 (and all conditions from building are met add food)
        int foodBonusFromBuildings = 0;
        for(BuildingCard buildingCard : buildingCards)
        {
            foodBonusFromBuildings += buildingCard.GetFoodBonusFromCardAcquisition(characterCards, card);
        }
        addFood(foodBonusFromBuildings);
        logger.info("Player " + this.getNickname() + " has received " + foodBonusFromBuildings +
                        " food from building 10 or 14. ");
        characterCards.add(card);
    }

    public void addBuilding(BuildingCard card) {
        buildingCards.add(card);
    }


    /**
     * add cards to player (from playerAction)
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
                logger.info("Player " + this.getNickname() + " has " + getNumberOfHunters() + " food from hunters with icon. ");
            }
        }

        if(!canBuyBuidings(buildingCards))
            throw new InvalidOperationException(ErrorType.INSUFFICIENT_FOOD_BUILDINGS);

        for (BuildingCard card : buildingCards){
            int cost = calculateBuildingCost(card);
            try {
                addFood(-cost);
            } catch (IllegalStateException e) {
                throw new InvalidOperationException(ErrorType.INSUFFICIENT_FOOD_BUILDINGS);
            }
            addBuilding(card);
            logger.info("Player " + this.getNickname() + " paid " + cost + " for building: " + card);
        }
    }

    public boolean canBuyBuidings(List<BuildingCard> buildingsToBuy)
    {
        int totalCost = 0;
        for (BuildingCard card : buildingsToBuy){
            int cost = calculateBuildingCost(card);
            totalCost+= cost;
        }
        return totalCost <= food;
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
       logger.info("Player " + this.getNickname() + " has " + foodDiscount + " food discount from buildings for FoodEvent. ");
       //count totalDiscount given by numBinder and foodDiscount
       int totalDiscount = Math.toIntExact((3*numBinder) + foodDiscount);
       logger.info("Player " + this.getNickname() + " has " + totalDiscount + " total food discount for FoodEvent. ");
       //count totalCards, witch are all the player cards
       int totalCards = this.characterCards.size();
       //find foodPrice, witch is what the player has to pay
       int foodPrice = totalCards - totalDiscount;
       logger.info("Player " + this.getNickname() + " has " + foodPrice + " foodPrice to pay in FoodEvent. ");
       //if foodPrice<0, the player doesn't lose pp nor food
       if(foodPrice<=0){
           addPp(0);
           addFood(0);
           logger.info("Player " + this.getNickname() + " doesn't have to pay. ");
       }//if food is not enough, player loses pp and all the food he has
       else if (food < foodPrice) {
           int remaining = foodPrice - food;

           int lostPp = pointLost * remaining;

           addPp(lostPp * (-1));
           addFood(food * (-1));
           logger.info("Player " + getNickname() + " has lost all food (" + food + ") and " + lostPp
                    + " points from FoodEvent.");
       } //if food is enough
       else {
           addFood(foodPrice * (-1));
           logger.info("Player " + getNickname() + " has lost " + foodPrice + " food from FoodEvent.");
       }
   }

   public void solveHuntingEvent(int pointEarned) {
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
       //find additional food and PP given by buildingCard
       for(BuildingCard c: this.buildingCards){
           totalFood += c.AddFoodPerHunterInHuntingEvent(this.characterCards);
           totalPP += c.AddPointPerHunterInHuntingEvent(this.characterCards);
       }
       //add food and points
       this.addFood(totalFood);
       this.addPp(totalPP);
       }
       logger.info("Player " + getNickname() + " has gained " + totalFood + " food and "
               + totalPP + " points from HuntingEvent.");
   }

   public void solvePaintingEvent(int numMax, int pointsMax, int pointsLow){
       //count number of artists
       int numArtist = (int) characterCards.stream()
               .filter(c-> c.getCardType().equals(CardType.ARTIST))
               .count();
       //assign PP based on number of artists
       if(numArtist>=numMax){
           addPp(numArtist * pointsMax);
           logger.info("Player " + getNickname() + " has gained " + numArtist*pointsMax
                   + " points for PaintingEvent.");
       }
       else {
           addPp(pointsLow*(-1));
           logger.info("Player " + getNickname() + " has lost " + pointsLow
                   + " points for PaintingEvent.");
       }

       int additionalFood=0;


       //find additional food given by buildingCard
       for(BuildingCard c: buildingCards){
           additionalFood += c.AddFoodPerArtistInPaintingEvent(this.characterCards);
       }
       //add additionalFood
       addFood(additionalFood);
       logger.info("Player " + getNickname() + " had gained " + additionalFood
               + " food from buildings for PaintingEvent.");
   }

   public int calculateStarPoints() {
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
        String playerString = "Nickname: " + nickname +
                "\nPp: " + pp +
                "\nFood: " + food;
        if (!characterCards.isEmpty()) playerString+= "\nCharacter cards: " + playerCharacterCardstoString(17);
        if (!buildingCards.isEmpty()) playerString+= "Building cards: " + playerBuildingCardsString(17);
        return playerString;
    }

    /**
     * Builds a string containing the character cards of the player
     * @return  the string with character cards of the player
     */
    public String playerCharacterCardstoString(int startSpace) {
        StringBuilder sb = new StringBuilder();
        if (!characterCards.isEmpty()) {
            // map of character types and character cards of the player
            // key: character type
            // value: list of cards of the key type
            Map<CardType, List<CharacterCard>> groupCharacters = characterCards.stream()
                    .collect(Collectors.groupingBy(TribesCard::getCardType));

            // map with maximum widths of the columns
            Map<CardType, Integer> columnWidths = columnLength();

            // calculate the number of rows to append
            int maxRows = groupCharacters.values().stream().mapToInt(List::size).max().orElse(0);

            // initial span
            if (startSpace == 15) sb.repeat(" ", startSpace - 4);
            else sb.repeat(" ", startSpace - 17);
            // append cards
            for (int i = 0; i < maxRows; i++) {
                if (i>0) sb.append("\n").repeat(" ", startSpace);
                for (CardType type : groupCharacters.keySet()) {
                    List<CharacterCard> columnCards = groupCharacters.get(type);
                    // if cards are more than the current row index append one of them
                    // append blank otherwise
                    String card = (i < columnCards.size()) ? columnCards.get(i).toString() : "";
                    sb.append(String.format("%-" + columnWidths.get(type) + "s", card));
                }

            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Builds a string containing the building cards of the player
     * @return  the string with building cards of the player
     */
    public String playerBuildingCardsString(int startSpace) {
        if (buildingCards.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (startSpace == 17) {
            // if first row add only one space
            sb.append(" ");
        }
        else {
            sb.repeat(" ", startSpace);
        }
        for (int i = 0; i < buildingCards.size(); i++) {
            sb.append(buildingCards.get(i).toString());
            if (i < buildingCards.size() - 1) {
                sb.append("\n").repeat(" ", startSpace);
            }
        }
        return sb.toString();
    }

    /**
     * @return  a map with the maximum card string length for each character type.
     */
    private Map<CardType, Integer> columnLength() {
        Map<CardType, Integer> columnMaxLength = new HashMap<>();
        // space between columns
        int fixedGap = 4;
        // artist: 8
        columnMaxLength.put(CardType.ARTIST, 8 + fixedGap);
        // hunter: 9
        columnMaxLength.put(CardType.HUNTER, 9 + fixedGap);
        // shaman: 11
        columnMaxLength.put(CardType.SHAMAN, 11 + fixedGap);
        // inventor: 20
        columnMaxLength.put(CardType.INVENTOR, 20 + fixedGap);
        // builder: 19
        columnMaxLength.put(CardType.BUILDER, 19 + fixedGap);
        // binder: 8
        columnMaxLength.put(CardType.BINDER, 8  + fixedGap);
        return columnMaxLength;
    }

}