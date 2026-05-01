package it.polimi.ingsw.am17.Server;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public class ServerActionMethods {
    /**
     * @return the games id list to the client (unicast)
     * @throws RemoteException
     */
    public static void getGamesList(GamesController controller, VirtualView client) {
        new Thread(()->{
            System.err.println("getGamesList request received");
            try {
                ((VirtualViewRMI)client).updateGamesIdList(controller.getGamesList());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Creates the game with the specified number of players and adds the player to the game
     * @param player
     * @param numPlayers
     * @throws RemoteException
     */
    public static void createGame(GamesController controller, VirtualView client, Player player, int numPlayers) {
        new Thread(()->{
            System.err.println("createGame request received");
            // game id generation
            UUID id = controller.createGame(player, numPlayers);
            // the client signs up as observer for the game
            controller.signUpAsObserver(client, id);
            // send gameId to client
            try {
                ((VirtualViewRMI)client).updateGameId(id);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Adds the player to the game with the specified gameId
     * @param gameId
     * @param player
     * @throws RemoteException
     */
    public static void joinGame(GamesController controller, VirtualView client, UUID gameId, Player player) {
        new Thread(() -> {
            try {
                System.err.println("joinGame request received");
                // Esegue le operazioni di logica
                controller.signUpAsObserver(client, gameId);
                // Notifica il client
                client.updateGameId(gameId);
                controller.joinGame(gameId, player);
            } catch (Exception e) {
                System.err.println("Errore durante la joinGame: " + e.getMessage());
                try {
                    // Remove observer
                    controller.removeClientAsObserver(client, gameId);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    /**
     * Picks the player offering card
     * @param gameId
     * @param player
     * @param card
     * @throws RemoteException
     */
    public static void pickOfferingCard(GamesController controller, UUID gameId, Player player, OfferingCard card){
        new Thread(()->{
            System.err.println("pickOfferingCard request received");
            controller.pickOfferingCard(gameId, player, card);
        }).start();
    }

    /**
     * Picks the player tribe cards
     * @param gameId
     * @param player
     * @param characterCards
     * @param buildingCards
     * @throws RemoteException
     */
    public static void pickTribeCards(GamesController controller, UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        new Thread(()->{System.err.println("pickTribeCards request received");
            controller.pickTribeCards(gameId, player, characterCards, buildingCards);
        }).start();
    }
}
