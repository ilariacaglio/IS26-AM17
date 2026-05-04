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
import it.polimi.ingsw.am17.Server.Utility.MoveValidator;


import java.util.*;

public class CLI implements UI {
    private final VirtualServer virtualServer;
    private final VirtualView client;
    private ClientModel game;

    public CLI (VirtualServer server, VirtualView client, ClientModel game) {
        this.virtualServer = server;
        this.client = client;
        this.game = game;
    }

    /**
     * Starts the cli and collects user commands
     */
    public void start() {
        System.out.println("=== Welcome to Mesos ===");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            // Set up the user
            String nickname = askNickname(scanner);
            Color color = chooseColor(scanner);
            game.createLocalPlayer(nickname, color);

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "get games", "gg":
                        getGamesList();
                        break;
                    case "change nickname", "cn":
                        changeNickname(scanner);
                        break;
                    case "change color", "cc":
                        changeColor(scanner);
                        break;
                    case "create", "c":
                        createGame(scanner);
                        break;
                    case "pick offering card", "po":
                        pickOfferingCard(scanner);
                        break;
                    case "join", "j":
                        joinGame(scanner);
                        break;
                    case "pick cards", "p":
                        pickCards(scanner);
                        break;
                    case "view player", "vp":
                        printPlayer(scanner);
                        break;
                    case "help", "h":
                        printHelp();
                        break;
                    case "exit", "quit", "q":
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
        System.out.println("- help, h: shows this menu");
        System.out.println("- exit, quit, q: closes the application");
        System.out.println("- get games, gg: get the list of starting games");
        System.out.println("- change nickname, cn: changes the player's nickname");
        System.out.println("- change color, cc: changes the player's color");
        System.out.println("- create, c: creates a new game");
        System.out.println("- join, j: joins an existing game");
        System.out.println("- pick offering card, po: choose the offering card to take");
        System.out.println("- pick cards, p: choose the cards to take");
        System.out.println("- view player, vp: shows a player's cards, food, and points");
    }

