package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.Server.Controller.GamesController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Sets up and starts a manager for SocketServers to handle requests from multiple clients.
 */
public class ServerSocketMultiplexer {
    private final static Logger logger = Logger.getLogger(ServerSocketMultiplexer.class.getName());

    public ServerSocketMultiplexer(GamesController controller, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Socket multiplexer started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("New client connected: " + socket.getRemoteSocketAddress());

                // create a VirtualView associated with the client (socket) to send updates
                VirtualViewSocket client = new VirtualViewSocket(socket);

                // create _ServerSocket associated with the client to receive requests
                ServerSocketSingle server = new ServerSocketSingle(socket, controller, client);
                new Thread(server).start();
            }
        } catch (IOException e) {
            logger.severe("Error opening connection: " + e.getMessage());
        }
    }
}