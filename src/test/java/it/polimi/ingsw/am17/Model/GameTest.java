package it.polimi.ingsw.am17.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        game = new Game(3);
    }

    //to do
    @Test
    void testGetNextPlayer(){

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
    void testStart(){
        game.start();
        //check if the game has started
        assertEquals(game.isStarted(),true);
        //controlla currentEra
        //size lower row
        //size upper row
        //size upper building row
    }

    //to do
    @Test
    void testGetNextTurn(){
        // get current turn letter
        //call method
        // verifiy that the player returned has the new current letter
        // check exception
    }

    //to do
    @Test
    void testEndTurn(){
        //nuova lower row uguale a vecchia upper row
        // check size nuova upper row
        // vedere se cambia era???????
    }

    @Test
    void testChangeEra(){


    }

    @Test
    void testRemoveCardFromRow(){


    }

    @Test
    void testResolveEvent(){

    }

}

