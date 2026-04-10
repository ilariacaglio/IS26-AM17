package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.am17.Utility.CardParser.loadOfferingCards;

public class Game extends Subject {
    private final int id;
    private final int numPlayers;
    private int currentEra;

    private final List<Player> players;
    private Stack<Player> orderedPlayer;

    private final List<OfferingCard> offeringCards;

    private final TribesDeck tribesDeck;
    private List<TribesCard> upperRow;
    private List<TribesCard> lowerRow;

    private final BuildingDeck buildingDeck;

    private List<BuildingCard> upperBuildingRow;
    private List<BuildingCard> lowerBuildingRow;

    private final OfferingCard building2OfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
    private final int[] turnFoodPoints;

    public Game(int id, int numPlayers) {
        this.id = id;
        this.numPlayers = numPlayers;
        currentEra = 0;
        players = new ArrayList<>(numPlayers);
        offeringCards = loadOfferingCards(numPlayers);

        tribesDeck = new TribesDeck(numPlayers);
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow = new ArrayList<>();

        turnFoodPoints = getTurnFoodPoints();
    }

    private int[] getTurnFoodPoints() {
        return switch (numPlayers) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{2, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            case 5 -> new int[]{3, 1, 0, 0, -1};
            default -> throw new IllegalStateException("Wrong number of players");
        };
    }

    boolean isStarted() {
        return currentEra != 0;
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    private OfferingCard getNextOccupiedOfferingCard() {
        OfferingCard offCard =  offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);
        if(offCard != null) {
            return offCard;
        }
        else if(building2OfferingCard.getPlayer() == null){
            return null;
        }
        else{
            return building2OfferingCard;
        }
    }

    /**
     * @return leftmost player in the offering track.
     */
    private Player getNextPlayer() {
        return getNextOccupiedOfferingCard().getPlayer();
    }

    /**
     * Adds a player to the game.
     *
     * @param p player to add to the game
     */
    public void addPlayer(Player p) {
        if (isStarted()) {
            throw new IllegalStateException("The game has already started.");
        }
        if (players.size() >= numPlayers) {
            throw new IllegalStateException("The game lobby is full (max " + numPlayers + " players).");
        }
        if (players.contains(p)) {
            throw new IllegalArgumentException("This player is already in the lobby.");
        }

        players.add(p);

        //if we reached the number of players for the game we start the game
        if(players.size() == numPlayers)
            nextEra();
    }

    /**
     * Goes to the next era.
     * If the game is not started, it starts.
     * Notifies the observers.
     */
    private void nextEra() {
        switch (currentEra) {
            case 0:
                era1();
                break;
            case 1:
                era2();
                break;
            case 2:
                era3();
                break;
            default:
                throw new IllegalStateException("Invalid era");
        }

        // TODO: notifyObserver(GameState gameState);
    }

    private void moveDownBuildingCards() {
        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
    }

    private void initStack(){
        // Init players order stack
        orderedPlayer = new Stack<>();
        for (Player p : players) {
            orderedPlayer.push(p);
        }
    }

    private void giveFoodToPlayers(){
        int[] startingFood = {2, 3, 3, 4, 4};
        for (int i = 0; i < numPlayers && i < startingFood.length; i++) {
            players.get(i).addFood(startingFood[i]);
        }
    }

