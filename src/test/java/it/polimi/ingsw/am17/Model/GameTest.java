package it.polimi.ingsw.am17.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        game = new Game(4);
    }

    @Test
    void testGetNextPlayer(){
       //boh
    }

    @Test
    void testAddPlayer(){
        Player p = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        List<Player> players = new ArrayList<>();
        game.addPlayer(p);
        players.add(p);
        assertEquals(1,game.getPlayers().size());
        assertEquals(players,game.getPlayers());
        game.addPlayer(p2);
        players.add(p2);
        assertEquals(2,game.getPlayers().size());
        assertEquals(players,game.getPlayers());
    }

    @Test
    void testGetNextTurn(){

    }

    @Test
    void testEndTurn(){

    }

    @Test
    void testChangeEra(){

    }

    @Test
    void testResolveEvent(){

    }

    @Test
    void testRemoveCardFromRow(){

    }



}

