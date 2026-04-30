package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ClientModel {
    private UUID id;
    private int numPlayers;
    private int currentEra = 0;
    private boolean isPickOfferingCardPhase;

    private Player myPlayer;

    private List<UUID> gamesIdList = new ArrayList<>();

    private final Stack<Player> orderedPlayer = new Stack<>();

    private List<OfferingCard> offeringCards = new ArrayList<>();

    private final List<TribesCard> upperRow = new ArrayList<>();
    private final List<TribesCard> lowerRow = new ArrayList<>();

    private final List<BuildingCard> upperBuildingRow = new ArrayList<>();
    private final List<BuildingCard> lowerBuildingRow  = new ArrayList<>();

    public void setGameId(UUID id) {
        this.id = id;
    }

    public UUID getGameId() {
        return id;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void createLocalPlayer(String nickname, Color color) {
        myPlayer = new Player(nickname, color);
    }

    public Player getLocalPlayer() {
        return myPlayer;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setCurrentEra(int currentEra){
        this.currentEra = currentEra;
    }

    public int getCurrentEra(){
        return currentEra;
    }

    public void setOrderedPlayers(Stack<Player> orderedPlayers){
        this.orderedPlayer.clear();
        this.orderedPlayer.addAll(orderedPlayers);
    }

    public Stack<Player> getOrderedPlayers(){
        return Collections.unmodifiableCollection(orderedPlayer)
                .stream().collect(Collectors.toCollection(Stack::new));
    }

    public void setOfferingCards(List<OfferingCard> offeringCards){
        this.offeringCards = offeringCards;
    }

    public void setTribeCards(List<TribesCard> upperRow, List<TribesCard> lowerRow){
        this.upperRow.clear();
        this.upperRow.addAll(upperRow);
        this.lowerRow.clear();
        this.lowerRow.addAll(lowerRow);
    }

    public void setBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        this.upperBuildingRow.clear();
        this.upperBuildingRow.addAll(upperBuildingRow);
        this.lowerBuildingRow.clear();
        this.lowerBuildingRow.addAll(lowerBuildingRow);
    }

    public void setGameIdList(List<UUID> gamesIdList){
        this.gamesIdList = new  ArrayList<>(gamesIdList);
    }

    public Player getPlayer(Player player)
    {
        return orderedPlayer.stream()
            .filter(p -> p.equals(player))
            .findFirst().orElse(null);
    }

    public void setPlayerOfferingCard(OfferingCard offeringCard, Player player)
    {
        offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));
    }

    public List<TribesCard> getUpperTribeRow(){
        return Collections.unmodifiableList(upperRow);
    }
    public List<BuildingCard> getLowerBuildingRow(){
        return Collections.unmodifiableList(lowerBuildingRow);
    }
    public List<BuildingCard> getUpperBuildingRow(){
        return Collections.unmodifiableList(upperBuildingRow);
    }

    public List<TribesCard> getLowerTribeRow(){
        return Collections.unmodifiableList(lowerRow);
    }

    public List<OfferingCard> getOfferingCards(){
        return Collections.unmodifiableList(offeringCards);
    }

    public boolean isPlayerTurn(){
        if(currentEra<0)
            return false;
        if (isPickOfferingCardPhase){
            return orderedPlayer.peek().equals(myPlayer);
        }
        else {
            OfferingCard oc =  offeringCards.stream()
                    .filter(c -> c.getPlayer() != null)
                    .min(Comparator.comparing(OfferingCard::getOrderLetter))
                    .orElse(null);
            if(oc != null)
                return oc.getPlayer().equals(myPlayer);
            else
                return false;
        }
    }

    public void removeTribeCards(List<CharacterCard> tribeCards){
        upperRow.removeAll(tribeCards);
        lowerRow.removeAll(tribeCards);
    }

    public void removeBuildingCards(List<BuildingCard> buildingCards){
        upperBuildingRow.removeAll(buildingCards);
        lowerBuildingRow.removeAll(buildingCards);
    }

    public void setPlayerInStack(Player player){
        for (int i = 0; i < orderedPlayer.size(); i++) {
            if (orderedPlayer.get(i).equals(player)) {
                orderedPlayer.set(i,player);
                break;
            }
        }
    }

    public void removePlayerFromOfferingCard(Player player){
        offeringCards.stream()
                .filter(o -> o.getPlayer().equals(player))
                .findFirst()
                .ifPresent(o -> o.setPlayer(null));
    }

    public List<UUID> getGamesIdList(){
        return Collections.unmodifiableList(gamesIdList);
    }
}
