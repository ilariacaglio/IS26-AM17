package it.polimi.ingsw.am17.CommonInterfaces;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.FoodEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.HuntingEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.PaintingEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.RitualEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Message class for socket communication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // removes null values when serializing
public class Message implements Serializable {
    private static final Logger logger = Logger.getLogger(Message.class.getName());

    private final MessageType type;

    private UUID gameId;
    private Player player;
    private Integer numPlayers;
    private OfferingCard offeringCard;
    private Character offeringCardLetter;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "JacksonTribeCardType",
            defaultImpl = TribesCard.class)
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

    private List<UUID> gamesIdList;
    private GameState gameState;
    private Queue<Player> orderedPlayer;
    private List<OfferingCard> offeringCards;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "JacksonTribeCardType",
            defaultImpl = TribesCard.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Inventor.class, name = "inventor"),
            @JsonSubTypes.Type(value = Binder.class, name = "binder"),
            @JsonSubTypes.Type(value = Shaman.class, name = "shaman"),
            @JsonSubTypes.Type(value = Artist.class, name = "artist"),
            @JsonSubTypes.Type(value = Hunter.class, name = "hunter"),
            @JsonSubTypes.Type(value = Builder.class, name = "builder"),
            @JsonSubTypes.Type(value = RitualEvent.class, name = "ritualEvent"),
            @JsonSubTypes.Type(value = HuntingEvent.class, name = "huntingEvent"),
            @JsonSubTypes.Type(value = PaintingEvent.class, name = "paintingEvent"),
            @JsonSubTypes.Type(value = FoodEvent.class, name = "foodEvent"),
    })
    private List<TribesCard> upperRow;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "JacksonTribeCardType",
            defaultImpl = TribesCard.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Inventor.class, name = "inventor"),
            @JsonSubTypes.Type(value = Binder.class, name = "binder"),
            @JsonSubTypes.Type(value = Shaman.class, name = "shaman"),
            @JsonSubTypes.Type(value = Artist.class, name = "artist"),
            @JsonSubTypes.Type(value = Hunter.class, name = "hunter"),
            @JsonSubTypes.Type(value = Builder.class, name = "builder"),
            @JsonSubTypes.Type(value = RitualEvent.class, name = "ritualEvent"),
            @JsonSubTypes.Type(value = HuntingEvent.class, name = "huntingEvent"),
            @JsonSubTypes.Type(value = PaintingEvent.class, name = "paintingEvent"),
            @JsonSubTypes.Type(value = FoodEvent.class, name = "foodEvent")
    })
    private List<TribesCard> lowerRow;

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
    private List<BuildingCard> upperBuildingRow;

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
    private List<BuildingCard> lowerBuildingRow;

    private InvalidOperationException exception;

    private List<RankingEntry> ranking;

    private final ObjectMapper mapper = new ObjectMapper();

    @JsonCreator
    public Message(@JsonProperty("type") MessageType type) {
        this.type = type;
    }

    // TODO: comments, synchronize?
    public void send(Socket socket) throws Exception {
//        logger.setLevel(Level.FINE);
        logger.fine("Parsing message:" + this);
        if(type != MessageType.HEARTBEAT) logger.info("Sending message:" + mapper.writeValueAsString(this));
        else logger.finer("Sending heartbeat");
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

    public Integer getNumPlayers() {
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

    public Character getOfferingCardLetter() { return offeringCardLetter;}

    public void setOfferingCardLetter(Character offeringCardLetter) { this.offeringCardLetter = offeringCardLetter; }

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

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Queue<Player> getOrderedPlayer() {
        return orderedPlayer;
    }

    public void setOrderedPlayer(Queue<Player> orderedPlayer) {
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

    public InvalidOperationException getException() {return exception; }

    public void setException(InvalidOperationException message) {
        exception = message;}

    public List<RankingEntry> getRanking() {return ranking;}

    public void setRanking(List<RankingEntry> ranking) {this.ranking = ranking;}

    @Override
    public String toString() {
        return mapper.writeValueAsString(this);
    }
}
