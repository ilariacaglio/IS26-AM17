package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.RMI.ServerRMI;
import it.polimi.ingsw.am17.Server.Socket.ServerSocketMultiplexer;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Launches both the RMI and Socket servers with the same controller.
 */
public class ServerLauncher {
    private static final Logger logger = Logger.getLogger(ServerLauncher.class.getName());

    /**
     * Launches a server.
     * Use --serverName to specify the server name for RMI.
     * Use --portRMI to specify the port for RMI.
     * Use --portSocket to specify the port for Socket.
     * Use --debug to raise logging level. TODO
     */
    static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);

        String serverName;
        if (argsList.contains("--serverName")) {
            serverName = argsList.get(argsList.indexOf("--serverName") + 1);
        }
        else {
            serverName = "MesosRMIServer";
        }

        int portRMI;
        if (argsList.contains("--portRMI")) {
            portRMI = Integer.parseInt(argsList.get(argsList.indexOf("--portRMI") + 1));
        }
        else {
            portRMI = 1099;
        }

        int portSocket;
        if (argsList.contains("--portSocket")) {
            portSocket = Integer.parseInt(argsList.get(argsList.indexOf("--portSocket") + 1));
        }
        else {
            portSocket = 24312;
        }

        // create the main controller
        GamesController controller = new GamesController();

        // Start an RMI server in its own thread
        new Thread(() -> {
            try {
                new ServerRMI(controller, portRMI, serverName);
            } catch (Exception e) {
                logger.severe("RMI server failed to start: " + e.getMessage());
            }
        }).start();

        // Start a Socket server in its own thread
        new Thread(() -> {
            try {
                new ServerSocketMultiplexer(controller, portSocket);
            } catch (Exception e) {
                logger.severe("Socket server failed to start: " + e.getMessage());
            }
        }).start();


    }
}
