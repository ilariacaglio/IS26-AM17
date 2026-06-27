package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TribesDeckTest {
    TribesDeck tribesDeck;

    //doesn't Draw(), throws exception, end of deck
    @Test
    void shouldNotDrawTribesCard_whenOutOfCards(){
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();

        for(int  i = 0; i<deck.size(); i++) {
            tribesDeck.Draw();
        }

        assertThrows(InvalidOperationException.class, () -> tribesDeck.Draw());
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

        for (TribesCard tribesCard : deck) {
            assertEquals(tribesCard, tribesDeck.Draw());
        }
    }

    //finalEvents are the last 2 cards
    @Test
    void shouldHaveFinalEventsAtTheEnd(){
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();

        TribesCard lastCard = deck.getLast();
        TribesCard secondLastCard = deck.get(deck.size()-2);

        assertFalse(lastCard.getCardType().isCharacter());
        assertFalse(secondLastCard.getCardType().isCharacter());
        assertTrue(((EventCard)lastCard).isFinal());
        assertTrue(((EventCard)secondLastCard).isFinal());
    }

    //era of the cards is always in order from 1 to 3
    @Test
    void shouldHaveRightEraOrder(){
        tribesDeck = new TribesDeck(3);
        List<TribesCard> deck = tribesDeck.getTribeCards();
        GameState previousEra = GameState.NONE;

        for(int  i = 1; i<deck.size(); i++) {
            GameState currentEra = deck.get(i).getEra();
            if (previousEra == GameState.NONE){
                if (currentEra == GameState.ERA2){
                    previousEra = GameState.ERA1;
                }
                else {
                    assertSame(GameState.ERA1, currentEra);
                }
            }
            else if (previousEra == GameState.ERA1){
                if (currentEra == GameState.ERA3){
                    previousEra = GameState.ERA2;
                }
                else {
                    assertSame(GameState.ERA2, currentEra);
                }
            }
            else assertSame(GameState.ERA3, currentEra);
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