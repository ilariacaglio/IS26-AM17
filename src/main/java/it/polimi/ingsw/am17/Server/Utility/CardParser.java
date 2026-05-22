package it.polimi.ingsw.am17.Server.Utility;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType13M;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType3M;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.FoodEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.HuntingEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.PaintingEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.RitualEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CardParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static List<OfferingCard> loadOfferingCards(int numPlayers){
        try{
            var cardList = mapper.readValue(CardParser.class.getResourceAsStream("/offeringCards.json"), new TypeReference<List<OfferingCard>>() {});
            return cardList.stream()
                    .filter(c-> numPlayers >= c.getMinPlayers())
                    .toList();
        }
        catch (Exception ex){
            throw new RuntimeException("Error while loading offering cards", ex);
        }
    }

    public static List<BuildingType3M> buildingCard3MParser(){
        try {
            return mapper.readValue(CardParser.class.getResourceAsStream("/buildingType3M.json"), new TypeReference<List<BuildingType3M>>() {});
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading building 3M cards", ex);
        }
    }

    public static List<BuildingType13M> buildingCard13MParser(GameState era){
        try {
            var buildingList =  mapper.readValue(CardParser.class.getResourceAsStream("/buildingType13M.json"), new TypeReference<List<BuildingType13M>>() {});
            return buildingList.stream()
                    .filter(c->c.getEra() == era)
                    .toList();
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading Building 13M cards", ex);
        }
    }

    public static List<Artist> artistsParser(int numPlayers){
        try {
            List<Artist> list = mapper.readValue(
                    CardParser.class.getResourceAsStream("/Artist.json"),
                    new TypeReference<ArrayList<Artist>>() {}
            );
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading artists", ex);
        }
    }

    public static List<Binder> bindersParser (int numPlayers){
        try {
            List<Binder> list = mapper.readValue(CardParser.class.getResourceAsStream("/Binder.json"), new TypeReference<ArrayList<Binder>>() {});
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading binders", ex);
        }
    }

    public static List<Builder> buildersParser(int numPlayers){
        try {
            List<Builder> list = mapper.readValue(CardParser.class.getResourceAsStream("/Builder.json"), new TypeReference<ArrayList<Builder>>() {});
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading builders", ex);
        }
    }

    public static List<Hunter> huntersParser(int numPlayers){
        try {
            List<Hunter> list = mapper.readValue(CardParser.class.getResourceAsStream("/Hunter.json"), new TypeReference<ArrayList<Hunter>>() {});
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading hunters", ex);
        }
    }

    public static List<Inventor> inventorParser(int numPlayers){
        try {
            List<Inventor> list = mapper.readValue(CardParser.class.getResourceAsStream("/Inventor.json"), new TypeReference<ArrayList<Inventor>>() {});
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading inventors", ex);
        }
    }

    public static List<Shaman> shamansParser(int numPlayers){
        try {
            List<Shaman> list = mapper.readValue(CardParser.class.getResourceAsStream("/Shaman.json"), new TypeReference<ArrayList<Shaman>>() {});
            return list.stream().filter(c->numPlayers >= c.getMinPlayers()).toList();
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading shamans", ex);
        }
    }

    public static List<FoodEvent> foodEventParser(){
        try {
            return mapper.readValue(CardParser.class.getResourceAsStream("/FoodEvent.json"), new TypeReference<ArrayList<FoodEvent>>() {});
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading food event cards", ex);
        }
    }

    public static List<HuntingEvent> huntingEventParser(){
        try {
            return mapper.readValue(CardParser.class.getResourceAsStream("/HuntingEvent.json"), new TypeReference<ArrayList<HuntingEvent>>() {});
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading hunting event cards", ex);
        }
    }

    public static List<PaintingEvent> paintingEventParser(){
        try {
            return mapper.readValue(CardParser.class.getResourceAsStream("/PaintingEvent.json"), new TypeReference<ArrayList<PaintingEvent>>() {});
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading painting event cards", ex);
        }
    }

    public static List<RitualEvent> ritualEventParser(){
        try {
            return mapper.readValue(CardParser.class.getResourceAsStream("/RitualEvent.json"), new TypeReference<ArrayList<RitualEvent>>() {});
        }
        catch(Exception ex) {
            throw new RuntimeException("Error while loading ritual event cards", ex);
        }
    }
}
