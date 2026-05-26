package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.CommonInterfaces.ColorException;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Utility.DatabaseManager;

import it.polimi.ingsw.am17.Server.Utility.MoveValidator;
import it.polimi.ingsw.am17.Server.Utility.TurnFoodHandler;

import static it.polimi.ingsw.am17.Server.Utility.CardParser.loadOfferingCards;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Game extends Subject {
    private final UUID id;
    private final int numPlayers;
    private GameState gameState;

    private final Queue<Player> orderedPlayers;

    private final List<OfferingCard> offeringCards;

    private final TribesDeck tribesDeck;
    private final List<TribesCard> upperRow;
    private List<TribesCard> lowerRow;

    private final BuildingDeck buildingDeck;

    private List<BuildingCard> upperBuildingRow;
    private List<BuildingCard> lowerBuildingRow;

    private final OfferingCard building2OfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
    private final int[] turnFoodPoints;

    private static final Logger logger = Logger.getLogger(Game.class.getName());

    public Game(UUID id, int numPlayers) {
        logger.info("Instantiated new game for " + numPlayers + " players with id: " + id);

        this.id = id;

        checkNumPlayers(numPlayers);

        this.numPlayers = numPlayers;
        gameState = GameState.LOBBY;
        orderedPlayers = new LinkedList<>();
        offeringCards = loadOfferingCards(numPlayers);

        tribesDeck = new TribesDeck(numPlayers);
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow = new ArrayList<>();

        turnFoodPoints = TurnFoodHandler.getTurnFoodPoints(numPlayers);
    }

    private void checkNumPlayers(int numPlayers) {
        logger.fine("Checking number of players: " + numPlayers);

        if (numPlayers < 2 || numPlayers > 5) {
            logger.warning("Invalid number of players: " + numPlayers);
            throw new InvalidOperationException(ErrorType.INVALID_NUMBER_OF_PLAYERS);
        }
    }

    public boolean isStarted() {
        logger.fine("Checking if game is started.");
        return gameState.isGameStarted();
    }

    public boolean isEnded() {
        return gameState.isGameEnded();
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    private OfferingCard getNextOccupiedOfferingCard() {
        logger.fine("Getting next occupied offering card.");

        // lookup in normal offering cards
        OfferingCard offeringCard = offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);

        // if not found, lookup in building2OfferingCard
        if (offeringCard == null && building2OfferingCard.getPlayer() != null) offeringCard = building2OfferingCard;

        return offeringCard;
    }

    /**
     * Adds a player to the game.
     *
     * @param p player to add to the game
     */
    public void addPlayer(Player p) {
        logger.info("Adding player " + p.getNickname() + " to game with id " + id);

        if (isStarted()) {
            logger.warning("in Game, in addPlayer(), problem in if(isStarted())");
            throw new InvalidOperationException(ErrorType.GAME_ALREADY_STARTED);
        }
        if (orderedPlayers.stream().anyMatch(player -> player.getNickname().equals(p.getNickname()))) {
           logger.warning("The nickname " + p.getNickname() + " is not available.");
            throw new InvalidOperationException(ErrorType.DUPLICATE_NICKNAME);
        }
        if (orderedPlayers.stream().anyMatch(player -> player.getColor().equals(p.getColor()))) {
            logger.warning("The color " + p.getColor() + " is not available");
            String message = "The game has already a player with the same color. Unused colors: ";
            //Get All colors
            List<Color> unusedColors = new ArrayList<>(EnumSet.allOf(Color.class).stream().toList());
            //Remove the colors that are currently in use
            orderedPlayers.forEach(player -> unusedColors.remove(player.getColor()));
            throw new ColorException(ErrorType.DUPLICATE_COLOR,unusedColors);
        }

        orderedPlayers.add(p);
        notifyPlayerQueue(orderedPlayers);

        //if we reached the number of players for the game we start the game
        if (orderedPlayers.size() == numPlayers)
            nextEra();
    }

    /**
     * Removes a player from the game AND ENDS THE GAME (re-join not implemented).
     * @param nickname  the nickname of the player that closes the game
     */
    public void forceEndGame(String nickname) {
        logger.severe("Forcibly closing game with id: " + id);
        this.gameState = GameState.ENDED;
        notifyEndGame(nickname, null, null); // no ranking and ... when game closed early
    }

    /**
     * Goes to the next era.
     * If the game is not started, it starts.
     * Notifies the observers.
     */
    private void nextEra() {
        logger.info("Going to next era.");

        switch (gameState) {
            case GameState.LOBBY:
                era1();
                break;
            case GameState.ERA1:
                era2();
                notifyGameState(gameState);
                break;
            case GameState.ERA2:
                era3();
                notifyGameState(gameState);
                break;
            default:
                throw  new InvalidOperationException(ErrorType.INVALID_GAME_STATE);
        }
    }

    private void moveDownBuildingCards() {
        logger.fine("Moving down building cards.");

        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
    }

    /**
     * Gives the starting food to players
     */
    private void giveFoodToPlayers() {
        logger.fine("Giving starting food to players.");

        int[] startingFood = {2, 3, 3, 4, 4};
        int i = 0;
        for (Player player : orderedPlayers) {
            player.addFood(startingFood[i]);
            logger.info("Player " + player.getNickname() + " was given "
                    + player.getFood() + " beginning food.");
            i++;
        }
    }

    private void shuffleQueue() {
        List<Player> players = new ArrayList<>(orderedPlayers);
        Collections.shuffle(players);
        orderedPlayers.clear();
        orderedPlayers.addAll(players);
    }

    /**
     * Starts the game by entering the first era.
     */
    private void era1() {
        logger.info("Starting game (switching to era 1).");

        if (isStarted()) {
            throw new InvalidOperationException(ErrorType.GAME_ALREADY_STARTED);
        }

        // Set era and shuffle players
        this.gameState = GameState.ERA1;
        shuffleQueue();

        giveFoodToPlayers();

        // Populate the rows
        int targetLowerRowSize = numPlayers + 1;
        int targetUpperRowSize = numPlayers + 4;

        while (lowerRow.size() < targetLowerRowSize) {
            TribesCard drawnCard = tribesDeck.Draw();

            if (drawnCard.getCardType().isCharacter()) {
                lowerRow.add(drawnCard);
            } else if (upperRow.size() < targetUpperRowSize) {
                upperRow.add(drawnCard);
            }
        }

        while (upperRow.size() < targetUpperRowSize) {
            upperRow.add(tribesDeck.Draw());
        }

        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra1());

        notifyStartGame(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    /**
     * Sets up the second era.
     */
    private void era2() {
        logger.info("Switching to era 2.");

        gameState = GameState.ERA2;
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra2());
    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        logger.info("Switching to era 3.");

        gameState = GameState.ERA3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra3());
    }

    private void turnOrderFoodBonus() {
        logger.fine("Calculating turn order food bonus.");

        int i = 0;
        for (Player p : orderedPlayers) {
            //check if turnFood > 0
            if (turnFoodPoints[i] < 0) {
                //if not, check if player can pay the food (food price is not higher than 1)
                if (p.getFood() < 1)
                    p.addPp(-2);
                else
                    p.addFood(turnFoodPoints[i]);
                logger.info("Player " + p.getNickname() + " has " +  p.getFood() + " food and "
                        + p.getPp() + " points end round.");
            } else {
                //check if player has food bonus from buildings
                int foodFromBuilding = p.addFoodToTurnFood();
                p.addFood(turnFoodPoints[i] + foodFromBuilding);
                logger.info("Player " + p.getNickname() + " has " +  p.getFood() + " food and "
                        + p.getPp() + " points end round.");
            }
            i++;
        }
    }

    /**
     * Ends the current round by solving events.
     */
    // this method is public only for testing reasons
    public void endRound() {
        logger.info("Ending round.");

        turnOrderFoodBonus();
        // Get events from the lower row.
        List<EventCard> events = lowerRow.stream()
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // 1. Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );

        // 2. 3. 4. Reorganize cards.
        lowerRow = new ArrayList<>(upperRow);
        upperRow.clear();
        for (int i = 0; i < numPlayers + 4; i++) {
            try {
                TribesCard c = tribesDeck.Draw();
                if (c.getEra() != gameState) nextEra();
                upperRow.add(c);
            }
            catch (InvalidOperationException e) {
                // If the deck is empty, end the game. N.B. This is how we decided to handle game ending.
                if (e.getErrorType() == ErrorType.EMPTY_DECK)
                    endGame();
                return;
            }
            catch(Exception unknownEx) {
                throw new InvalidOperationException(ErrorType.UNKNOWN);
            }
        }

        notifyEndTurn(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    /**
     * Ends the game.
     */
    private void endGame() {
        logger.info("Ending game.");

        //put era to -1 to signal game has ended
        this.gameState = GameState.ENDED;
        // Solve events
        // Get all events from both rows. N.B. we solve the food events from BOTH rows at the end.
        List<EventCard> events = Stream.concat(lowerRow.stream(), upperRow.stream())
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );

        for (Player player : orderedPlayers) {
            player.calculateFinalPoints();
        }

        // insert data into db
        DatabaseManager.insertGameData(this);

        notifyEndGame(null, DatabaseManager.getRanking(this.numPlayers), orderedPlayers);
    }

    /**
     * Validates card selection for the player action "pickTribeCards".
     *
     * @param numToSelectFromUpper Number of cards that must be selected (if possible!) from the upper row.
     * @param numToSelectFromLower Number of cards that must be selected (if possible!) from the lower row.
     * @param characterCards       Selected character cards.
     * @param buildingCards        Selected building cards.
     */
    private void validateCardChoice(int numToSelectFromUpper, int numToSelectFromLower, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception{
        logger.fine("Validating card selection.");

        Exception e = MoveValidator.validateCardChoice(numToSelectFromUpper, numToSelectFromLower,
                characterCards, buildingCards, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
        if(e != null)
            throw e;
    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     *
     * @param nickname              nickname of the player
     * @param offeringCardLetter    offering card picked
     */
    public void selectOfferingCard(String nickname, Character offeringCardLetter) {
        logger.info("Request forwarded to selectOfferingCard method in model");
        // check if letter is null
        if(offeringCardLetter == null)
            throw new InvalidOperationException(ErrorType.MISSING_OFFERING_CARD_LETTER);

        // get player from nickname
        Player player = orderedPlayers.stream().filter(p->nickname.equals(p.getNickname()))
                .findFirst().orElseThrow(() -> new InvalidOperationException(ErrorType.INVALID_PLAYER));

        //check if is player turn
        if (!player.equals(orderedPlayers.peek()))
            throw new InvalidOperationException(ErrorType.OUT_OF_TURN);


        // check if player is not in an Offering Card already
        if (offeringCards.stream().anyMatch(card -> card.getPlayer() != null && card.getPlayer().equals(player)))
            throw new InvalidOperationException(ErrorType.OFFERING_CARD_ALREADY_SELECTED);


        // get offering card from letter
        OfferingCard offeringCard = offeringCards.stream()
                .filter(c -> offeringCardLetter.equals(c.getOrderLetter()))
                .findFirst().orElse(null);

        //check if offeringCard is valid
        if (offeringCard == null) throw new InvalidOperationException(ErrorType.INVALID_OFFERING_CARD_LETTER);


        logger.info("Player " + nickname + " wants offering card " + offeringCardLetter);

        // check if offering card is free
        if(offeringCard.getPlayer() != null)
            throw new InvalidOperationException(ErrorType.OFFERING_CARD_ALREADY_SELECTED);

        //set player to offeringCard
        offeringCard.setPlayer(player);

        // dequeue and enqueue the player in last position
        movePlayerInQueue();

        // when all player have an offering card recalculate queue
        if (allPlayersPickedOfferingCards())
            recalculatePlayerQueue();

        // notify changes
        notifyPlayerQueue(orderedPlayers);
        notifyPlayerSelectOfferingCard(player, offeringCard);
    }

    /**
     * Replaces player queue with new order from offering cards
     */
    private void recalculatePlayerQueue() {
        // get players from offering cards
        List <Player> playersList = offeringCards.stream()
                .filter(card -> card.getPlayer()!=null)
                .sorted(Comparator.comparing(OfferingCard::getOrderLetter))
                .map(OfferingCard::getPlayer)
                .toList();
        // insert them in queue
        orderedPlayers.clear();
        orderedPlayers.addAll(playersList);
    }

    /**
     * Checks that every player is set into an offering card
     * @return true if they are, false otherwise
     */
    private Boolean allPlayersPickedOfferingCards() {
        return orderedPlayers.stream().allMatch(player ->
                offeringCards.stream().anyMatch(card -> card.getPlayer() != null && card.getPlayer().equals(player))
        );
    }

    /**
     * Emulates a player action (picking cards).
     * @param nickname          the nickname of the player that has picked the cards
     * @param characterCards    the character cards picked by the player
     * @param buildingCards     the building cards picked by the player
     */
    public void pickTribeCards(String nickname, List<CharacterCard> characterCards, List<BuildingCard> buildingCards)  {
        // get player from nickname
        Player player =  orderedPlayers.stream()
                .filter(p->nickname.equals(p.getNickname()))
                .findFirst().orElseThrow(()->new InvalidOperationException(ErrorType.INVALID_PLAYER));

        // Get leftmost occupied offering card.
        OfferingCard currentOffering = getNextOccupiedOfferingCard();

        logger.info("Player " + player.getNickname() + " wants to pick tribe cards from offering card " +  currentOffering.getOrderLetter() + ": " + characterCards + " and " + buildingCards);


        // if a player has selected the offering card with letter A
        if (currentOffering.getOrderLetter()=='A') {
            handleOfferingCardWithLetterA(currentOffering);
            // recalculate next occupied offering card
            currentOffering = getNextOccupiedOfferingCard();
        }

        // Check if the player is current next player
        if (!player.equals(currentOffering.getPlayer()))
            throw new IllegalStateException(ErrorType.OUT_OF_TURN.toString());

        // check if cards selection is legal based on the offeringCard
        int numUpper = currentOffering.getNumCardsUpper();
        int numLower = currentOffering.getNumCardsLower();
        try {
            validateCardChoice(numUpper, numLower, characterCards, buildingCards);
        } catch (InvalidOperationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new InvalidOperationException(e.getMessage());
        }

        // selection legal: obtain cards
        try {
            player.addCards(characterCards, buildingCards);
        } catch (InvalidOperationException e) {
            if (e.getErrorType()==ErrorType.INSUFFICIENT_FOOD_BUILDINGS) {
                throw e;
            } else {
                throw new InvalidOperationException(ErrorType.UNKNOWN);
            }
        }
        upperBuildingRow.removeAll(buildingCards); // if not present, no worries
        lowerBuildingRow.removeAll(buildingCards); // if not present, no worries
        upperRow.removeAll(characterCards); // if not present, no worries
        lowerRow.removeAll(characterCards); // if not present, no worries

        if (player.hasBuilding2()) {
            // N.B.: there is a singular buildingType2 per game
            logger.fine("Adding player to buildingType2 offering card.");

            // N.B.: if calling from building2OfferingCard, don't set the player again
            if (building2OfferingCard.getPlayer() == null) building2OfferingCard.setPlayer(player);
        }

        currentOffering.setPlayer(null);
        movePlayerInQueue();

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard();

        notifyPlayerSelectTribesCard(player, characterCards, buildingCards);
        //check everybody played his base turn
        if (nextOfferingCard == null) {
            // the round has ended
            endRound();
        }
    }

    /**
     * Adds food to player in the offering card with letter A, then sets player to null
     * @param offeringCard reference to offering card with letter A
     */
    private void handleOfferingCardWithLetterA(OfferingCard offeringCard) {
        logger.fine("Handling offering card with letter A.");

        // give +3 food to the player
        offeringCard.getPlayer().addFood(3);
        // remove player from offering card
        offeringCard.setPlayer(null);
        // move the player to last position in queue
        movePlayerInQueue();
    }

    /**
     * Dequeues and enqueues the player to allow other players to play.
     */
    private void movePlayerInQueue() {
        // remove player from queue
        Player lastPlayer = orderedPlayers.poll();
        // add player as last element of queue
        orderedPlayers.add(lastPlayer);
    }

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayersList() {
        return orderedPlayers.stream().toList();
    }

    /// only for testing
    public GameState getGameState() {
        return gameState;
    }

    /// only for testing
    public List<TribesCard> getUpperRow() {
        return upperRow;
    }

    /// only for testing
    public List<BuildingCard> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    /// only for testing
    public List<BuildingCard> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    /// only for testing
    public List<TribesCard> getLowerRow() {
        return lowerRow;
    }

    /// only for testing
    public List<OfferingCard> getOfferingCards() {
        return offeringCards;
    }

    /// only for testing
    public Player getCurrentPlayer() {
        return orderedPlayers.peek();
    }
}
