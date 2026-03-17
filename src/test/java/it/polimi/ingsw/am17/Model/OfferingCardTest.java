package it.polimi.ingsw.am17.Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

class OfferingCardTest {
    static List<OfferingCard> offeringCards;

    @BeforeEach
    void setUp() {

        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        offeringCards = mapper.readValue(getClass().getClassLoader()
                .getResourceAsStream("offeringCards.json"), new TypeReference<>() {
        });
        System.out.println(offeringCards);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getOrderLetter() {
        for (OfferingCard offeringCard : offeringCards) {
            System.out.println(offeringCard.getOrderLetter());
        }
    }

    @Test
    void getFoodBonus() {
    }

    @Test
    void getNumCardsLower() {
    }

    @Test
    void getMinPlayers() {
    }

    @Test
    void getNumCardsUpper() {
    }

    @Test
    void attach() {
    }

    @Test
    void detach() {
    }

    @Test
    void notifyObserver() {
    }
}