package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientModel {
    private final Logger logger = Logger.getLogger(ClientModel.class.getName());

    private final UI userInterface;

    private UUID id;
    private int numPlayers;
    private int currentEra;
    private boolean isPickOCPhase;

    private Player myPlayer;

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
        currentEra = 0;
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
        // UI communication
        userInterface.printGameId(id);
    }

    public UUID getGameId() {
        return id;
    }

    public List<UUID> getGamesIdList(){
        return Collections.unmodifiableList(gamesIdList);
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void createLocalPlayer(String nickname, Color color) {
        myPlayer = new Player(nickname, color);
    }

    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    private void setLocalPlayer() {
        Player foundPlayer = orderedPlayer.stream()
                .filter(p->p.getNickname().equals(myPlayer.getNickname()))
                .findFirst().orElse(null);
        if (foundPlayer != null) {
            myPlayer = foundPlayer;
        }
    }

    public Player getLocalPlayer() {
        return myPlayer;
    }

    /**
     * Sets currentEra field and displays it on the screen
     * @param currentEra    the value to be set
     */
    public void setCurrentEra(int currentEra){
        this.currentEra = currentEra;
        // UI communication
        userInterface.printEra();
    }

    public int getCurrentEra(){
        return currentEra;
    }

    public boolean isPickOCPhase() {
        return isPickOCPhase;
    }

    public void setPickOCPhase(boolean value) {
        isPickOCPhase = value;
    }

    // TODO: print in another method
    public void setOrderedPlayers(Queue<Player> orderedPlayers){
        this.orderedPlayer.clear();
        this.orderedPlayer.addAll(orderedPlayers);
        setLocalPlayer();
        // UI communication
        userInterface.drawInterface(null);
    }

    // TODO: why stack?
    public Stack<Player> getOrderedPlayers(){
        return Collections.unmodifiableCollection(orderedPlayer)
                .stream().collect(Collectors.toCollection(Stack::new));
    }

    /**
     * Replaces the player with the value passed as parameter.
     * @param player    the player to be set
     */
    public void setPlayerInQueue(Player player){
        List<Player> players = new ArrayList<>(orderedPlayer);
        players.replaceAll(p -> p.equals(player) ? player : p);
        orderedPlayer.clear();
        orderedPlayer.addAll(players);
        Player lastPlayer = orderedPlayer.poll();
        orderedPlayer.add(lastPlayer);
        setLocalPlayer();
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

    /**
     * Searches for the player into players list and returns player object
     * @param player    the player to look for
     * @return          the player object in player collection
     */
    public Player getPlayer(Player player)
    {
        return orderedPlayer.stream()
            .filter(p -> p.equals(player))
            .findFirst().orElse(null);
    }

    /**
     * Sets player to offering card.
     * When condition met the phase of offering cards selection ends.
     * @param offeringCard  offering card value
     * @param player        player to be set into offering card
     */
    public void setPlayerOfferingCard(OfferingCard offeringCard, Player player)
    {
        offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));
        if(everyPlayerInOfferingCard())
            setPickOCPhase(false);
    }

    /**
     * Sets offering cards player to null
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
        if(currentEra<1)
            return false;
        return orderedPlayer.peek().equals(myPlayer);
    }

    public void setRanking (List<RankingEntry> ranking) {
        this.ranking.clear();
        this.ranking.addAll(ranking);
    }

    public List<RankingEntry> getRanking(){
        return Collections.unmodifiableList(ranking);
    }
}
