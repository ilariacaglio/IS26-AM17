package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullGameSimulationTest {
    Game game;
    @Test
    void fullGameSimulation() {
        Player currentPlayer;
        int roundNumber = 1;
        int maxRoundNumber = 10;
        int numPlayers = 4;
        int pos;
        game = new Game(numPlayers);
        //add players to game
        game.addPlayer(new Player("player1",Color.BLACK));
        game.addPlayer(new Player("player2",Color.RED));
        game.addPlayer(new Player("player3",Color.YELLOW));
        game.addPlayer(new Player("player4",Color.WHITE));
        //start game
        game.start();
        //turn one
        //the players pick an offering card basing on the players list order
        for(int i = 0; i < numPlayers; i++) {
            game.getPlayers().get(i).setOfferingCard(game.getOfferingCards().get(i));
        }
        while(roundNumber <= maxRoundNumber) {
            //the players play their turn by choosing a card from the game rows
            for (int i = 0; i < numPlayers; i++){
                List<TribesCard> lowerCards = game.getLowerRow().getCards();
                List<TribesCard> upperCards = game.getUpperRow().getCards();
                currentPlayer = game.getNextTurn();
                switch (i) {
                    case 0:
                        //pick one card from the lower row
                        currentPlayer.playTurn(getCharacterCards(lowerCards, 1), Collections.emptyList(), game);
                        break;
                    case 1:
                        //pick one card from the upper row
                        currentPlayer.playTurn(getCharacterCards(upperCards, 1), Collections.emptyList(), game);
                        break;
                    case 2:
                        //pick two cards from the lower row
                        currentPlayer.playTurn(getCharacterCards(lowerCards, 2), Collections.emptyList(), game);
                        break;
                    case 3:
                        //pick one card from the lower row and one card from the upper row
                        List<TribesCard> list = new ArrayList<>();
                        list.addAll(getCharacterCards(lowerCards, 1));
                        list.addAll(getCharacterCards(upperCards, 1));
                        currentPlayer.playTurn(list, Collections.emptyList(), game);
                        break;
                }
            }
            //resolve events
            game.resolveEvent();
            if (roundNumber != maxRoundNumber) {
                //end of the turn
                game.endTurn();
                //the players pick an offering card basing on the offering cards order
                for(int i = 0; i < numPlayers; i++) {
                    game.getNextPlayer().setOfferingCard(game.getOfferingCards().get(i));
                }
            }
            else {
                //end game
                game.end();
            }
            roundNumber++;
        }
    }
    private List<TribesCard> getCharacterCards(List<TribesCard> cards, int amount) {
        return cards.stream()
                .filter(card -> card.getCardType().isCharacter())
                .limit(amount)
                .toList();
    }
}
