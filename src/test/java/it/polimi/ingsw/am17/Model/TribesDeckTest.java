package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.Builder;
import it.polimi.ingsw.am17.Model.GameCard.CardType;
import it.polimi.ingsw.am17.Model.GameCard.EventCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TribesDeckTest {
    TribesDeck tribesDeck;

    @BeforeEach
    void setUp()
    {
        tribesDeck = new TribesDeck(3);
    }

    //Draw() the card assigned to currIndex
    @Test
    void shouldDrawTribesCard(){
        List<TribesCard> deck = tribesDeck.getTribeCards();

        TribesCard carta = tribesDeck.Draw();

        assertEquals(carta, deck.get(0));

    }

    //doesn't Draw(), throws exception, end of deck
    @Test
    void shouldNotDrawTribesCard(){
        List<TribesCard> deck = tribesDeck.getTribeCards();

        for(int  i = 0; i<deck.size(); i++) {
            tribesDeck.Draw();
        }

        assertThrows(IllegalStateException.class, () -> tribesDeck.Draw());
    }

    //doesn't create empty list
    @Test
    void shouldNotHaveEmptyTribesDeck(){
        assertFalse(tribesDeck.getTribeCards().isEmpty());
    }

    //cards are drawn in order
    @Test
    void shouldDrawCardsInOrder(){
        List<TribesCard> deck = tribesDeck.getTribeCards();

        for(int  i = 0; i<deck.size(); i++) {
            assertEquals(deck.get(i), tribesDeck.Draw());
        }
    }

    //finalEvents are the last 2 cards
    @Test
    void shouldHaveFinalEventsAtTheEnd(){
        List<TribesCard> deck = tribesDeck.getTribeCards();

        TribesCard lastCard = deck.get(deck.size()-1);
        TribesCard secondLastCard = deck.get(deck.size()-2);

        assertTrue(!lastCard.getCardType().isCharacter());
        assertTrue(!secondLastCard.getCardType().isCharacter());
        assertTrue(((EventCard)lastCard).isFinal());
        assertTrue(((EventCard)secondLastCard).isFinal());
    }

    //era of the cards is always in order from 1 to 3
    @Test
    void shouldHaveRightEraOrder(){
        List<TribesCard> deck = tribesDeck.getTribeCards();
        int previousEra = deck.get(0).getEra();

        for(int  i = 1; i<deck.size(); i++) {
            int currentEra = deck.get(i).getEra();
            assertTrue(previousEra <= currentEra);
            previousEra = currentEra;
        }
    }

}