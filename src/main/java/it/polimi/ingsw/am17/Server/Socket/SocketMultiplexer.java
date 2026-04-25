package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.Server.Controller.GamesController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Enables multiple socket connections on the server.
 */
public class SocketMultiplexer {
    private final GamesController controller;
    private final List<_ServerSocket> serverSockets;

    public SocketMultiplexer(GamesController controller) {
        this.controller = controller;
        this.serverSockets = new ArrayList<>();
    }

    public static void main(String[] args) {
        SocketMultiplexer server = new SocketMultiplexer(new GamesController());
        server.start(5000);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Socket multiplexer started on port " + port + ". Waiting for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());

                // create a VirtualView associated with the client (socket) to handle updates
                VirtualViewSocket client = new VirtualViewSocket(socket); //TODO

                // create _ServerSocket associated with the client to handle requests
                // and start a thread for it
                _ServerSocket server = new _ServerSocket(socket, controller, client);
                serverSockets.add(server);
                new Thread(server).start();
            }
        } catch (IOException e) {
            System.out.println("Error opening connection: " + e.getMessage());
        }
    }
}