    /**
     * Starts the game by entering the first era.
     */
    private void era1() {
        if (isStarted()) {
            throw new IllegalStateException("The game has already started.");
        }

        // Set era and shuffle players
        this.currentEra = 1;
        Collections.shuffle(players); // TODO: check if already shuffled by controller

        initStack();

        giveFoodToPlayers();

        // Populate the rows
        int targetLowerRowSize = numPlayers + 1;
        int targetUpperRowSize = numPlayers + 4;

        while (lowerRow.size() < targetLowerRowSize) {
            TribesCard drawnCard = tribesDeck.Draw();

            if (drawnCard.getCardType().isCharacter()) {
                lowerRow.add(drawnCard);
            } else if (upperRow.size()< targetUpperRowSize) {
                upperRow.add(drawnCard);
            }
        }

        while (upperRow.size() < targetUpperRowSize) {
            upperRow.add(tribesDeck.Draw());
        }

        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra1());
    }

    /**
     * Sets up the second era.
     */
    private void era2() {
        currentEra = 2;
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra2());
    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        currentEra = 3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra3());
    }

    private void turnOrderFoodBonus(){
        int i=0;
        for (Player p : orderedPlayer) {
            //check if turnFood > 0
            if(turnFoodPoints[i]<0){
                //if not check if player can pay the food (food price is not higher than 1)
                if( p.getFood()<1)
                    p.addPp(-2);
                else
                    p.addFood(turnFoodPoints[i]);
            }
            else{
                //check if player has food bonus from buildings
                int foodFromBuilding = p.addFoodToTurnFood();
                p.addFood(turnFoodPoints[i] + foodFromBuilding);
            }
            i++;
        }
    }

    /**
     * Ends the current round by solving events.
     */
     void endRound() {
        turnOrderFoodBonus();
        // Get events from the lower row.
        List<EventCard> events = lowerRow.stream()
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // 1. Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );

        // 2. 3. 4. Reorganize cards.
        lowerRow = new ArrayList<>(upperRow);
        upperRow.clear();
        for (int i = 0; i < numPlayers + 4; i++) {
            try {
                TribesCard c = tribesDeck.Draw();
                if (c.getEra() != currentEra) nextEra();
                upperRow.add(c);
            }
            // If the deck is empty, end the game. N.B. This is how we decided to handle game ending.
            catch (IllegalStateException e) {
                if (e.getMessage().equals("No more cards left in the deck.")) {
                    endGame();
                }
            }
        }

        // remove player from buildingType2 offering card
         building2OfferingCard.setPlayer(null);

        // TODO: notifyObserver(GameState gameState);
    }

    /**
     * Ends the game.
     */
    private void endGame() {
        this.currentEra = -1; //put era to -1 to signal game has ended
        // Solve events
        // Get all events from both rows. N.B. we solve the food events from BOTH rows at the end.
        List<EventCard> events = Stream.concat(lowerRow.stream(), upperRow.stream())
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );

        for (Player player : players) {
            player.calculateFinalPoints();
        }

        // TODO: notifyObserver(GameState gameState);
    }

    private void validateCardChoice(int numUpper, int numLower, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        List<CharacterCard> upperRowCharacterCards = upperRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList();
        List<CharacterCard> lowerRowCharacterCards = lowerRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList();


        for (CharacterCard card : characterCards) {
            if (upperRowCharacterCards.contains(card)) {
                numUpper--;
            } else if (lowerRowCharacterCards.contains(card)) {
                numLower--;
            } else {
                throw new IllegalStateException("Illegal character selection. (Card not in any row)");
            }
        }
        for (BuildingCard card : buildingCards) {
            if (upperBuildingRow.contains(card)) {
                numUpper--;
            } else if (lowerBuildingRow.contains(card)) {
                numLower--;
            } else {
                throw new IllegalStateException("Illegal building selection. (Card not in any row)");
            }
        }
        if (numUpper != 0 || numLower != 0) {
            throw new IllegalStateException("Illegal card selection. (Wrong number of cards)");
        }

    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     * @param player player that chose the offering card
     * @param offeringCard offering card picked
     */
    public void selectOfferingCard(Player player, OfferingCard offeringCard)
    {
        //check if is player turn
        if(!player.equals(orderedPlayer.pop())) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        //check card is free
        if(offeringCard == null || offeringCard.getPlayer() != null){
            throw new IllegalStateException("The offering card was already selected");
        }

        //check if offeringCard is valid
        if(!offeringCards.contains(offeringCard) && !offeringCard.equals(building2OfferingCard)) {
            throw new IllegalStateException("Illegal card selection. (Card not in any offering)");
        }

        //set player to offeringCard
        offeringCard.setPlayer(player);

        Player nextPlayer;
        //notify observer
        try {
            nextPlayer = orderedPlayer.peek();
        }catch (EmptyStackException e)
        {
            //order player stack for next turn
            orderedPlayer = offeringCards.stream()
                    .filter(card -> card.getPlayer() != null)
                    .sorted(Comparator.comparing(OfferingCard::getOrderLetter).reversed())
                    .map(OfferingCard::getPlayer)
                    .collect(Collectors.toCollection(Stack::new));


            //get first player to do player action
            nextPlayer = getNextPlayer();

            //gamestate should specify it s turn for player action
            //GameState = new GameState ...
            //notifyObserver(gameState)
            return;
        }

        //GameState = new GameState ...
        //notifyObserver(gameState)
    }

    /**
     * Emulates a player action (picking cards).
     * TODO: FoodBonusFromTurnOrder
     * @param player
     * @param characterCards TODO: check null
     * @param buildingCards TODO: check null
     */
    public void playerAction(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        // Get leftmost occupied offering card.
        OfferingCard currentOffering = getNextOccupiedOfferingCard();

        // Check if the player is current next player
        if (player != currentOffering.getPlayer()) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        // check if cards selection is legal based on the offeringCard
        int numUpper = currentOffering.getNumCardsUpper();
        int numLower = currentOffering.getNumCardsLower();
        validateCardChoice(numUpper, numLower, characterCards, buildingCards);

        // selection legal: obtain cards
        try {
            player.addCards(characterCards, buildingCards);
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Not enough food to buy building cards")) {
                throw new IllegalStateException("Not enough food to buy building cards");
            }
            else {
                throw new IllegalStateException("unknown error");
            }
        }
        upperBuildingRow.removeAll(buildingCards); // if not present, no worries
        lowerBuildingRow.removeAll(buildingCards); // if not present, no worries
        upperRow.removeAll(characterCards); // if not present, no worries
        lowerRow.removeAll(characterCards); // if not present, no worries

        //if the player has the buildingType2 card set it to offering card
        if(building2OfferingCard.getPlayer() == null) {
            addPlayerToBT2OfferingCard(player);
        }

        currentOffering.setPlayer(null);

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard();

        //check everybody played his base turn
        if(nextOfferingCard == null)
        {
            // the round has ended
            endRound();
        }

        //GamseState = new GameState
        //notifyObserver
    }


    /**
     * Checks if the player has the BuildingType2 card and sets it to buildingType2OfferingCard
     * @param player the player to be set
     */
    private void addPlayerToBT2OfferingCard(Player player) {
        //Important: there is a singular buildingType2 per game
        if(player.hasBuilding2()) {
            building2OfferingCard.setPlayer(player);
        }
    }

    /// only for testing
    protected List<Player> getPlayers(){
        return players;
    }
    /// only for testing
    protected int getCurrentEra(){
        return currentEra;
    }
    /// only for testing
    protected List<TribesCard> getUpperRow(){
        return upperRow;
    }
    ///only for testing
    protected List<BuildingCard> getUpperBuildingRow(){
        return upperBuildingRow;
    }
    ///only for testing
    protected List<BuildingCard> getLowerBuildingRow(){
        return lowerBuildingRow;
    }
    ///only for testing
    protected List<TribesCard> getLowerRow(){
        return lowerRow;
    }
    ///only for testing
    protected List<OfferingCard> getOfferingCards(){
        return offeringCards;
    }
    ///only for testing
    protected Player getCurrentPlayer(){
        return orderedPlayer.peek();
    }

    @Override
    public void attach(Observer observer) {

    }


    @Override
    public void detach(Observer observer) {

    }


    @Override
    public void notifyObserver() {

    }
}
