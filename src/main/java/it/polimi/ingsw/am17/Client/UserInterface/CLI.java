package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.*;

public class CLI implements UI {
    private final VirtualServer virtualServer;
    private final VirtualView client;
    private ClientModel game;
    Player myPlayer;
    String nickname;
    OfferingCard myOfferingCard;
    boolean inGame;

    public CLI (VirtualServer server, VirtualView client, ClientModel game) {
        this.virtualServer = server;
        this.client = client;
        this.game = game;
        inGame = false;
        myPlayer = null;
        nickname = "";
        myOfferingCard = null;
    }

    /**
     * Starts the cli and collects user commands
     */
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
                switch (input) {
                    case "get games":
                        getGamesList();
                        break;
                    case "change nickname":
                        changeNickname(scanner);
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
                    case "pick cards":
                        pickCards(scanner);
                        break;
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

    /**
     * Prints the commands list
     */
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("- help: shows this menu");
        System.out.println("- exit: closes the application");
        System.out.println("- change color: changes the player's color");
        System.out.println("- change nickname: changes the player's nickname");
        System.out.println("- get games: get the list of incomplete games");
        System.out.println("- create: creates a new game");
        System.out.println("- join: joins an existing game");
        System.out.println("- pick offering card: choose the offering card to take");
        System.out.println("- pick cards: choose the cards to take");
    }

    /**
     * Prints the gameId on the terminal.
     * @param gameId the id to be printed
     */
    public void printGameId(UUID gameId) {
        System.out.print("\b\b");
        System.out.println("You are connected to game: ".concat(gameId.toString()));
        System.out.print("> ");
    }

    /**
     * Prints the games id list.
     */
    public void printGamesList(){
        System.out.print("\b\b");
        System.out.println("Incomplete games:");
        for(int i=0; i< game.getGamesIdList().size(); i++){
            System.out.println(i+"\t"+game.getGamesIdList().get(i));
        }
        System.out.print("> ");
    }

