package it.polimi.ingsw.am17.Utility;

import it.polimi.ingsw.am17.Model.OfferingCard;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class OfferingCardParser {
    public static List<OfferingCard> loadOfferingCards(int numPlayers) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream stream = OfferingCardParser.class.getResourceAsStream("/offeringCards.json");) {
            if (stream == null) {
                throw new IllegalArgumentException("offering_cards.json not found");
            }
            var cardList = mapper.readValue(stream, new TypeReference<List<OfferingCard>>() {});
            return cardList.stream()
                    .filter(c-> numPlayers >= c.getMinPlayers())
                    .toList();
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading offering cards", ex);
        }
    }
}
