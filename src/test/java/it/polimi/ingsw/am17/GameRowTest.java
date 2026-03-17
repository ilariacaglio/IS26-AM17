package it.polimi.ingsw.am17;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.smartcardio.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameRowTest {
    GameRow gameRow;

    @BeforeEach
    void setUp(){
        gameRow = new GameRow();
    }

    @Test
    void testShouldRemoveCard(){
        GameCard A = new GameCard(1);
        GameCard B = new GameCard(2);

        List<GameCard> lista = new ArrayList<>();

        gameRow.addCard(A);
        gameRow.addCard(B);

        //all'inizio la lista ha 2 elementi
        assertEquals(2, gameRow.getCards().size());
        gameRow.removeCard(B);
        //ora la lista deve avere 1 elemento
        assertEquals(1, gameRow.getCards().size());
        //assertFalse(gameRow.getCards().contains(B));

    }


}