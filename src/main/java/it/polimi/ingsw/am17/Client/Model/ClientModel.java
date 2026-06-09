package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.SharedModelLogic;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;
import java.util.logging.Logger;

public class ClientModel implements ClientModelInterface {

    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private final String TURN_CARD_IMAGE_PATH = "/Images/TurnOrderCard/turnOrderCard_";
    private final UI userInterface;
    private final Queue<Player> orderedPlayers;
    private final List<TribesCard> upperRow;
    private final List<TribesCard> lowerRow;
    private final List<BuildingCard> upperBuildingRow;
    private final List<BuildingCard> lowerBuildingRow;
    private final List<RankingEntry> ranking;
    private final OfferingCard buildingTwoOfferingCard;
    private UUID id;
    private int numPlayers;
    private GameState gameState;
    private boolean isPickOCPhase;
    private List<UUID> gamesIdList;
    private List<OfferingCard> offeringCards;

    public ClientModel (UI userInterface) {
        this.userInterface = userInterface;
        gameState = GameState.NONE;
        gamesIdList = new ArrayList<>();
        orderedPlayers = new LinkedList<>();
        offeringCards = new ArrayList<>();
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow  = new ArrayList<>();
        ranking = new ArrayList<>();
        buildingTwoOfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
    }

    /**
     * Starts the user interface of the client
     */
    public void startInterface(){
        this.userInterface.start();
    }

    /* GETTER AND SETTERS */

