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
    Player myPlayer;
    String nickname;
    boolean inGame;

    public CLI (VirtualServer server, VirtualView client, ClientModel game) {
        this.virtualServer = server;
        this.client = client;
        this.game = game;
        inGame = false;
        myPlayer = null;
        nickname = "";
    }

    public void start() {
        System.out.println("=== Welcome to Mesos ===");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            //ask user for nickname
            setNickname(scanner);
            // ask user for color
            System.out.println("Choose your color");
            Color color = chooseColor(scanner);
            //create new player with nickname and color
            myPlayer = new Player(nickname, color);

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim().toLowerCase();
                //TODO: parsing more flexible?
                switch (input) {
                    case "change nickname":
                        nickname = "";
                        setNickname(scanner);
                        myPlayer.setNickname(nickname);
                        break;
                    case "change color":
                        changeColor(scanner);
                        break;
                    case "create":
                        createGame(scanner);
                        break;
                    case "pick offering card":
                        pickOfferingCard(scanner);
                        break;
                    case "join":
                        joinGame(scanner);
                        break;
                    // TODO: command to select tribe/building cards
                    // TODO: command to view the cards/pp/food of the other players
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
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("- help: shows this menu");
        System.out.println("- exit: closes the application");
        System.out.println("- change color: changes the player's color");
        System.out.println("- join: joins an existing game");
        System.out.println("- create: creates a new game");
        System.out.println("- pick offering card: choose the offering card to take");
    }

    public void printGameId(UUID gameId) {
        System.out.print("\b\b");
        System.out.println("You are connected to game: ".concat(gameId.toString()));
        System.out.print("> ");
    }

    public void drawInterface(ClientModel game)
    {
        try{
            this.game = game;
            //clear console
            clearConsole(); //call method because it was ridondante
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
            System.err.println("CLI error: " + e.getMessage());
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

    /**
     * let player choose new color
     * @param scanner
     */
    private void changeColor(Scanner scanner){
        Color c = chooseColor(scanner);
        myPlayer.setColor(c);
    }

    /**
     * Asks the user to type their nickname and sets it to variable
     * @param scanner
     */
    private void setNickname(Scanner scanner) {
        while (nickname.isEmpty()) {
            System.out.print("Insert your nickname (max 10 char) :> ");
            nickname = scanner.nextLine().trim();
            if(nickname.length() >10 )
            {
                nickname = "";
                System.out.print("Your nickname has more than 10 characters!\n");
            }
        }
    }

    /**
     * send server command to create a game
     * @param scanner
     */
    private void createGame(Scanner scanner){
        try {
            if (!inGame) {
                System.out.print("How many players? (2 to 5) > ");
                int numPlayers = Integer.parseInt(scanner.nextLine());
                System.out.println("trying to create game...");
                // fix something in server, create game should throw an exception if there are errors
                virtualServer.createGame(client, myPlayer, numPlayers);
                inGame = true;
            } else {
                System.out.print("Already in a game \n>");
            }
        }catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * send server command to pick offering card
     * @param scanner
     */
    private void pickOfferingCard(Scanner scanner){
        try {
            System.out.print("Insert card number (position from 0) \n>");
            int numCard = Integer.parseInt(scanner.nextLine()); //TODO: check for errors
            virtualServer.pickOfferingCard(game.id, myPlayer, game.offeringCards.get(numCard));
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * send server command to join game
     * @param scanner
     */
    private void joinGame(Scanner scanner){
        try {
            if (!inGame) {
                game = new ClientModel();
                System.out.print("GameID: ");
                UUID gameId = UUID.fromString(scanner.nextLine()); //TODO: check for errors
                System.out.println("trying to connect");
                virtualServer.joinGame(client, gameId, myPlayer);
            } else {
                System.out.println("Already in a game");
            }
        }catch (Exception e){
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    private void clearConsole(){
        String os =  System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {//sometimes the code in else doesn't work in old windows
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
        else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }//we can also write this method putting the ansiClear first and the fallbackClear
    //after just in case the ansi doesn't work without using if or else o try and catch
    //in this way we don't exclude os windows that support the ansiClear
    //even if in this way we will always do both ansiClear and fallbackClear all the times
    //because ansiClear does not throw any exceptions
    //the cons about not using if/else is that we will do the clear console 2 times,
    //which is redundant (clearing the console twice)
}