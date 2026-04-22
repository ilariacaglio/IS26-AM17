package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.Scanner;
import java.util.UUID;

public class CLI implements UI {
    private final VirtualServer virtualServer;
    private final VirtualView client;
    private ClientModel game;
    Player myPlayer = null;
    String nickname = "";
    boolean inGame = false;

    public CLI (VirtualServer server, VirtualView client, ClientModel game) {
        this.virtualServer = server;
        this.client = client;
        this.game = game;
    }

    public void start() {
        System.out.println("=== Welcome to Mesos ===");


        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            nickname = "";
            while (nickname.isEmpty()) {
                System.out.print("Insert your nickname (max 10 char): \n>");
                nickname = scanner.nextLine().trim();
                if(nickname.length() >=10 )
                {
                    System.out.print("your nickname has more then 10 character \n>");
                }
            }

            //TODO: pick color

            myPlayer = new Player(nickname, Color.BLACK);

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "change color":
                        Color c = chooseColor(scanner);
                        myPlayer.setColor(c);
                        break;
                    case "create":
                        if(!inGame) {
                            System.out.print("Number of player? \n>");
                            int numPlayers = Integer.parseInt(scanner.nextLine()); //TODO: check for errors
                            System.out.println("trying to create game");
                            virtualServer.createGame(client, myPlayer, numPlayers);
                            inGame = true;
                        }else
                        {
                            System.out.print("Already in a game \n>");
                        }
                        break;
                    case "pick offering card":
                        System.out.print("insert card number (position from 0) \n>");
                        int numCard = Integer.parseInt(scanner.nextLine()); //TODO: check for errors
                        virtualServer.pickOfferingCard(game.id, myPlayer, game.offeringCards.get(numCard));
                        break;
                    case "join":
                        if(!inGame) {
                            game = new ClientModel();
                            System.out.print("GameID: ");
                            UUID gameId = UUID.fromString(scanner.nextLine()); //TODO: check for errors
                            System.out.println("trying to connect");
                            virtualServer.joinGame(client, gameId, myPlayer);
                        }else{
                            System.out.println("Already in a game");
                        }
                        break;

                    case "help":
                        printHelp();
                        break;
                    case "quit":
                    case "exit":
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Command not recognized. Please type 'help' to view the list of available commands.");
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

    public void printGameId(UUID gameId) {
        System.out.println("You are connected to game: ".concat(gameId.toString()));
    }

    public void drawInterface(ClientModel game)
    {
        try{
            this.game = game;
            //clear console
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
            System.out.print("\033[H\033[2J");
            System.out.flush();
            //draw players
            System.out.print("Players: ");
            for(Player p : game.orderedPlayer)
            {
                System.out.print(p.getNickname().concat(" "));
            }
            System.out.println();

            //draw upper deck
            System.out.print("Upper deck: ");
            for(TribesCard c : game.upperRow)
            {
                System.out.print(c.toString().concat(" "));
            }
            System.out.println();

            //draw lower deck
            System.out.print("Lower deck: ");
            for(TribesCard c : game.lowerRow)
            {
                System.out.print(c.toString().concat(" "));
            }
            System.out.println();
            System.out.println();

            //draw offering card
            System.out.print("Offering card: ");
            for(OfferingCard c : game.offeringCards)
            {
                System.out.print(c.toString().concat(" "));
            }
            System.out.println();
            if(game.orderedPlayer.peek().equals(myPlayer))
                System.out.println("it's your turn");
        } catch (Exception e) {
            System.err.println("Errore nel CLI: " + e.getMessage());
        }
    }


    private Color chooseColor(Scanner scanner) {
        Color[] colors = Color.values();

        while (true) {
            System.out.println("Available colors:");
            for (int i = 0; i < colors.length; i++) {
                System.out.println((i + 1) + ") " + colors[i]);
            }
            System.out.print("Choose a color number: ");

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < colors.length) {
                    return colors[choice];
                } else {
                    System.out.println("Invalid number. Please choose between 1 and " + colors.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number, not text.");
            }
        }
    }
}