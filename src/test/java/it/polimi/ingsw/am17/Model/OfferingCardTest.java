package it.polimi.ingsw.am17.Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class OfferingCardTest {
    static List<OfferingCard> offeringCards;

    @BeforeEach
    void setUp() {

        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        offeringCards = mapper.readValue(new File("offeringCards.json"), OfferingCard.class);
        print(offeringCards);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getOrderLetter() {
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