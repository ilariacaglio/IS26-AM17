package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.RMI.ServerRMI;
import it.polimi.ingsw.am17.Server.Socket.SocketMultiplexer;
import javafx.application.Application;

public class ServerLauncher {
    public static void main(String[] args) {
        GamesController controller = new GamesController();
        // Start RMI server in its own thread
        new Thread(() -> {
            try {
                ServerRMI.start(controller);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Start Socket server in its own thread
        new Thread(() -> {
            try {
                SocketMultiplexer.start(controller);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


    }
}
