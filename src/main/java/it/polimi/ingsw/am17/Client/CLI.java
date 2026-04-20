package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Model.*;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;

import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Stack;
import java.util.UUID;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

public class CLI extends UnicastRemoteObject implements VirtualViewRMI {

    private VirtualServerRMI virtualServer;
    Player myPlayer = null;
    GameClient game;
    static CLI cli;
    boolean inGame = game != null;
    String nickname = "";

    protected CLI() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            cli = new CLI();
            cli.startCLI();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void startCLI() {
        System.out.println("=== Benvenuto a Mesos ===");


        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            String nickname = "";
            while (nickname.isEmpty()) {
                System.out.print("Inserisci il tuo nickname: \n>");
                nickname = scanner.nextLine().trim();
            }

            myPlayer = new Player(nickname, Color.BLACK);

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "connect":
                        System.out.println("Insert Server ip");
                        String ip = scanner.nextLine();

                        if (ip.isEmpty()) {
                            ip = "127.0.0.1";
                            System.out.println("Ip set to localhost");
                        }

                        connect(ip);

                        break;
                    case "create":
                        if(!inGame) {
                            System.out.print("Quanti giocatori vuoi? \n>");
                            int numPlayers = Integer.parseInt(scanner.nextLine()); //TODO: check for errors
                            System.out.println("trying to create game");
                            virtualServer.createGame(this, myPlayer, numPlayers);

                            game = new GameClient(); //TODO: fix
                            game.numPlayers = numPlayers;
                        }else
                        {
                            System.out.print("Già in partita \n>");
                        }
                        break;

                    case "join":
                        if(!inGame) {
                            System.out.println("GameID: ");
                            UUID gameId = UUID.fromString(scanner.nextLine()); //TODO: check for errors
                            System.out.println("trying to create game");
                            virtualServer.joinGame(this, gameId, myPlayer);
                        }else{
                            System.out.println("Già in game");
                        }
                        break;

                    case "help":
                        printHelp();
                        break;
                    case "quit":
                    case "exit":
                        System.out.println("Arrivederci!");
                        running = false;
                        break;
                    default:
                        System.out.println("Comando non riconosciuto. Scrivi 'help' per la lista comandi.");
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nel CLI: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Comandi disponibili:");
        System.out.println("- help: mostra questo menu");
        System.out.println("- exit: chiude l'applicazione");
    }

    public void connect(String ip) {
        try {
            // Locate the registry on the server's IP and default port 1099
            Registry registry = LocateRegistry.getRegistry(ip, 1099);
            // Look up the object by the name it was bound to
            virtualServer = (VirtualServerRMI) registry.lookup("MesosRMIServer");
            System.out.println("Connected to server via RMI!");
        }catch (RemoteException e) {
            System.err.println("Network error: The server seems to be offline.");
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("Service error: The server is running but the game service isn't registered.");
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    // TODO
    public void updateEra(int era) {
        // call model to update era
        game.currentEra = era;

        //notify user
    }

    // TODO
    public void updatePlayerStack(Stack<Player> orderedPlayer) {
        game.orderedPlayer = orderedPlayer;

        //notify user
    }

    // TODO
    public void updateOfferingCards(List<OfferingCard> offeringCards) {
        game.offeringCards = offeringCards;
        drawInterface();
        //notify user
    }

    // TODO
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) {
        game.upperRow = upperRow;
        game.lowerRow = lowerRow;

        //notify user
    }

    // TODO
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        game.upperBuildingRow = upperBuildingRow;
        game.lowerBuildingRow = lowerBuildingRow;

        drawInterface();
        //notify user
    }

    public void updateGameId(UUID gameId) throws Exception
    {
        game.id = gameId;

        //notify user
    }

    private void drawInterface()
    {

    }

}