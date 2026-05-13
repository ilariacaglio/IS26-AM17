package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.Client.Socket.ClientSocket;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.Server.Controller.GamesController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Enables multiple socket connections on the server.
 */
public class SocketMultiplexer {
    private final GamesController controller;
    private final List<_ServerSocket> serverSockets;
    private final static Logger logger = Logger.getLogger(SocketMultiplexer.class.getName());

    public SocketMultiplexer(GamesController controller) {
        this.controller = controller;
        this.serverSockets = new ArrayList<>();
    }

    public static void start(GamesController controller) {
        SocketMultiplexer server = new SocketMultiplexer(controller);
        server.start(5000);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Socket multiplexer started on port " + port + ". Waiting for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("New client connected: " + socket.getRemoteSocketAddress());

                // create a VirtualView associated with the client (socket) to handle updates
                VirtualViewSocket client = new VirtualViewSocket(socket); //TODO

                // create _ServerSocket associated with the client to handle requests
                // and start a thread for it
                _ServerSocket server = new _ServerSocket(socket, controller, client);
                serverSockets.add(server);
                new Thread(server).start();

                // create a heartbeat thread
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate((pinger(socket, client, server)), 1, 1, TimeUnit.SECONDS);

            }
        } catch (IOException e) {
            logger.severe("Error opening connection: " + e.getMessage());
        }
    }

    private Runnable pinger(Socket socket, VirtualViewSocket client, _ServerSocket server) {
        return () -> {
            try {
                logger.finer("Sending heartbeat to socket: " + socket.getRemoteSocketAddress());
                new Message(MessageType.HEARTBEAT).send(socket); // this is not actually handled
            } catch (Exception e) {
                logger.severe("Socket client disconnected! (Failed heartbeat: " + e.getMessage() + ") Was at: " + socket.getRemoteSocketAddress());
                controller.closeGame(client);
                serverSockets.remove(server);
            }
        };
    }
}