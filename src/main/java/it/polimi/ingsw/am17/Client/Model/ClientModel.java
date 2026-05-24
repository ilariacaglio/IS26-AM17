package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.ColorException;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;
import java.util.logging.Logger;

public class ClientModel {
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());

    private final UI userInterface;

    private UUID id;
    private int numPlayers;
    private GameState gameState;
    private boolean isPickOCPhase;

    private List<UUID> gamesIdList;

    private final Queue<Player> orderedPlayer;

    private List<OfferingCard> offeringCards;

    private final List<TribesCard> upperRow;
    private final List<TribesCard> lowerRow;

    private final List<BuildingCard> upperBuildingRow;
    private final List<BuildingCard> lowerBuildingRow;

    private final List<RankingEntry> ranking;

    public ClientModel (UI userInterface) {
        this.userInterface = userInterface;
        gameState = GameState.NONE;
        gamesIdList = new ArrayList<>();
        orderedPlayer = new LinkedList<>();
        offeringCards = new ArrayList<>();
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow  = new ArrayList<>();
        ranking = new ArrayList<>();
    }

    /**
     * Starts the user interface of the client
     */
    public void startInterface(){
        this.userInterface.start();
    }

    /**
     * Sets field gameId and displays it on the screen.
     * @param id    the value to be set
     */
    public void setGameId(UUID id) {
        this.id = id;
        setGameState(GameState.LOBBY);
        // UI communication
        userInterface.printGameId(id);
        userInterface.drawInterface(null);
    }

    public UUID getGameId() {
        return id;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Sets currentEra field and displays it on the screen
     * @param gameState    the value to be set
     */
    public void setGameState(GameState gameState){
        this.gameState = gameState;
        // UI communication
        userInterface.printEra();
    }

    public GameState getGameState(){
        return gameState;
    }

    public boolean isPickOCPhase() {
        return isPickOCPhase;
    }

    public void setPickOCPhase(boolean value) {
        isPickOCPhase = value;
    }

    public void setOrderedPlayers(Queue<Player> orderedPlayers){
        this.orderedPlayer.clear();
        this.orderedPlayer.addAll(orderedPlayers);
        userInterface.setLocalPlayer();
    }

    public List<Player> getOrderedPlayers(){
        return Collections.unmodifiableCollection(orderedPlayer).stream().toList();
    }

    /**
     * Replaces the player in the queue managing turn order with the value passed as parameter.
     * @param player    the player to be set
     */
    public void setPlayerInQueue(Player player){
        List<Player> players = new ArrayList<>(orderedPlayer);
        players.replaceAll(p -> p.equals(player) ? player : p);
        orderedPlayer.clear();
        orderedPlayer.addAll(players);
        Player lastPlayer = orderedPlayer.poll();
        orderedPlayer.add(lastPlayer);
        userInterface.setLocalPlayer();
    }

    public void setOfferingCards(List<OfferingCard> offeringCards){
        this.offeringCards = offeringCards;
    }

    /**
     * Sets tribe upper and lower row
     * @param upperRow  the new list to be set
     * @param lowerRow  the new list to be set
     */
    public void setTribeCards(List<TribesCard> upperRow, List<TribesCard> lowerRow){
        this.upperRow.clear();
        this.upperRow.addAll(upperRow);
        this.lowerRow.clear();
        this.lowerRow.addAll(lowerRow);
    }

    public List<TribesCard> getUpperTribeRow(){
        return Collections.unmodifiableList(upperRow);
    }

    public List<TribesCard> getLowerTribeRow(){
        return Collections.unmodifiableList(lowerRow);
    }

    /**
     * Removes all the cards in the param from upper and lower tribe rows
     * @param tribeCards    the cards to be removed.
     */
    public void removeTribeCards(List<CharacterCard> tribeCards){
        upperRow.removeAll(tribeCards);
        lowerRow.removeAll(tribeCards);
    }

    /**
     * Sets building upper and lower row
     * @param upperBuildingRow  the new list to be set
     * @param lowerBuildingRow  the new list to be set
     */
    public void setBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        this.upperBuildingRow.clear();
        this.upperBuildingRow.addAll(upperBuildingRow);
        this.lowerBuildingRow.clear();
        if (lowerBuildingRow != null) this.lowerBuildingRow.addAll(lowerBuildingRow);
    }

    public List<BuildingCard> getLowerBuildingRow(){
        return Collections.unmodifiableList(lowerBuildingRow);
    }

    public List<BuildingCard> getUpperBuildingRow(){
        return Collections.unmodifiableList(upperBuildingRow);
    }

    /**
     * Removes all the cards in the param from upper and lower building rows
     * @param buildingCards    the cards to be removed.
     */
    public void removeBuildingCards(List<BuildingCard> buildingCards){
        upperBuildingRow.removeAll(buildingCards);
        lowerBuildingRow.removeAll(buildingCards);
    }

    /**
     * sets gameIdList value and displays it on the screen
     * @param gamesIdList   the value to be set
     */
    public void setGameIdList(List<UUID> gamesIdList){
        this.gamesIdList = new  ArrayList<>(gamesIdList);
        userInterface.printGamesList();
    }

    public List<UUID> getGamesIdList(){
        return Collections.unmodifiableList(gamesIdList);
    }

    /**
     * Searches for the player into players list and returns player object
     * @param player    the player to look for
     * @return          the player object in player collection
     */
    public Player getPlayerFromList(Player player)
    {
        return orderedPlayer.stream()
            .filter(p -> p.equals(player))
            .findFirst().orElse(null);
    }

    /**
     * Sets player to offering card.
     * If the player is the last to select, ends the offering card selection phase.
     * @param offeringCard  offering card value
     * @param player        player to be set into offering card
     */
    public void setPlayerOfferingCard(OfferingCard offeringCard, Player player)
    {
        offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));
        if(everyPlayerInOfferingCard()) {
            setPickOCPhase(false);
            setNullOfferingCardAPlayer();
        }
    }

    /**
     * Removes player from currently assigned offering card.
     * @param player the player already present into the offering card field
     */
    public void removePlayerFromOfferingCard(Player player){
        offeringCards.stream()
                .filter(o -> o.getPlayer()!= null && o.getPlayer().equals(player))
                .findFirst()
                .ifPresent(o -> o.setPlayer(null));
    }

    public List<OfferingCard> getOfferingCards(){
        return Collections.unmodifiableList(offeringCards);
    }

    /**
     * @return true if every player of the game is into an offering card, false otherwise
     */
    public boolean everyPlayerInOfferingCard(){
        for(Player p : orderedPlayer){
            OfferingCard oc = offeringCards.stream()
                    .filter(c-> c.getPlayer()!= null && c.getPlayer().equals(p))
                    .findFirst().orElse(null);
            if(oc == null)
                return false;
        }
        return true;
    }

    /**
     * Sets to null the player field of the offering card with letter A.
     */
    public void setNullOfferingCardAPlayer() {
        offeringCards.stream().filter(card -> card.getOrderLetter()=='A' && card.getPlayer()!=null)
                .findFirst().ifPresent(card -> card.setPlayer(null));
    }

    /**
     * Checks if it is the turn of the local player.
     * @return true if it is players turn, false otherwise.
     */
    public boolean isPlayerTurn(){
        if(!gameState.isGameStarted())
            return false;
        return userInterface.getLocalPlayer().equals(orderedPlayer.peek());
    }

    public void setRanking (List<RankingEntry> ranking) {
        this.ranking.clear();
        this.ranking.addAll(ranking);
    }

    public List<RankingEntry> getRanking(){
        return Collections.unmodifiableList(ranking);
    }

    /**
     * Updates players in queue and displays it to screen
     * @param playerQueue   the value to be set
     */
    public void updatePlayerQueue(Queue<Player> playerQueue) {
        setOrderedPlayers(playerQueue);
        // UI communication
        userInterface.drawInterface(null);
    }

    /**
     * Sets model params to new values when game starts and displays it to screen.
     * @param players               the players queue value to be set.
     * @param upperRow              the upper tribe row value to be set.
     * @param lowerRow              the lower tribe row value to be set.
     * @param upperBuildingRow      the upper building row value to be set.
     * @param lowerBuildingRow      the lower building row value to be set.
     * @param offeringCards         the offering cards value to be set.
     */
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) {

        setGameState(GameState.ERA1);
        setNumPlayers(players.size());
        setOrderedPlayers(players);
        setTribeCards(upperRow, lowerRow);
        setBuildingCards(upperBuildingRow, lowerBuildingRow);
        setOfferingCards(offeringCards);
        setPickOCPhase(true);

        userInterface.drawInterface(null);
    }


    /**
     * Sets model params to new values when turn ends and displays it to screen.
     * @param players               the players queue value to be set.
     * @param upperRow              the upper tribe row value to be set.
     * @param lowerRow              the lower tribe row value to be set.
     * @param upperBuildingRow      the upper building row value to be set.
     * @param lowerBuildingRow      the lower building row value to be set.
     */
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                     List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        for(Player player : players) {
            // update player PP and food in player queue
            updatePlayerValue(getPlayerFromList(player), player);
        }
        setBuildingCards(upperBuildingRow, lowerBuildingRow);
        setTribeCards(upperRow, lowerRow);
        setPickOCPhase(true);

        userInterface.drawInterface(null);
    }

    /**
     * Updates PP and food of the old player with values from the new player
     * @param oldP  the outdated value of the player
     * @param newP  the new value of the player
     */
    private static void updatePlayerValue(Player oldP, Player newP) {
        oldP.addPp(newP.getPp()- oldP.getPp());
        oldP.addFood(newP.getFood() - oldP.getFood());
    }

    /**
     * Updates offering cards list when a player selects one and displays it to screen.
     * @param player            the player that picks the offering card
     * @param offeringCard      the offering card picked
     */
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        setPlayerOfferingCard(offeringCard, player);
        userInterface.drawInterface(null);
    }

    /**
     * Updates player cards and rows when player picks cards and displays it to screen.
     * @param player            the player that picked the cards
     * @param characterCards    the character cards picked by the player
     * @param buildingCards     the building cards picked by the player
     */
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        logger.info(characterCards.toString() + " " + buildingCards.toString());
        setPlayerInQueue(player);
        removePlayerFromOfferingCard(player);
        removeTribeCards(characterCards);
        removeBuildingCards(buildingCards);
        userInterface.drawInterface(null);
    }

    /**
     * Updates ranking field and displays it to screen.
     * @param ranking   the value to be set
     */
    public void updateRanking(List<RankingEntry> ranking) {
        setRanking(ranking);
        userInterface.drawInterface(null);
        gameState = GameState.NONE;
    }

    /**
     * Updates era value when a user disconnects and displays it to the screen
     */
    public void updateGameEndedByUser() {
        // setGameId(null); // TODO: does not work, breaks RMI communication?!
        gameState = GameState.NONE; // TODO: -2 for aborted game? This works anyways
        setOrderedPlayers(new LinkedList<>());
        setOfferingCards(new ArrayList<>());
        setTribeCards(new ArrayList<>(), new ArrayList<>());
        setBuildingCards(new ArrayList<>(), new ArrayList<>());
        setRanking(new ArrayList<>());
        logger.info("Game closed.");
        // TODO: notify user interface that the game has ended due to the disconnection of player with "nickname"
    }

    public void updateNotifyError(InvalidOperationException exception) {
        ErrorType type = exception.getErrorType();
        switch (type) {
            case DUPLICATE_COLOR:
                userInterface.setAvailableColors(((ColorException)exception).getAvailableColors());
                setGameState(GameState.NONE);
                break;
            case DUPLICATE_NICKNAME:
                setGameState(GameState.NONE);
                break;
        }
        String messageToDisplay = (type == ErrorType.UNKNOWN)
                ? exception.getMessage()
                : type.getMessage();
        userInterface.drawInterface(messageToDisplay);
    }
}
