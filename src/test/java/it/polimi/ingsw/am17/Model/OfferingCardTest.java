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
        for (OfferingCard offeringCard : offeringCards) {
            System.out.println(offeringCard.getFoodBonus());
        }
    }

    @Test
    void getNumCardsLower() {
        for (OfferingCard offeringCard : offeringCards) {
            System.out.println(offeringCard.getNumCardsLower());
        }
    }

    @Test
    void getMinPlayers() {
        for (OfferingCard offeringCard : offeringCards) {
            System.out.println(offeringCard.getMinPlayers());
        }
    }

    @Test
    void getNumCardsUpper() {
        for (OfferingCard offeringCard : offeringCards) {
            System.out.println(offeringCard.getNumCardsUpper());
        }
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