    public synchronized UUID getGameId() {
        return id;
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setGameId(UUID gameId) {
        this.id = gameId;
    }

    public synchronized int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public synchronized GameState getGameState(){
        return gameState;
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setGameState(GameState gameState){
            this.gameState = gameState;
    }

    public synchronized boolean isPickOCPhase() {
        return isPickOCPhase;
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setPickOCPhase(boolean value) {
        isPickOCPhase = value;
    }

    public synchronized List<Player> getOrderedPlayers(){
        return Collections.unmodifiableCollection(orderedPlayers).stream().toList();
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setOrderedPlayers(Queue<Player> orderedPlayers){
        this.orderedPlayers.clear();
        this.orderedPlayers.addAll(orderedPlayers);
    }

    public synchronized List<TribesCard> getUpperTribeRow(){
        return Collections.unmodifiableList(upperRow);
    }
    public synchronized List<TribesCard> getLowerTribeRow(){
        return Collections.unmodifiableList(lowerRow);
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setTribeCards(List<TribesCard> upperRow, List<TribesCard> lowerRow){
        this.upperRow.clear();
        this.upperRow.addAll(upperRow);
        this.lowerRow.clear();
        this.lowerRow.addAll(lowerRow);
    }

    public synchronized List<BuildingCard> getUpperBuildingRow() {
        return Collections.unmodifiableList(upperBuildingRow);
    }
    public synchronized List<BuildingCard> getLowerBuildingRow() {
        return Collections.unmodifiableList(lowerBuildingRow);
    }

    /**
     * Setter method of ClientModel
     * This method is not synchronized! Should only be called within synchronized blocks of changes.
     */
    private void setBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        this.upperBuildingRow.clear();
        this.upperBuildingRow.addAll(upperBuildingRow);
        this.lowerBuildingRow.clear();
        if (lowerBuildingRow != null) this.lowerBuildingRow.addAll(lowerBuildingRow);
    }

    public synchronized List<UUID> getGamesIdList(){
        return Collections.unmodifiableList(gamesIdList);
    }

    // setter game id list

    public synchronized List<OfferingCard> getOfferingCards(){
        return Collections.unmodifiableList(offeringCards);
    }

    private void setOfferingCards(List<OfferingCard> offeringCards){
        this.offeringCards = offeringCards;
    }

    public synchronized OfferingCard getBuildingTwoOfferingCard() {
        return buildingTwoOfferingCard;
    }

    /**
     * Sets to null the player field of the offering card with letter A.
     */
    private void setNullOfferingCardAPlayer() {
        offeringCards.stream().filter(card -> card.getOrderLetter()=='A' && card.getPlayer()!=null)
                .findFirst().ifPresent(card -> card.setPlayer(null));
    }

    public synchronized String getTURN_CARD_IMAGE_PATH() {
        return TURN_CARD_IMAGE_PATH + numPlayers + ".png";
    }

    public synchronized List<RankingEntry> getRanking(){
        return Collections.unmodifiableList(ranking);
    }

    private void setRanking (List<RankingEntry> ranking) {
        this.ranking.clear();
        this.ranking.addAll(ranking);
    }

    /* UTILITY METHODS */

    /**
     * Looks up for the given player from the players (queue).
     * N.B. one of the few redundant synchronized
     * @param player    the player to look for
     * @return          given player or null if not found
     */
    public synchronized Player findPlayer(Player player)
    {
        return orderedPlayers.stream()
            .filter(p -> p.equals(player))
            .findFirst().orElse(null);
    }

    /**
     * Sets player to offering card.
     * If the player is the last to select, ends the offering card selection phase.
     * SETTER METHOD IN CLIENTMODEL, USE WITH SYNCHRONIZED TODO: cleanup, change name
     * @param offeringCard  offering card value
     * @param player        player to be set into offering card
     */
    private void setPlayerOfferingCard(OfferingCard offeringCard, Player player)
    {
        offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));
        if(SharedModelLogic.isEveryPlayerInOfferingCard(orderedPlayers, offeringCards)) {
            setPickOCPhase(false);
            setNullOfferingCardAPlayer();
        }
    }

    /**
     * @return true if it is the local player's turn, false otherwise.
     */
    public synchronized boolean isPlayerTurn(Player localPlayer) {
        // if the game hasn't started it is not the players turn
        if(!gameState.isGameStarted())
            return false;

        return SharedModelLogic.isPlayerTurn(localPlayer, orderedPlayers, isPickOCPhase,
                offeringCards, buildingTwoOfferingCard);
    }

    /**
     * Calls the SharedModelLogic validation for pickTribeCards action.
     * @param characterCards    The list of the picked character cards.
     * @param buildingCards     The list of the picked building cards.
     */
    public synchronized void  validateTribeCardsTurnAction(Player localPlayer, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        SharedModelLogic.validateTribesCardTurnAction(localPlayer, orderedPlayers,
                offeringCards, buildingTwoOfferingCard, characterCards, buildingCards,
                upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    /**
     * Calls the SharedModelLogic validation for pickOfferingCard action.
     * @param offeringCardLetter    The letter of the picked offering card.
     */
    public synchronized void validateOfferingCardTurnAction(Player localPlayer, Character offeringCardLetter){
        SharedModelLogic.validateOfferingCardTurnAction(offeringCardLetter, localPlayer,
                offeringCards, orderedPlayers);
    }


    /* UPDATE METHODS */
    // todo: remove setters private

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updateGameId(UUID gameId) {
        synchronized (this) {
            setGameId(gameId);
            setGameState(GameState.LOBBY);
        }

        userInterface.updateInterfaceFromGameIdChange();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updateGameIdList(List<UUID> gamesIdList){
        synchronized (this) {
            this.gamesIdList = new  ArrayList<>(gamesIdList);
        }

        userInterface.updateInterfaceFromGameIdListChange();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updateGameState(GameState gameState) {
        synchronized (this) {
            setGameState(gameState);
        }

        userInterface.updateInterfaceFromGameStateChange();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updatePlayerQueue(Queue<Player> playerQueue) {
        synchronized (this) {
            setOrderedPlayers(playerQueue);
        }

        userInterface.updateInterfaceFromPlayerQueueChange();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        synchronized (this) {
            setPlayerOfferingCard(offeringCard, player);
        }

        userInterface.updateInterfaceFromPlayerSelectOfferingCard();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        logger.info(characterCards.toString() + " " + buildingCards.toString());

        synchronized (this) {
            // update player data (pp and food plus already the cards)
            // cards in parameters are used later to remove them from rows
            List<Player> players = new ArrayList<>(orderedPlayers);
            players.replaceAll(p -> p.equals(player) ? player : p);
            orderedPlayers.clear();
            orderedPlayers.addAll(players);

            SharedModelLogic.handleOfferingCardsAndPlayersQueue(player, offeringCards, buildingTwoOfferingCard,orderedPlayers);

            // remove selected cards from rows
            upperRow.removeAll(characterCards);
            lowerRow.removeAll(characterCards);
            upperBuildingRow.removeAll(buildingCards);
            lowerBuildingRow.removeAll(buildingCards);
        }

        userInterface.updateInterfaceFromPlayerSelectTribeCards();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     * TODO: improve comments: here Players queue is stripped of cards (rightly so).
     */
    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                              List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        synchronized (this) {
            for(Player passedPlayer : players) {
                // update player PP and food in player queue
                Player localPlayer = findPlayer(passedPlayer);
                localPlayer.addPp(passedPlayer.getPp() - localPlayer.getPp());
                localPlayer.addFood(passedPlayer.getFood() - localPlayer.getFood());
            }
            setBuildingCards(upperBuildingRow, lowerBuildingRow);
            setTribeCards(upperRow, lowerRow);
            setPickOCPhase(true);
        }

        userInterface.updateInterfaceFromEndTurn();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) {
        synchronized (this) {
            setGameState(GameState.ERA1);
            setNumPlayers(players.size());
            setOrderedPlayers(players);
            setTribeCards(upperRow, lowerRow);
            setBuildingCards(upperBuildingRow, lowerBuildingRow);
            setOfferingCards(offeringCards);
            setPickOCPhase(true);
        }

        userInterface.updateInterfaceFromStartGame();
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     */
    @Override
    public void updateEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) {

        // set game state to ended
        synchronized (this) {
            gameState = GameState.ENDED;

            // set global ranking
            setRanking(ranking);

            // set local ranking
            setOrderedPlayers(orderedPlayers);
        }

        userInterface.updateInterfaceFromEndGame();

        // reset game state
        synchronized (this) {
            gameState = GameState.NONE;
        }

        logger.info("Game closed.");
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     * @param disconnectedPlayer passed to the UI.
     */
    @Override
    public void updateForcedEndGame(String disconnectedPlayer) {
        synchronized (this) {
            // Resets all game rows, offering cards, ranking and player queue
            setOrderedPlayers(new LinkedList<>());
            setOfferingCards(new ArrayList<>());
            setTribeCards(new ArrayList<>(), new ArrayList<>());
            setBuildingCards(new ArrayList<>(), new ArrayList<>());
            setRanking(new ArrayList<>());
        }

        userInterface.updateInterfaceFromForcedEndGame(disconnectedPlayer);

        // reset game state
        synchronized (this){
            gameState = GameState.NONE;
        }

        logger.info("Game forcibly closed.");
    }

    /**
     * Update method: changes the local state on a synchronized block
     * and sends an updateInterface to the UI.
     * @param exception which is passed to the UI.
     */
    // todo: set gamestate to none!!
    @Override
    public void updateError(InvalidOperationException exception) {
        userInterface.updateInterfaceFromErrorMessage(exception);
    }
}
