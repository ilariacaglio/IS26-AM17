package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FullGameSimulationTest {
    @Test
    void fullGameSimulation() { // From Controller POV

//        // Game information for testing
//        Player currentPlayer;
//        int numPlayers = 4;
//
//        // Set up and start the game
//        Game game = new Game(1, numPlayers);
//        // TODO: check nick/color in addPlayer
//        game.addPlayer(new Player("player1", Color.BLACK)); // controller POV
//        game.addPlayer(new Player("player2", Color.RED)); // controller POV
//        game.addPlayer(new Player("player3", Color.YELLOW)); // controller POV
//        game.addPlayer(new Player("player4", Color.WHITE)); // controller POV
//        game.start(); // controller POV
//
//        // ROUNDS
//        while (true) {
//            // 1. Offering card selection
//            // TODO: check offeringCard correctness
//            for (Player player : game.getPlayers()) {
//                Random rand = new Random();
//                OfferingCard offeringCard = game.getOfferingCards().get(rand.nextInt(game.getOfferingCards().size()));
//                System.out.println(player.getNickname() + " chooses offeringCard " + offeringCard.getOrderLetter());
//                player.setOfferingCard(game.getOfferingCards().get(0)); // controller POV
//            }
//
//            // 2. Player actions (pick character cards)
//            for (Player player : game.getPlayers()) {
//                game.selectTribeCards(player, cards); // controller POV
//            }
//        }
    }
}
