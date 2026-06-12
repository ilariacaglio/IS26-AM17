package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.Server.Controller.ControllerInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Sets up and starts a manager for SocketServers to handle requests from multiple clients.
 */
public class ServerSocketMultiplexer {
    private final static Logger logger = Logger.getLogger(ServerSocketMultiplexer.class.getName());
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    /**
     * Creates a new ServerSocketMultiplexer and starts it.
     * @param controller passed to each ServerSocketSingle.
     * @param port port to bind the ServerSocket to.
     */
    public ServerSocketMultiplexer(ControllerInterface controller, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Socket multiplexer started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("New client connected: " + socket.getRemoteSocketAddress());

                // create a VirtualClient associated with the client (socket) to send updates
                VirtualClientSocket client = new VirtualClientSocket(socket);

                // create _ServerSocket associated with the client to receive requests
                ServerSocketSingle server = new ServerSocketSingle(socket, controller, client);
                clientPool.execute(server);
            }
        } catch (IOException e) {
            logger.severe("Error opening connection: " + e.getMessage());
        }
    }
}