    /**
     * Asks the user for a player and prints its cards, food, and points.
     */
    private void printPlayer(Scanner scanner) {
        System.out.print("\b\b");
        System.out.print("Insert nickname > ");
        String nickname = scanner.nextLine().trim();

        // search for Player in ordered players
        Player player;
        player = game.getAllPlayers().stream()
                .filter(p->p.getNickname().equals(nickname))
                .findFirst().orElse(null);

        // if player not found print error
        if(player == null) {
            System.out.println("Player " + nickname + " not found!");
        }
        else {
            // print the player
            System.out.println(player);
        }
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
        System.out.println("Open games:");
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
        try{
            // update game data
            this.game = game;
            evaluateGamePhase();
            // cancel arrow
            System.out.print("\b\b");
            //clear console
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
            // TODO: remove this loop for real terminal execution
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }

            // print players list
            printPlayers();
            if(game.getCurrentEra()<1) {
                int playersToWait = game.getNumPlayers()+game.getOrderedPlayers().size();
                System.out.println("Waiting for "+ playersToWait + " more players to join...");
            }

            //draw upper row
            drawRow(true);

            //draw offering card
            drawOfferingCard();

            //draw lower row
            drawRow(false);

            // if the game has begun notify the players turn
            if(game.isPlayerTurn())
                System.out.println("It's your turn!");
            System.out.print("> ");
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints on the terminal the players list
     */
    private void printPlayers() {
        Collection<Player> players;
        if(game.getCurrentEra()<1){
            players = game.getOrderedPlayers();
        }
        else{
            players = game.getAllPlayers();
        }
        System.out.print("\nPlayers: ");
        for(Player p : players) {
            System.out.print("[" + p.getNickname() + " " + p.getFood() + "F " + p.getPp() + "PP" + "] ");
        }
        System.out.println();
    }

    /**
     * Gets the user selected cards and sends them to server
     * @param scanner
     */
    private void pickCards(Scanner scanner) {
        // get players offering card
        OfferingCard myOfferingCard = game.getOfferingCards().stream()
                .filter(c->c.getPlayer()!= null && c.getPlayer().equals(game.getLocalPlayer()))
                .findFirst().orElse(null);

        // if not found, return
        if (myOfferingCard == null) {
            System.out.println("No offering card chosen!");
            return;
        }

        // calculate the number of cards the user can pick
        int totalCards = totalCards = myOfferingCard.getNumCardsUpper()+ myOfferingCard.getNumCardsLower();

        // if card with letter A, no card can be chosen
        if(totalCards == 0) {
            System.out.println("You can't pick any card!");
            return;
        }

        // list of pickable cards
        List<Object> pickableCards = new ArrayList<>();

        // add upper character cards
        pickableCards.addAll(game.getUpperTribeRow().stream()
                .filter(c->c.getCardType().isCharacter()).toList());

        // add upper building cards
        pickableCards.addAll(game.getUpperBuildingRow());

        // add lower character cards
        pickableCards.addAll(game.getLowerTribeRow().stream()
                .filter(c->c.getCardType().isCharacter()).toList());

        // add upper building cards
        if(!game.getLowerBuildingRow().isEmpty())
            pickableCards.addAll(game.getLowerBuildingRow());

        // print the upper row
        int upperPrintIndex = printPickableRow(true,1);
        // print the lower row
        printPickableRow(false, upperPrintIndex);

        // selected cards indexes
        Set<Integer> cardIndexes = new HashSet<>();

        // cards selection
        while (cardIndexes.size() < totalCards) {
            System.out.print("Type the card number (or 'quit' to stop) > ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("quit")) {
                System.out.println("Selection stopped.");
                break;
            }
            try {
                int numCard = Integer.parseInt(input)-1;
                // check if the index is valid
                if (numCard >= 0 && numCard < pickableCards.size()) {
                    if (!cardIndexes.add(numCard)) {
                        // if the set already contains the index print the error
                        System.out.println("Card already selected. Choose a different one.");
                    }
                } else {
                    System.out.println("Index out of bounds!");
                }
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid input, please enter a valid number or 'quit' to stop.");
            }
        }

        // build cards lists
        List<CharacterCard> characterCards = new ArrayList<>();
        List<BuildingCard> buildingCards = new ArrayList<>();
        for(Integer i : cardIndexes) {
            Object pickedCard = pickableCards.get(i);
            try {
                characterCards.add((CharacterCard) pickedCard);
            }
            catch (ClassCastException e) {
                buildingCards.add((BuildingCard) pickedCard);
            }
        }

        //check if move is valid
        Exception mE = MoveValidator.validateCardChoice(myOfferingCard.getNumCardsUpper(), myOfferingCard.getNumCardsLower(),
                characterCards, buildingCards, game.getUpperTribeRow(), game.getLowerTribeRow(), game.getUpperBuildingRow(), game.getLowerBuildingRow());
        if(mE != null)
        {
            drawInterface(game, mE.getMessage());
            return;
        }

        //check if player can buy the buildings
        if(!buildingCards.isEmpty() && !game.getLocalPlayer().canBuyBuidings(buildingCards)) {
            drawInterface(game, "Not enough food to buy building cards");
            return;
        }



        // call server method
        try{
            virtualServer.pickTribeCards(game.getGameId(),game.getLocalPlayer(),characterCards,buildingCards);
        }
        catch (Exception e) {
            System.err.println("CLI error while calling pickTribeCards on the virtualServer: " + e.getMessage());
        }
    }

    /**
     * Prints all the character and building cards in the row
     * @param upper if true prints the upper row, if false prints the lower row
     */
    private int printPickableRow(boolean upper, int startingIndex){
        List<TribesCard> tribeRow;
        List<BuildingCard> buildingRow;
        // set lists basing on upper value
        if(upper){
            tribeRow = game.getUpperTribeRow();
            buildingRow = game.getUpperBuildingRow();
            System.out.print("Upper row:");
        }
        else {
            tribeRow = game.getLowerTribeRow();
            buildingRow = game.getLowerBuildingRow();
            System.out.print("Lower row:");
        }
        // set the starting index for the card display numbering
        int currentIndex = startingIndex;
        // print character cards
        for (TribesCard card : tribeRow) {
            if (card.getCardType().isCharacter()) {
                System.out.print(" " + currentIndex + ") [" + card + "] ");
                // increase number only when the card is printed
                currentIndex++;
            }
        }
        // print building cards
        for (BuildingCard card : buildingRow) {
            System.out.print(" " + currentIndex + ") [" + card + "] ");
            // increase number only when the card is printed
            currentIndex++;
        }
        System.out.print("\n");
        // return the current index for the next print
        return currentIndex;
    }

    /**
     * Prints the list of available colors and
     * lets the user select one of them
     * @param scanner
     * @return  the selected color
     */
    private Color chooseColor(Scanner scanner) {
        Color[] colors = Color.values();

        System.out.println("Choose your color");
        while (true) {
            System.out.println("Available colors:");
            for (int i = 0; i < colors.length; i++) {
                System.out.println((i + 1) + ") " + colors[i]);
            }
            System.out.print("Choose a color number > ");

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
        game.getLocalPlayer().setColor(c);
    }

    /**
     * Asks the user for a new nickname and sets it to player
     * @param scanner
     */
    private void changeNickname(Scanner scanner){
        Player myPlayer = game.getLocalPlayer();
        String nickname = askNickname(scanner);
        myPlayer.setNickname(nickname);
    }

    /**
     * Asks the user to type their nickname
     * @param scanner
     * @return the nickname to be set
     */
    private String askNickname(Scanner scanner) {
        String nickname = "";
        while (nickname.isEmpty()) {
            System.out.print("Insert your nickname (max 10 char) > ");
            nickname = scanner.nextLine().trim();
            if(nickname.length() >10 ) {
                nickname = "";
                System.out.print("Your nickname has more than 10 characters!\n");
            }
        }
        return nickname;
    }

    /**
     * Sends server command to create a game
     * @param scanner
     */
    private void createGame(Scanner scanner){
        try {
            if (game.getGameId() == null) {
                System.out.print("How many players? (2 to 5) > ");
                int numPlayers = Integer.parseInt(scanner.nextLine());
                System.out.println("Trying to create game...");
                virtualServer.createGame(client, game.getLocalPlayer(), numPlayers);
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
            //check if nuber is plausible
            if(numCard<0 || numCard>=game.getOfferingCards().size()){
                drawInterface(game, "number out of bound");
                return;
            }
            //check if card is free
            if(game.getOfferingCards().get(numCard).getPlayer() != null) {
                drawInterface(game, "card already taken");
                return;
            }
            virtualServer.pickOfferingCard(game.getGameId(), game.getLocalPlayer(), game.getOfferingCards().get(numCard));
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
            if (game.getGameId() == null) {
                System.out.print("Insert the gameID > ");
                UUID gameId = UUID.fromString(scanner.nextLine().trim());
                System.out.println("Trying to connect...");
                virtualServer.joinGame(client, gameId, game.getLocalPlayer());
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

    /**
     * Draws the tribe and building row
     * @param upper if true prints the upper row, if false prints the lower row
     */
    private void drawRow(boolean upper){
        List<TribesCard> tribeRow;
        List<BuildingCard> buildingRow;
        if(upper) {
            tribeRow = game.getUpperTribeRow();
            buildingRow = game.getUpperBuildingRow();
        }
        else {
            tribeRow = game.getLowerTribeRow();
            buildingRow = game.getLowerBuildingRow();
        }
        if(!(tribeRow.isEmpty() && buildingRow.isEmpty())){
            if(upper) System.out.print("Upper row:     ");
            else System.out.print("Lower row:     ");

            if(!tribeRow.isEmpty()) {
                for (TribesCard c : tribeRow) {
                    System.out.print("[" + c.toString() + "] ");
                }
            }
            if(!buildingRow.isEmpty()){
                for(BuildingCard c : buildingRow) {
                    System.out.print("[" + c.toString() + "] ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Draws the offering cards list
     */
    private void drawOfferingCard(){
        var offeringCards = game.getOfferingCards();
        if(!offeringCards.isEmpty()){
            System.out.print("Bidding trail: ");
            for(OfferingCard c : offeringCards) {
                System.out.print("[" + c.toString() + "] ");
            }
            System.out.println();
        }
    }

    /**
     * Sets the phase of the game to pick tribe cards if condition met
     */
    private void evaluateGamePhase(){
        if( game.getOrderedPlayers().size() == game.getNumPlayers() &&
            game.everyPlayerInOfferingCard()){
            game.setPickOfferingCardPhase(false);
        }
    }
}