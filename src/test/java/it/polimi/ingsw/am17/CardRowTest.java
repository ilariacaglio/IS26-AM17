package it.polimi.ingsw.am17;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardRowTest {
    CardRow cardRow;

    @BeforeEach
    void setUp(){cardRow = new CardRow();}

    @Test
    void testShouldAddCard(){
        GameCard c = null;
        //all'inizio la lista è vuota
        assertEquals(0, cardRow.getCards().size());
        cardRow.addCard(c);
        //ora la lista deve avere 1 elemento
        assertEquals(1, cardRow.getCards().size());
        assertTrue(cardRow.getCards().contains(c));

    }

}