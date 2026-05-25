package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.RMI.ServerRMI;
import it.polimi.ingsw.am17.Server.Socket.ServerSocketMultiplexer;

import java.util.logging.Logger;

/**
 * Launches both the RMI and Socket servers with the same controller.
 */
public class ServerLauncher {
    private static final Logger logger = Logger.getLogger(ServerLauncher.class.getName());

    static void main(String[] args) {
        // create the main controller
        GamesController controller = new GamesController();

        // Start an RMI server in its own thread
        new Thread(() -> {
            try {
                new ServerRMI(controller, 1099, "MesosRMIServer");
            } catch (Exception e) {
                logger.severe("RMI server failed to start: " + e.getMessage());
            }
        }).start();

        // Start a Socket server in its own thread
        new Thread(() -> {
            try {
                new ServerSocketMultiplexer(controller, 5000);
            } catch (Exception e) {
                logger.severe("Socket server failed to start: " + e.getMessage());
            }
        }).start();


    }
}
