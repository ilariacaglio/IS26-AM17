package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.CommonInterfaces.ColorException;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.SharedModelLogic;
import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Utility.DatabaseManager;

import static it.polimi.ingsw.am17.CommonInterfaces.SharedModelLogic.*;
import static it.polimi.ingsw.am17.Server.Utility.CardParser.loadOfferingCards;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Game extends Subject implements ModelInterface {
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

        turnFoodPoints = getTurnFoodPoints(numPlayers);
    }

    private void checkNumPlayers(int numPlayers) {
        logger.info("Checking number of players: " + numPlayers);

        if (numPlayers < 2 || numPlayers > 5) {
            logger.warning("Invalid number of players: " + numPlayers);
            throw new InvalidOperationException(ErrorType.INVALID_NUMBER_OF_PLAYERS);
        }
    }

    public boolean isStarted() {
        logger.info("Checking if game is started.");
        return gameState.isGameStarted();
    }

    /**
     * Adds a player to the game.
     *
     * @param p player to add to the game
     */
    @Override
    public void addPlayer(Player p) {
        logger.info("Adding player " + p.getNickname() + " to game with id " + id);

        if (isStarted()) {
            logger.warning("Impossible adding " + p.getNickname() + ": the Game is already started (isStarted = true).");
            throw new InvalidOperationException(ErrorType.GAME_ALREADY_STARTED);
        }
        if (orderedPlayers.stream().anyMatch(player -> player.getNickname().equals(p.getNickname()))) {
           logger.warning("The nickname " + p.getNickname() + " is not available.");
            throw new InvalidOperationException(ErrorType.DUPLICATE_NICKNAME);
        }
        if (orderedPlayers.stream().anyMatch(player -> player.getColor().equals(p.getColor()))) {
            //Get All colors
            List<Color> unusedColors = new ArrayList<>(EnumSet.allOf(Color.class).stream().toList());
            //Remove the colors that are currently in use
            orderedPlayers.forEach(player -> unusedColors.remove(player.getColor()));
            logger.warning("The color " + p.getColor() + " is not available");
            throw new ColorException(ErrorType.DUPLICATE_COLOR,unusedColors);
        }

        orderedPlayers.add(p);
        logger.info(p.getNickname() + " was added successfully");
        notifyPlayerQueue(orderedPlayers);

        //if we reached the number of players for the game we start the game
        if (orderedPlayers.size() == numPlayers){
            logger.info("Game starts");
            nextEra();
        }

    }

    /**
     * Removes a player from the game AND ENDS THE GAME (re-join not implemented).
     * @param nickname  the nickname of the player that closes the game
     */
    @Override
    public void forceEndGame(String nickname) {
        logger.severe("Forcibly closing game with id: " + id);
        this.gameState = GameState.ENDED;
        notifyForceEndGame(nickname);
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
                throw new InvalidOperationException(ErrorType.INVALID_GAME_STATE);
        }
    }

    private void moveDownBuildingCards() {
        logger.info("Moving down building cards.");

        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
    }

    /**
     * Gives the starting food to players
     */
    private void giveFoodToPlayers() {
        logger.info("Giving starting food to players.");

        int[] startingFood = {2, 3, 3, 4, 4};
        int i = 0;
        for (Player player : orderedPlayers) {
            player.addFood(startingFood[i]);
            logger.info("Player " + player.getNickname() + " was given "
                    + player.getFood() + " beginning food.");
            i++;
        }
    }

    /**
     * Shuffles player queues to create casual order
     */
    private void shuffleQueue() {
        List<Player> players = new ArrayList<>(orderedPlayers);
        Collections.shuffle(players);
        orderedPlayers.clear();
        orderedPlayers.addAll(players);
        logger.info("Player queue shuffled. Total players in queue: " + orderedPlayers.size());
    }

    /**
     * Starts the game by entering the first era.
     */
    private void era1() {
        logger.info("Starting game (switching to era 1).");

        if (isStarted()) {
            logger.warning("Game is already started, gameState is already in era1");
            throw new InvalidOperationException(ErrorType.GAME_ALREADY_STARTED);
        }

        // Set era and shuffle players
        this.gameState = GameState.ERA1;
        logger.info("gameState is set at era1");
        shuffleQueue();

        giveFoodToPlayers();
        logger.info("Starting food was given to the players");

        // Populate the rows
        int targetLowerRowSize = numPlayers + 1;
        logger.info("Size of lowerRow is supposed to be numPlayers+1: " + (numPlayers+1));
        int targetUpperRowSize = numPlayers + 4;
        logger.info("Size of upperRow is supposed to be numPlayers+4: " + (numPlayers+4));

        while (lowerRow.size() < targetLowerRowSize) {
            logger.info("Lower row setup loop - Current lower size: " + lowerRow.size() + "/" + targetLowerRowSize);
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
        logger.info("Rows populated successfully. Triggering notifyStartGame.");

        notifyStartGame(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }
    /**
     * Sets up the second era.
     */
    private void era2() {
        logger.info("Switching to era 2.");

        gameState = GameState.ERA2;
        moveDownBuildingCards();
        logger.info("BuildingCards era 1 have been moved down");
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra2());
        logger.info("BuildingCards era 2 have been added to the upperRow");
        logger.info("Era 2 setup completed successfully. Current gameState: " + gameState);
    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        logger.info("Switching to era 3.");

        gameState = GameState.ERA3;
        lowerBuildingRow.clear();
        logger.info("BuildingCards era 1 have been removed from lowerRow");
        moveDownBuildingCards();
        logger.info("BuildingCards era 2 have been moved down");
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra3());
        logger.info("BuildingCards era 3 have been added to the upperRow");
        logger.info("Era 3 setup completed successfully. Current gameState: " + gameState);
    }

    /**
     * Gives food to players at the end of the turn
     */
    private void turnOrderFoodBonus() {
        logger.info("Calculating turn order food bonus.");

        int i = 0;
        for (Player p : orderedPlayers) {
            //check if turnFood > 0
            if (turnFoodPoints[i] < 0) {
                //if not, check if player can pay the food (food price is not higher than 1)
                if (p.getFood() < 1) {
                    p.addPp(-2);
                    logger.info(p.getNickname() + " is last in turnCard and can't pay the foodPrice " +
                            "at the end round, so they loose -2 PP.");
                }
                else {
                    p.addFood(turnFoodPoints[i]);
                    logger.info(p.getNickname() + " is last at the end round, so they lost "
                                + (-turnFoodPoints[i]) + " food.");
                }
                logger.info("Player " + p.getNickname() + " has " +  p.getFood() + " food and "
                        + p.getPp() + " points end round.");
            } else {
                //check if player has food bonus from buildings
                int foodFromBuilding = p.addFoodToTurnFood();
                p.addFood(turnFoodPoints[i] + foodFromBuilding);
                logger.info("The player " + p.getNickname() + " had food bonus from buildings: " +
                        foodFromBuilding);
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
        logger.info("The lowerRow has been updated after end round. Current size: " + lowerRow.size());
        upperRow.clear();
        for (int i = 0; i < numPlayers + 4; i++) {
            try {
                TribesCard c = tribesDeck.Draw();
                if (c.getEra() != gameState) nextEra();
                upperRow.add(c);
            }
            catch (InvalidOperationException e) {
                // If the deck is empty, end the game. N.B. This is how we decided to handle game ending.
                if (e.getErrorType() == ErrorType.EMPTY_DECK){
                    logger.severe("DECK IS EMPTY! Triggering endGame() inside endRound catch block.");
                    endGame();
                    logger.info("The card deck is empty, so the game ended. ");
                }
                logger.warning("Method endRound() is returning EARLY due to exception. notifyEndTurn WILL NOT BE CALLED!");
                return;
            }
            catch(Exception unknownEx) {
                logger.severe("UNKNOWN EXCEPTION CAUGHT IN END ROUND: " + unknownEx.getMessage());
                throw new InvalidOperationException(ErrorType.UNKNOWN);
            }
        }
        logger.info("The upperRow has been completely refilled. Final size: " + upperRow.size());

        notifyEndTurn(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
        logger.info("End round has been notified successfully. ");
    }

    /**
     * Ends the game.
     */
    @Override
    public void endGame() {
        logger.info("Ending game.");

        //put era to -1 to signal game has ended
        this.gameState = GameState.ENDED;
        logger.info("The game ended: era is " + gameState.name());
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
        logger.info("All the players' final points were calculated. ");
        // insert data into db
        DatabaseManager.insertGameData(this);
        logger.info("Database is updated. ");

        notifyEndGame(DatabaseManager.getRanking(this.numPlayers), orderedPlayers);
        logger.info("End game has been notified successfully. ");
    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     *
     * @param nickname              nickname of the player
     * @param offeringCardLetter    offering card picked
     */
    @Override
    public void selectOfferingCard(String nickname, Character offeringCardLetter) {
        logger.info("Request forwarded to selectOfferingCard method in model");
        // check if letter is null
        if(offeringCardLetter == null){
            logger.warning("OfferingCard not selected. ");
            throw new InvalidOperationException(ErrorType.MISSING_OFFERING_CARD_LETTER);
        }

        // get player from nickname
        Player player = orderedPlayers.stream().filter(p->nickname.equals(p.getNickname()))
                .findFirst().orElseThrow(() -> new InvalidOperationException(ErrorType.INVALID_PLAYER));

        logger.info("Player " + nickname + " wants offering card " + offeringCardLetter);

        validateOfferingCardTurnAction(offeringCardLetter, player, offeringCards, orderedPlayers);

        OfferingCard offeringCard = getOfferingCardFromLetter(offeringCardLetter, offeringCards);

        //set player to offeringCard
        offeringCard.setPlayer(player);

        // dequeue and enqueue the player in last position
        movePlayerInQueue(orderedPlayers);

        // when all player have an offering card recalculate queue
        if (isEveryPlayerInOfferingCard(orderedPlayers, offeringCards)){
            // the pick offering card phase is over
            recalculatePlayerQueue();
            // if offering card 'A' was chosen, the player receives food bonus
            handleOfferingCardWithLetterA();
        }

        // notify changes
        notifyPlayerQueue(orderedPlayers);
        notifyPlayerSelectOfferingCard(player, offeringCard);

        logger.info("Offering card " + offeringCardLetter + " successfully assigned to " + nickname + ". Notifications sent.");
    }

    /**
     * Replaces player queue with new order from offering cards
     */
    private void recalculatePlayerQueue() {
        logger.info("Recalculate player queue");
        // get players from offering cards
        List <Player> playersList = offeringCards.stream()
                .filter(card -> card.getPlayer()!=null)
                .sorted(Comparator.comparing(OfferingCard::getOrderLetter))
                .map(OfferingCard::getPlayer)
                .toList();
        // insert them in queue
        orderedPlayers.clear();
        orderedPlayers.addAll(playersList);

        logger.info("Player queue successfully recalculated ");
    }

    /**
     * Emulates a player action (picking cards).
     * @param nickname          the nickname of the player that has picked the cards
     * @param characterCards    the character cards picked by the player
     * @param buildingCards     the building cards picked by the player
     */
    @Override
    public void pickTribeCards(String nickname, List<CharacterCard> characterCards, List<BuildingCard> buildingCards)  {
        logger.info("Picking tribeCards. ");
        // get player from nickname
        Player player =  orderedPlayers.stream()
                .filter(p->nickname.equals(p.getNickname()))
                .findFirst().orElseThrow(()->new InvalidOperationException(ErrorType.INVALID_PLAYER));

        logger.info("Player " + player.getNickname() + " wants to pick tribe cards: " + characterCards + " and " + buildingCards);

        // check if cards selection is legal based on the offeringCard
        try {
            validateTribesCardTurnAction(player, orderedPlayers, offeringCards, building2OfferingCard,
                    characterCards, buildingCards, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
        } catch (InvalidOperationException e) {
            logger.warning("CardChoice is not valid: " + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            throw new InvalidOperationException(e.getMessage());
        }

        upperBuildingRow.removeAll(buildingCards); // if not present, no worries
        lowerBuildingRow.removeAll(buildingCards); // if not present, no worries
        upperRow.removeAll(characterCards); // if not present, no worries
        lowerRow.removeAll(characterCards); // if not present, no worries

        SharedModelLogic.handleOfferingCardsAndPlayersQueue(player, offeringCards, building2OfferingCard, orderedPlayers);

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard(offeringCards, building2OfferingCard);

        notifyPlayerSelectTribesCard(player, characterCards, buildingCards);
        //check everybody played his base turn
        if (nextOfferingCard == null) {
            // the round has ended
            endRound();
            logger.info("The round is ended. ");
        }
    }

    /**
     * Adds food to player in the offering card with letter A, then sets player to null
     */
    private void handleOfferingCardWithLetterA() {
        logger.info("Handling offering card with letter A.");
        // get offering card with letter A from list
        OfferingCard offeringCard = offeringCards.stream().filter(oc->oc.getOrderLetter().equals('A'))
                .findFirst().orElse(null);

        // for all the games with numPlayers < 5 the card is not found
        if (offeringCard == null) return;

        // if offering card wasn't selected return
        if(offeringCard.getPlayer() == null) return;

        // give +3 food to the player
        offeringCard.getPlayer().addFood(3);

        // remove player from offering card
        offeringCard.setPlayer(null);

        // move the player to last position in queue
        movePlayerInQueue(orderedPlayers);
    }

    @Override
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
