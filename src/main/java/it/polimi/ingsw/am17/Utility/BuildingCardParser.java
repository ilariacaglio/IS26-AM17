package it.polimi.ingsw.am17.Utility;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.BuildingType13M;
import it.polimi.ingsw.am17.Model.GameCard.BuildingType3M;
import it.polimi.ingsw.am17.Model.OfferingCard;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class BuildingCardParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static List<BuildingType3M> buildingCard3MParser(){
        try (InputStream stream = BuildingCardParser.class.getResourceAsStream("/buildingType3M.json");) {
            if (stream == null) {
                throw new IllegalArgumentException("buildingType3M.json not found");
            }
            return mapper.readValue(stream, new TypeReference<List<BuildingType3M>>() {});
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading building 3M cards", ex);
        }
    }

    public static List<BuildingType13M> buildingCard13MParser(int era){
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream stream = BuildingCardParser.class.getResourceAsStream("/buildingType13M.json");) {
            if (stream == null) {
                throw new IllegalArgumentException("buildingType13M.json not found");
            }
            var buildingList =  mapper.readValue(stream, new TypeReference<List<BuildingType13M>>() {});
            return buildingList.stream()
                    .filter(c->c.getEra() == era)
                    .toList();
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading Building 13M cards", ex);
        }
    }
}
