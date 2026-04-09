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
        game = new Game(3,3);
    }

    @Test
    void testAddPlayer_withGameStart(){
        // player creation
        Player p1 = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        Player p3 = new Player("player3",Color.YELLOW);
        // add players to game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        // check that every player is in the list and the size is 3
        assertTrue(game.getPlayers().contains(p1));
        assertTrue(game.getPlayers().contains(p2));
        assertTrue(game.getPlayers().contains(p3));
        assertEquals(3,game.getPlayers().size());
        // check the game has started
        //check current era updated to 1
        assertEquals(1,game.getCurrentEra());
        //check size of upper and lower tribe rows
        assertEquals(4, game.getLowerRow().size());
        assertEquals(7, game.getUpperRow().size());
        //check that the lower row has only character cards
        for (TribesCard c: game.getLowerRow()){
            assertTrue(c.getCardType().isCharacter());
        }
        //check the upper building row
        assertNotNull(game.getUpperBuildingRow());
        assertFalse(game.getUpperBuildingRow().isEmpty());
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

    @Test
    void testEndRound_Normal(){
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        //the game should have started
        List<TribesCard> oldUpperRow = new ArrayList<>(game.getUpperRow());
        //check that the lower row equals the old upper row
        assertEquals(oldUpperRow.size(),game.getLowerRow().size());
        for(TribesCard c: game.getLowerRow()){
            assertTrue(oldUpperRow.contains(c));
        }
        //check that the new upper row has the right size
        assertEquals(7,game.getUpperRow().size());
    }

    @Test
    void testEndRound_ChangeEra_2(){
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        // play 2 rounds
        //get upper building row value
        List<BuildingCard> oldUpperBuildingRow = new ArrayList<>(game.getUpperBuildingRow());
        assertEquals(1,game.getCurrentEra());
        //play one more round
        // check era changed to 2
        assertEquals(2,game.getCurrentEra());
        // check the building rows
        assertEquals(oldUpperBuildingRow.size(),game.getLowerBuildingRow().size());
        for(BuildingCard c: game.getLowerBuildingRow()){
            assertTrue(oldUpperBuildingRow.contains(c));
        }
        assertNotNull(game.getUpperBuildingRow());
        assertFalse(game.getUpperBuildingRow().isEmpty());
    }

    @Test
    void testEndRound_ChangeEra_3(){
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        //play turns
        //get upper building row value
        List<BuildingCard> oldUpperBuildingRow = new ArrayList<>(game.getUpperBuildingRow());
        assertEquals(2,game.getCurrentEra());
        // check era changed to 3
        assertEquals(3,game.getCurrentEra());
        // check the building rows
        assertEquals(oldUpperBuildingRow.size(),game.getLowerBuildingRow().size());
        for(BuildingCard c: game.getLowerBuildingRow()){
            assertTrue(oldUpperBuildingRow.contains(c));
        }
        assertNotNull(game.getUpperBuildingRow());
        assertFalse(game.getUpperBuildingRow().isEmpty());
    }

    @Test
    void testEndRound_EndGame(){
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        //play turns
        assertEquals(1,game.getCurrentEra());
        assertEquals(2,game.getCurrentEra());
        assertEquals(2,game.getCurrentEra());
        assertEquals(3,game.getCurrentEra());
        assertEquals(3,game.getCurrentEra());
        //check if the game has ended
        assertEquals(-1, game.getCurrentEra());
    }

    // TODO selectOfferingCard
    // TODO selectTribeCards
}