    /**
     * Sends the get id list request to server.
     */
    private void getGamesList(){
        try {
            virtualServer.getGamesList(client);
        } catch (Exception e) {
            System.out.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Draws the game configuration.
     * @param game  the model to be drawn.
     */
    public void drawInterface(ClientModel game)
    {
        // TODO: manca la stampa dei buildings!!
        // TODO: spezzare stampe nei metodi
        try{
            this.game = game;
            // cancel arrow
            System.out.print("\b\b");
            //clear console
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
            // TODO: remove this loop for real terminal execution
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }

            //draw players
            if(!inGame) {
                int playersToWait = game.getNumPlayers()+game.getOrderedPlayers().size();
                printPlayers();
                System.out.println("\nWaiting for "+ playersToWait + " more players to join...");
            }

            //draw upper row
            List<TribesCard> upperRow = game.getUpperTribeRow();
            if(!upperRow.isEmpty()){
                System.out.print("Upper row: ");
                for(TribesCard c : upperRow) {
                    System.out.print(c.toString().concat(" "));
                }
                System.out.println();
            }

            //draw lower row
            List<TribesCard> lowerRow = game.getLowerTribeRow();
            if(!lowerRow.isEmpty()){
                System.out.print("Lower row: ");
                for(TribesCard c : lowerRow) {
                    System.out.print(c.toString().concat(" "));
                }
                System.out.println();
            }

            //draw offering card
            List<OfferingCard> offeringCards = game.getOfferingCards();
            if(!offeringCards.isEmpty()){
                System.out.print("Offering card: ");
                for(OfferingCard c : offeringCards) {
                    System.out.print(c.toString().concat(" "));
                }
                System.out.println();
            }

            // if the game has begun notify the players turn
            if(!offeringCards.isEmpty()){
                if(game.getCurrentPlayer().equals(myPlayer))
                    System.out.println("It's your turn!");
            }
            System.out.print("> ");
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints on the terminal the players list
     */
    private void printPlayers() {
        Stack<Player> orderedPlayer = game.getOrderedPlayers();
        System.out.print("\nPlayers: ");
        for(Player p : orderedPlayer) {
            System.out.print(p.getNickname().concat(" "));
        }
    }

    /**
     * Gets the user selected cards and sends them to server
     * @param scanner
     */
    private void pickCards(Scanner scanner) {
        // TODO: improve offering card
        int totalCards = myOfferingCard.getNumCardsUpper() + myOfferingCard.getNumCardsLower();

        // get game rows
        var upperTRow = game.getUpperTribeRow();
        var lowerTRow = game.getLowerTribeRow();
        var upperBRow = game.getUpperBuildingRow();
        var lowerBRow = game.getLowerBuildingRow();

        // calculate rows sizes
        int sizeUpperTribeRow = upperTRow.size();
        int sizeLowerTribeRow = lowerTRow.size();
        int sizeUpperBuildingRow = upperBRow.size();
        int sizeLowerBuildingRow = lowerBRow.size();
        int sizeUpperRow = sizeUpperTribeRow + sizeUpperBuildingRow;
        int sizeLowerRow = sizeLowerTribeRow + sizeLowerBuildingRow;

        // selected cards indexes
        List<Integer> cardIndexes = new ArrayList<>();
        // print the upper row
        printPickableUpperRow();
        // print the lower row
        printPickableLowerRow();
        // cards selection
        while (cardIndexes.size() < totalCards) {
            System.out.print("Type the card number >");
            int numCard = Integer.parseInt(scanner.nextLine());
            if (numCard >= 0 && numCard <= sizeUpperRow+sizeLowerRow) {
                cardIndexes.add(numCard);
            }
            System.out.println();
        }

        // build cards lists
        List<CharacterCard> characterCards = new ArrayList<>();
        List<BuildingCard> buildingCards = new ArrayList<>();
        for(Integer i : cardIndexes) {
            if(i< sizeUpperTribeRow) {
                characterCards.add((CharacterCard) upperTRow.get(i));
            }
            else if (i < sizeUpperRow) {
                buildingCards.add(upperBRow.get(i - sizeUpperTribeRow));
            }
            else if (i < sizeUpperRow + sizeLowerTribeRow) {
                characterCards.add((CharacterCard) lowerTRow.get(i -sizeUpperRow));
            }
            else{
                buildingCards.add(lowerBRow.get(i-sizeUpperRow-sizeLowerTribeRow));
            }
        }

        // call server method
        try{
            virtualServer.pickTribeCards(game.getGameId(),myPlayer,characterCards,buildingCards);
        }
        catch (Exception e) {}
    }

    /**
     * Prints all the character and building cards in the upper row
     */
    private void printPickableUpperRow(){
        int span = game.getUpperTribeRow().size();
        System.out.print("Upper row:");
        // print character cards
        for(int i=0; i< span; i++){
            if(game.getUpperTribeRow().get(i).getCardType().isCharacter()){
                System.out.print(i + " " + game.getUpperTribeRow().get(i) + "\t");
            }
        }
        // print building cards
        for(int i=0; i< game.getUpperBuildingRow().size(); i++){
            System.out.print((i+span) + " " + game.getUpperBuildingRow().get(i) + "\t");
        }
        System.out.print("\n");
    }

    /**
     * Prints all the character and building cards in the lower row
     */
    private void printPickableLowerRow(){
        int span = game.getUpperTribeRow().size()+game.getUpperBuildingRow().size();
        int buildingSpan = span + game.getLowerTribeRow().size();
        System.out.print("Lower row:");
        // print character cards
        for(int i=0; i< game.getLowerTribeRow().size(); i++){
            if(game.getLowerTribeRow().get(i).getCardType().isCharacter()){
                System.out.print((i+span) + " " + game.getLowerTribeRow().get(i) + "\t");
            }
        }
        // print building cards
        for(int i=0; i< game.getLowerBuildingRow().size(); i++){
            System.out.print((i+buildingSpan) + " " + game.getUpperBuildingRow().get(i) + "\t");
        }
        System.out.print("\n");
    }

    /**
     * Prints the list of available colors and
     * lets the user select one of them
     * @param scanner
     * @return  the selected color
     */
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
     * Lets player choose new color
     * @param scanner
     */
    private void changeColor(Scanner scanner){
        Color c = chooseColor(scanner);
        myPlayer.setColor(c);
    }

    /**
     * Asks the user for a new nickname and sets it to player
     * @param scanner
     */
    private void changeNickname(Scanner scanner){
        nickname = "";
        setNickname(scanner);
        myPlayer.setNickname(nickname);
    }

    /**
     * Asks the user to type their nickname and sets it to variable
     * @param scanner
     */
    private void setNickname(Scanner scanner) {
        while (nickname.isEmpty()) {
            System.out.print("Insert your nickname (max 10 char) > ");
            nickname = scanner.nextLine().trim();
            if(nickname.length() >10 ) {
                nickname = "";
                System.out.print("Your nickname has more than 10 characters!\n");
            }
        }
    }

    /**
     * Sends server command to create a game
     * @param scanner
     */
    private void createGame(Scanner scanner){
        try {
            if (!inGame) {
                System.out.print("How many players? (2 to 5) > ");
                int numPlayers = Integer.parseInt(scanner.nextLine());
                System.out.println("Trying to create game...");
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
     * Sends server command to pick offering card
     * @param scanner
     */
    private void pickOfferingCard(Scanner scanner){
        try {
            System.out.print("Insert card number (position from 0) > ");
            int numCard = Integer.parseInt(scanner.nextLine());
            virtualServer.pickOfferingCard(game.getGameId(), myPlayer, game.getOfferingCards().get(numCard));
            myOfferingCard = game.getOfferingCards().get(numCard);
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Sends server command to join game
     * @param scanner
     */
    private void joinGame(Scanner scanner){
        try {
            if (!inGame) {
                game = new ClientModel();
                System.out.print("Insert the gameID > ");
                UUID gameId = UUID.fromString(scanner.nextLine().trim());
                System.out.println("trying to connect...");
                virtualServer.joinGame(client, gameId, myPlayer);
            } else {
                System.out.println("Already in a game!");
            }
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints message on the terminal to notify the user that the new era has begun.
     */
    public void printEra(){
        if(game.getCurrentEra() > 1) {
            System.out.println("\nEra "+game.getCurrentEra()+ " has begun!\n");
        }
    }
}