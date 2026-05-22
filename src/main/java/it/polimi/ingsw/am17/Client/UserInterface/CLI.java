package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.GameCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Utility.MoveValidator;

import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import it.polimi.ingsw.am17.Server.Utility.TurnFoodHandler;

import java.util.*;

public class CLI implements UI {
    private final VirtualServer virtualServer;
    private final VirtualView client;
    private ClientModel readOnlyModel;
    private Player localPlayer;
    Scanner scanner;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public CLI (VirtualServer server, VirtualView client) {
        this.virtualServer = server;
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void setModel(ClientModel model) {
        this.readOnlyModel = model;
    }

    /**
     * Starts the cli and collects user commands
     */
    public void start() {
        System.out.println("=== Welcome to Mesos ===");

        try {
            boolean running = true;

            // Set up the user
            String nickname = askNickname();
            Color color = chooseColor();
            localPlayer = new Player(nickname, color);

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    case "get games", "gg":
                        getGamesList();
                        break;
                    case "change nickname", "cn":
                        changeNickname();
                        break;
                    case "change color", "cc":
                        changeColor();
                        break;
                    case "create", "c":
                        createGame();
                        break;
                    case "close game", "xxx":
                        closeGame();
                        break;
                    case "pick offering card", "po":
                        pickOfferingCard();
                        break;
                    case "join", "j":
                        joinGame();
                        break;
                    case "pick cards", "p":
                        pickCards();
                        break;
                    case "view player", "vp":
                        printPlayer();
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
            // exit
            System.exit(0);

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
        System.out.println("- close game, xxx: closes the current game");
        System.out.println("- join, j: joins an existing game");
        System.out.println("- pick offering card, po: choose the offering card to take");
        System.out.println("- pick cards, p: choose the cards to take");
        System.out.println("- view player, vp: shows a player's cards, food, and points");
    }

    /**
     * Asks the user for a player and prints its cards, food, and points.
     */
    private void printPlayer() {
        System.out.print("\b\b");
        System.out.print("Insert nickname > ");
        String nickname = scanner.nextLine().trim();

        // search for Player in ordered players
        Player player = readOnlyModel.getOrderedPlayers().stream()
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
        for(int i=0; i< readOnlyModel.getGamesIdList().size(); i++){
            System.out.println(i + "\t" + readOnlyModel.getGamesIdList().get(i));
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
     * @param errorMessage message you want to print
     */
    public void drawInterface(String errorMessage)
    {
        try{
            // cancel arrow
            System.out.print("\b\b");
            //clear console
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
            // TODO: remove this loop for real terminal execution
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }

            if(errorMessage != null && !errorMessage.isBlank()) {
                System.out.println(ANSI_RED+errorMessage+ANSI_RESET);
                System.out.flush(); //ensure error message is before the interface
            }

            GameState gameState = readOnlyModel.getGameState();

            if (gameState.isInLobbyOrStarted()) {
                // print players list
                printPlayers();
                if(gameState.isInLobby()){
                    System.out.println("Waiting for more players to join...");
                }
                else {
                    printTurnOrder();

                    //draw upper row
                    drawRow(true);

                    //draw offering card
                    drawOfferingCard();

                    //draw lower row
                    drawRow(false);

                    // print the cards of the player
                    drawLocalPlayer();

                    // if the game has begun notify the players turn
                    if(readOnlyModel.isPlayerTurn())
                        System.out.println("It's your turn!");
                    if(errorMessage == null || errorMessage.isBlank())
                        System.out.print("> ");
                }
            }
            else if (gameState.isGameEnded()) {
                drawLocalRanking();
                drawGlobalRanking();
                System.out.print("> ");
            }
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * prints the cards of the local player
     */
    private void drawLocalPlayer() {
        System.out.print("You:");
        String cards = localPlayer.playerCharacterCardstoString(15) +
                localPlayer.playerBuildingCardsString(15);
        if(cards.isEmpty()) {
            cards = "           no cards yet";
        }
        System.out.println(cards);
    }

    /**
     * prints the ranking of the player in global ranking when game ends
     */
    private void drawGlobalRanking(){
        List<RankingEntry> ranking = readOnlyModel.getRanking();
        if (!ranking.isEmpty()) {
            System.out.println("\n--- YOUR POSITION IN GLOBAL RANKING ---");
            RankingEntry userEntry = ranking.stream()
                    .filter(e -> e.getGameId().equals(readOnlyModel.getGameId())
                            && e.getNickname().equals(localPlayer.getNickname()))
                    .findFirst().orElse(null);
            if (userEntry != null) {
                int pos = ranking.indexOf(userEntry) + 1;
                System.out.println(pos + ")\t"+userEntry.getNickname()+"\t"+ userEntry.getFinalPoints());
            }
            else {
                System.out.println("Player data not found!");
            }

            System.out.println("\n--- GLOBAL RANKING ---");
            System.out.printf("N.\t%-12s\t%-15s\t%s%n", "DATA", "NICKNAME", "SCORE");
            int rank = 1;
            for (RankingEntry entry: readOnlyModel.getRanking()) {
                System.out.println(rank + ")\t" + entry);
                rank++;
            }
        }
    }

    /**
     * prints the ranking of the local players when game ends
     */
    private void drawLocalRanking() {
        System.out.println("--- FINAL GAME RANKING ---");
        List<Player> sortedPlayers = readOnlyModel.getOrderedPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getPp).reversed())
                .toList();
        int rank = 1;
        for (Player player : sortedPlayers) {
            System.out.println(rank + "° place: " + player.getNickname() + " - Points: " + player.getPp());
            rank++;
        }
    }

    /**
     * Prints the turn order card with players
     */
    private void printTurnOrder() {
        // get the list of the players in offering cards
        List<Player> playersInOfferingCard = readOnlyModel.getOfferingCards().stream()
                .map(OfferingCard::getPlayer).toList();

        // get the list of the players to print in turn order card
        List<Player> playersToPrint = readOnlyModel.getOrderedPlayers().stream()
                .filter(player -> !playersInOfferingCard.contains(player))
                .toList();

        int[] turnFood = TurnFoodHandler.getTurnFoodPoints(readOnlyModel.getNumPlayers());

        // calculate offset basing on game phase
        int offset = readOnlyModel.isPickOCPhase() ? turnFood.length - playersToPrint.size() : 0;

        // print
        System.out.print("Turn order:    ");
        for (int i = 0; i < readOnlyModel.getNumPlayers(); i++) {
            // choose if print nickname or not
            String nickname = " ";
            if (i >= offset && i-offset < playersToPrint.size())
                nickname = playersToPrint.get(i-offset).getNickname();
            // choose if it is the last cell
            boolean isLast = (i == readOnlyModel.getNumPlayers() - 1);
            // get foodBonus value and build string
            String foodBonus = String.format("%+dF", turnFood[i]) + (isLast ? "/-2PP" : "");
            // get string separator
            String separator = isLast ? "\n" : "\t";
            // build string and print
            System.out.print("[(" + nickname + ") " + foodBonus + "]" + separator);
        }
    }

    /**
     * Prints on the terminal the players list
     */
    private void printPlayers() {
        Collection<Player> players = readOnlyModel.getOrderedPlayers();
        System.out.print("\nPlayers:       ");
        for(Player p : players) {
            System.out.print("[" + p.getNickname() + " " + p.getFood() + "F " + p.getPp() + "PP" + "] ");
        }
        System.out.println();
    }

    /**
     * Gets the user selected cards and sends them to server
     */
    private void pickCards() {
        // get players offering card
        OfferingCard myOfferingCard = readOnlyModel.getOfferingCards().stream()
                .filter(c->c.getPlayer()!= null && c.getPlayer().equals(localPlayer))
                .findFirst().orElse(null);

        // if not found, return
        if (myOfferingCard == null) {
            System.out.println("No offering card chosen!");
            return;
        }

        // calculate the number of cards the user can pick
        int totalCards = myOfferingCard.getNumCardsUpper()+ myOfferingCard.getNumCardsLower();

        // if card with letter A, no card can be chosen
        if(totalCards == 0) {
            System.out.println("You can't pick any card!");
            return;
        }

        System.out.println("You can pick " + myOfferingCard.getNumCardsUpper() + " card from upper row and "
        + myOfferingCard.getNumCardsLower() +" card from lower row");

        // list of pickable cards
        List<GameCard> pickableCards = new ArrayList<>();

        // add upper character cards
        pickableCards.addAll(readOnlyModel.getUpperTribeRow().stream()
                .filter(c->c.getCardType().isCharacter()).toList());

        // add upper building cards
        pickableCards.addAll(readOnlyModel.getUpperBuildingRow());

        // add lower character cards
        pickableCards.addAll(readOnlyModel.getLowerTribeRow().stream()
                .filter(c->c.getCardType().isCharacter()).toList());

        // add upper building cards
        if(!readOnlyModel.getLowerBuildingRow().isEmpty())
            pickableCards.addAll(readOnlyModel.getLowerBuildingRow());

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
                        System.err.println("Card already selected. Choose a different one.");
                    }
                } else {
                    System.err.println("Index out of bounds!");
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
            GameCard pickedCard = pickableCards.get(i);
            if(pickedCard.getIsBuilding()){
                buildingCards.add((BuildingCard) pickedCard);
            }
            else {
                characterCards.add((CharacterCard) pickedCard);
            }
        }

        //check if move is valid
        Exception mE = MoveValidator.validateCardChoice(myOfferingCard.getNumCardsUpper(), myOfferingCard.getNumCardsLower(),
                characterCards, buildingCards, readOnlyModel.getUpperTribeRow(), readOnlyModel.getLowerTribeRow(), readOnlyModel.getUpperBuildingRow(), readOnlyModel.getLowerBuildingRow());
        if(mE != null)
        {
            drawInterface(mE.getMessage());
            return;
        }

        //check if player can buy the buildings
        if(!buildingCards.isEmpty() && !localPlayer.canBuyBuidings(buildingCards)) {
            drawInterface("Not enough food to buy building cards");
            return;
        }


        // call server method
        try{
            virtualServer.pickTribeCards(this.client, characterCards,buildingCards);
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
            tribeRow = readOnlyModel.getUpperTribeRow();
            buildingRow = readOnlyModel.getUpperBuildingRow();
            System.out.print("Upper row:");
        }
        else {
            tribeRow = readOnlyModel.getLowerTribeRow();
            buildingRow = readOnlyModel.getLowerBuildingRow();
            System.out.print("Lower row:");
        }
        // set the starting index for the card display numbering
        int currentIndex = startingIndex;
        // print character cards
        for (TribesCard card : tribeRow) {
            if (card.getCardType().isCharacter()) {
                System.out.print(" " + currentIndex + ") " + card);
                // increase number only when the card is printed
                currentIndex++;
            }
        }
        // print building cards
        for (BuildingCard card : buildingRow) {
            System.out.print(" " + currentIndex + ") "+ card);
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
     * @return  the selected color
     */
    private Color chooseColor() {
        Color[] colors = Color.values();

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
     */
    private void changeColor(){
        Color c = chooseColor();
        localPlayer.setColor(c);
    }

    /**
     * Asks the user for a new nickname and sets it to player
     */
    private void changeNickname(){
        String nickname = askNickname();
        localPlayer.setNickname(nickname);
    }

    /**
     * Asks the user to type their nickname
     * @return the nickname to be set
     */
    private String askNickname() {
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
     */
    private void createGame(){
        try {
            if (readOnlyModel.getGameState() == GameState.NONE) {
                System.out.print("How many players? (2 to 5) > ");
                int numPlayers = Integer.parseInt(scanner.nextLine());
                System.out.println("Trying to create game...");
                virtualServer.createGame(client, localPlayer, numPlayers);
            } else {
                System.out.print("Already in a game \n>");
            }
        }catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Sends server command to close the current game
     */
    private void closeGame(){
        try {
            if (readOnlyModel.getGameId() == null) {
                System.out.print("Not in a game \n>");
            } else {
                virtualServer.closeGame(client);
            }
        }catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Sends server command to pick offering card
     */
    private void pickOfferingCard(){
        try {
            System.out.print("Insert card letter > ");
            Character cardLetter = scanner.next().charAt(0);
            //check if letter is present in offering card list
            var letters = readOnlyModel.getOfferingCards()
                    .stream().map(OfferingCard::getOrderLetter).toList();
            if(!letters.contains(cardLetter)) {
                drawInterface("Offering card not found");
                return;
            }
            //check if card is free
            OfferingCard selectedOfferingCard = readOnlyModel.getOfferingCards().stream()
                    .filter(c -> c.getOrderLetter() == cardLetter)
                    .findFirst().orElseThrow();
            if(selectedOfferingCard.getPlayer() != null) {
                drawInterface("Card already picked");
                return;
            }
            virtualServer.pickOfferingCard(this.client, cardLetter);
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Sends server command to join game
     */
    private void joinGame(){
        try {
            if (readOnlyModel.getGameState().equals(GameState.NONE)) {
                System.out.print("Insert the gameID or index in gameList > ");
                String input = scanner.nextLine().trim();
                UUID gameId = null;

                // Try to treat input as an index (Integer)
                if (input.matches("\\d+")) {
                    int index = Integer.parseInt(input);
                    if (index >= 0 && index < readOnlyModel.getGamesIdList().size()) {
                        gameId = readOnlyModel.getGamesIdList().get(index);
                    } else {
                        System.out.println("Index out of bounds.");
                    }
                }
                //Otherwise, try to treat input as a UUID
                else {
                    try {
                        gameId = UUID.fromString(input);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid format. Please enter a number or a valid UUID.");
                    }
                }

                //If we successfully got a gameId, proceed
                if (gameId != null) {
                    System.out.println("Trying to connect...");
                    virtualServer.joinGame(client, gameId, localPlayer);
                }
            } else {
                System.out.println("Already in a game!");
            }
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints message on the terminal to notify the user that the new era has begun.
     * TODO: fix this method
     */
    public void printEra(){
        System.out.println("\nEra "+readOnlyModel.getGameState()+ " has begun!\n");
    }

    /**
     * Draws the tribe and building row
     * @param upper if true prints the upper row, if false prints the lower row
     */
    private void drawRow(boolean upper){
        List<TribesCard> tribeRow;
        List<BuildingCard> buildingRow;
        if(upper) {
            tribeRow = readOnlyModel.getUpperTribeRow();
            buildingRow = readOnlyModel.getUpperBuildingRow();
        }
        else {
            tribeRow = readOnlyModel.getLowerTribeRow();
            buildingRow = readOnlyModel.getLowerBuildingRow();
        }
        if(!(tribeRow.isEmpty() && buildingRow.isEmpty())){
            if(upper) System.out.print("Upper row:     ");
            else System.out.print("Lower row:     ");

            if(!tribeRow.isEmpty()) {
                for (TribesCard c : tribeRow) {
                    System.out.print(c);
                }
            }
            if(!buildingRow.isEmpty()){
                for(BuildingCard c : buildingRow) {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }

    /**
     * Draws the offering cards list
     */
    private void drawOfferingCard(){
        var offeringCards = readOnlyModel.getOfferingCards();
        if(!offeringCards.isEmpty()){
            System.out.print("Bidding trail: ");
            for(OfferingCard c : offeringCards) {
                System.out.print(c);
            }
            System.out.println();
        }
    }

    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    public void setLocalPlayer() {
        readOnlyModel.getOrderedPlayers().stream()
                .filter(p -> p.getNickname().equals(localPlayer.getNickname()))
                .findFirst().ifPresent(foundPlayer -> localPlayer = foundPlayer);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}