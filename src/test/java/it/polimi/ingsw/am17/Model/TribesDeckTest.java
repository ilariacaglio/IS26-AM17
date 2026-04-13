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

    //doesn't Draw(), throws exception, end of deck
    @Test
    void shouldNotDrawTribesCard(){
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();

        for(int  i = 0; i<deck.size(); i++) {
            tribesDeck.Draw();
        }

        assertThrows(IllegalStateException.class, () -> tribesDeck.Draw());
    }

    //doesn't create empty list
    @Test
    void shouldNotHaveEmptyTribesDeck(){
        tribesDeck = new TribesDeck(3);
        assertFalse(tribesDeck.getTribeCards().isEmpty());
    }

    //cards are drawn in order
    @Test
    void shouldDrawCardsInOrder(){
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();

        for(int  i = 0; i<deck.size(); i++) {
            assertEquals(deck.get(i), tribesDeck.Draw());
        }
    }

    //finalEvents are the last 2 cards
    @Test
    void shouldHaveFinalEventsAtTheEnd(){
        tribesDeck = new TribesDeck(3);
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
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();
        int previousEra = 1;

        for(int  i = 1; i<deck.size(); i++) {
            int currentEra = deck.get(i).getEra();
            assertTrue(previousEra <= currentEra);
            previousEra = currentEra;
        }


    }

    //the size of the deck is always the same if given numPlayer==2
    @Test
    void shouldHaveConsistentDeckSize2Players() {
        tribesDeck = new TribesDeck(2);
        int size = tribesDeck.getTribeCards().size();

        assertEquals(size, new TribesDeck(2).getTribeCards().size());
    }

    //the size of the deck is always the same if given numPlayer==3
    @Test
    void shouldHaveConsistentDeckSize3Players() {
        tribesDeck = new TribesDeck(3);
        int size = tribesDeck.getTribeCards().size();

        assertEquals(size, new TribesDeck(3).getTribeCards().size());
    }

    //the size of the deck is always the same if given numPlayer==4
    @Test
    void shouldHaveConsistentDeckSize4Players() {
        tribesDeck = new TribesDeck(4);
        int size = tribesDeck.getTribeCards().size();

        assertEquals(size, new TribesDeck(4).getTribeCards().size());
    }

    //the size of the deck is always the same if given numPlayer==5
    @Test
    void shouldHaveConsistentDeckSize5Players() {
        tribesDeck = new TribesDeck(5);
        int size = tribesDeck.getTribeCards().size();

        assertEquals(size, new TribesDeck(5).getTribeCards().size());
    }
}