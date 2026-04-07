package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        game = new Game(3);
    }

    @Test
    void testAddPlayer(){
        // player creation
        Player p = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        Player p3 = new Player("player3",Color.YELLOW);
        Player p4 = new Player("player4",Color.BLUE);
        List<Player> players = new ArrayList<>();
        // add players to game
        game.addPlayer(p);
        game.addPlayer(p2);
        game.addPlayer(p3);
        // add players to list
        players.add(p);
        players.add(p2);
        players.add(p3);
        // check usual and unusual behavior
        assertEquals(players,game.getPlayers());
        assertThrows(IllegalStateException.class, () -> game.addPlayer(p4));
    }

    @Test
    void testAddPlayer_Duplicate(){
        // player creation
        Player p = new Player("player2",Color.RED);
        // add player to game
        game.addPlayer(p);
        // check duplicate player
        assertThrows(IllegalArgumentException.class, () -> game.addPlayer(p));
    }

    @Test
    void testAddPlayer_Exception(){
        // add players to game
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        //this checks both started and overflow cases
        assertThrows(IllegalStateException.class, () -> game.addPlayer(new Player("player4",Color.BLUE)));
    }

    // TODO era 1
    @Test
    void testEra1(){

    }
    // TODO movedownbuildingcards
    // TODO endRound
    // TODO endGame
    // TODO validateCardChoice
    // TODO selectOfferingCard
    // TODO playerAction
    // TODO getNextOccupiedOfferingCard



//    @Test
//    void testGetNextTurn(){
//        Player p1 = new Player("player1",Color.BLACK);
//        Player p2 = new Player("player2",Color.RED);
//        Player p3 = new Player("player3",Color.YELLOW);
//        p1.setOfferingCard(new OfferingCard(2,'F',1,1,0));
//        p2.setOfferingCard(new OfferingCard(2,'C',1,1,0));
//        p3.setOfferingCard(new OfferingCard(2,'D',1,1,0));
//        game.addPlayer(p1);
//        game.addPlayer(p2);
//        game.addPlayer(p3);
//        //call method
//        Player p = game.getNextTurn();
//        // check that the player returned has the lowest letter
//        assertEquals('C',p.getOfferingCard().getOrderLetter());
//        p = game.getNextTurn();
//        // check that the player returned has the lowest letter
//        assertEquals('D',p.getOfferingCard().getOrderLetter());
//        p = game.getNextTurn();
//        // check that the player returned has the lowest letter
//        assertEquals('F',p.getOfferingCard().getOrderLetter());
//        // check exception
//        assertThrows(IllegalStateException.class, () -> game.getNextTurn());
//    }
//
//    @Test
//    void testEndTurn(){
//        game.start();
//        var oldUpperRow = game.getUpperRow().getCards();
//        int oldEra = game.getCurrentEra();
//        game.endTurn();
//        //new lower row equals to old upper row
//        assertTrue(game.getLowerRow().getCards().containsAll(oldUpperRow) &&
//                oldUpperRow.containsAll(game.getLowerRow().getCards()));
//        // check size new upper row
//        assertEquals(game.getUpperRowSize(), game.getNumPlayers()+4);
//        //check update era
//        int maxTurns = 10;
//        int numTurns = 0;
//        while (game.getCurrentEra() == oldEra && numTurns < maxTurns) {
//            game.endTurn();
//            numTurns++;
//        }
//        assertEquals(game.getCurrentEra(), oldEra+1);
//    }
//
//    @Test
//    void testChangeEra_currentEraIs1() {
//        game.start();
//        assertThrows(IllegalStateException.class, () -> game.changeEra());
//    }
//
//    @Test
//    void testChangeEra_currentEraIs2(){
//        game.start();
//        var oldBuildingRow = game.getUpperBuildingRow();
//        game.setCurrentEra(2);
//        game.changeEra();
//        //new lower building row contains old upper building row
//        assertTrue(game.getLowerBuildingRow().getCards().containsAll(oldBuildingRow.getCards()));
//        //check if the new upper building row has only cards with the correct era
//        for(BuildingCard card : game.getUpperBuildingRow().getCards())
//            assertEquals(2, card.getEra());
//    }
//
//    @Test
//    void testChangeEra_currentEraIs3(){
//        game.start();
//        game.setCurrentEra(2);
//        game.changeEra();
//        var oldBuildingRow = game.getUpperBuildingRow().getCards();
//        game.setCurrentEra(3);
//        game.changeEra();
//        //new lower building row equals to old upper building row
//        assertTrue(game.getLowerBuildingRow().getCards().containsAll(oldBuildingRow));
//        //check if the new upper building row has only cards with the correct era
//        for(BuildingCard card : game.getUpperBuildingRow().getCards())
//            assertEquals(3, card.getEra());
//    }
//
//    @Test
//    void testRemoveBuildingCardFromRow(){
//        game.start();
//        BuildingCard card;
//        // draw a card from the upper building row
//        card = (BuildingCard) game.getUpperBuildingRow().getCards().getFirst();
//        game.removeBuildingCardFromRow(card);
//        assertFalse(game.getUpperBuildingRow().getCards().contains(card));
//        //play turns to insert elements in lower building row
//        game.endTurn();
//        game.endTurn();
//        game.endTurn();
//        // draw a card from the lower building row
//        card = (BuildingCard) game.getLowerBuildingRow().getCards().getFirst();
//        game.removeBuildingCardFromRow(card);
//        assertFalse(game.getLowerBuildingRow().getCards().contains(card));
//        // create building card
//        card = new BuildingCard(1,1,1);
//        BuildingCard finalCard = card;
//        assertThrows(IllegalStateException.class, () -> game.removeBuildingCardFromRow(finalCard));
//    }
//
//
//    @Test
//    void testRemoveTribeCardFromRow(){
//        game.start();
//        // create game card
//        TribesCard card;
//        // draw a card from the upper row
//        card = (TribesCard) game.getUpperRow().getCards().getFirst();
//        game.removeTribeCardFromRow(card);
//        assertFalse(game.getUpperRow().getCards().contains(card));
//        // draw a card from the lower row
//        card = (TribesCard) game.getLowerRow().getCards().getFirst();
//        game.removeTribeCardFromRow(card);
//        assertFalse(game.getLowerRow().getCards().contains(card));
//        // create card
//        card = new TribesCard(1, CardType.ARTIST);
//        TribesCard finalCard = card;
//        assertThrows(IllegalStateException.class, () -> game.removeTribeCardFromRow(finalCard));
//    }
}