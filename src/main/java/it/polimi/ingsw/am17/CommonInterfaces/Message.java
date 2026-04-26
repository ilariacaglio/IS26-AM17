package it.polimi.ingsw.am17.CommonInterfaces;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Message class for socket communication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // removes null values when serializing
public class Message implements Serializable {
    private final MessageType type;

    private UUID gameId;
    private Player player;
    private int numPlayers;
    private OfferingCard offeringCard;
    private List<CharacterCard> characterCards;
    private List<BuildingCard> buildingCards;

    private List<UUID> gamesIdList;
    private int era;
    private Stack<Player> orderedPlayer;
    private List<OfferingCard> offeringCards;
    private List<TribesCard> upperRow;
    private List<TribesCard> lowerRow;
    private List<BuildingCard> upperBuildingRow;
    private List<BuildingCard> lowerBuildingRow;

    private final ObjectMapper mapper = new ObjectMapper();

    @JsonCreator
    public Message(@JsonProperty("type") MessageType type) {
        this.type = type;
    }

    // TODO: comments, synchronize?
    public void send(Socket socket) throws Exception {
        System.err.println("Sending message:" + mapper.writeValueAsString(this));
        String json = mapper.writeValueAsString(this);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(json);
    }

    public MessageType getType() {
        return type;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(Integer numPlayers) {
        this.numPlayers = numPlayers;
    }

    public OfferingCard getOfferingCard() {
        return offeringCard;
    }

    public void setOfferingCard(OfferingCard offeringCard) {
        this.offeringCard = offeringCard;
    }

    public List<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    public void setCharacterCards(List<CharacterCard> characterCards) {
        this.characterCards = characterCards;
    }

    public List<BuildingCard> getBuildingCards() {
        return buildingCards;
    }

    public void setBuildingCards(List<BuildingCard> buildingCards) {
        this.buildingCards = buildingCards;
    }

    public List<UUID> getGamesIdList() {
        return gamesIdList;
    }

    public void setGamesIdList(List<UUID> gamesIdList) {
        this.gamesIdList = gamesIdList;
    }

    public int getEra() {
        return era;
    }

    public void setEra(Integer era) {
        this.era = era;
    }

    public Stack<Player> getOrderedPlayer() {
        return orderedPlayer;
    }

    public void setOrderedPlayer(Stack<Player> orderedPlayer) {
        this.orderedPlayer = orderedPlayer;
    }

    public List<OfferingCard> getOfferingCards() {
        return offeringCards;
    }

    public void setOfferingCards(List<OfferingCard> offeringCards) {
        this.offeringCards = offeringCards;
    }

    public List<TribesCard> getUpperRow() {
        return upperRow;
    }

    public void setUpperRow(List<TribesCard> upperRow) {
        this.upperRow = upperRow;
    }

    public List<TribesCard> getLowerRow() {
        return lowerRow;
    }

    public void setLowerRow(List<TribesCard> lowerRow) {
        this.lowerRow = lowerRow;
    }

    public List<BuildingCard> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    public void setUpperBuildingRow(List<BuildingCard> upperBuildingRow) {
        this.upperBuildingRow = upperBuildingRow;
    }

    public List<BuildingCard> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    public void setLowerBuildingRow(List<BuildingCard> lowerBuildingRow) {
        this.lowerBuildingRow = lowerBuildingRow;
    }
}
