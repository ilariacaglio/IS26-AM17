package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.ServerAdapter;
import it.polimi.ingsw.am17.CommonInterfaces.ColorException;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.SharedModelLogic;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.GameCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;

public class CLI implements UI {
    private final ServerAdapter serverAdapter;
    private ClientModel readOnlyModel;
    private Player localPlayer;
    List<Color> availableColors;
    Scanner scanner;

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public CLI (ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
        this.scanner = new Scanner(System.in);
        resetColors();
    }



    // methods called by RMI-Socket clients

    @Override
    public void setModel(ClientModel model) {
        this.readOnlyModel = model;
    }



    // methods called by the model

    /**
     * Starts the cli and collects user commands
     */
    @Override
    public void start() {
        System.out.println("=== Welcome to Mesos ===");

        try {
            boolean running = true;

            // set up the user
            String nickname = askNickname();
            Color color = chooseColor();
            localPlayer = new Player(nickname, color);
            // print help
            printHelp();

            while (running) {
                showPrompt();
                String input = scanner.nextLine().trim().toLowerCase();
                switch (input) {
                    // lobby stage
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
                    case "join", "j":
                        joinGame();
                        break;
                    // game stage
                    case "pick offering card", "po":
                        pickOfferingCard();
                        break;
                    case "pick cards", "p":
                        pickTribeCards();
                        break;
                    case "view player", "vp":
                        printPlayer();
                        break;
                    case "close game", "xxx":
                        closeGame();
                        break;
                    case "help", "h":
                        printHelp();
                        break;
                    case "exit", "quit", "q":
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.print("Command not recognized. Please type 'help' to view the list of available commands");
                }
            }
            // exit
            System.exit(0);

        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints the game interface when the player joins a new game
     */
    @Override
    public void updateInterfaceFromGameIdChange() {
        String message = "You are connected to game: ".concat(readOnlyModel.getGameId().toString());
        drawInterface(message, false);
    }

    /**
     * Prints the game interface when the open games list is recieved
     */
    @Override
    public void updateInterfaceFromGameIdListChange() {
        // print games list
        System.out.println("\r\033[2KOpen games:");
        for(int i=0; i< readOnlyModel.getGamesIdList().size(); i++){
            System.out.println(i + "\t" + readOnlyModel.getGamesIdList().get(i));
        }
        showPrompt();
    }

    /**
     * Prints the game interface when the game state has changed
     */
    @Override
    public void updateInterfaceFromGameStateChange() {
        String eraToDisplay = readOnlyModel.getGameState().toString().replace("era", "");
        String message = "Era "+ eraToDisplay + " has begun!";
        drawInterface(message, false);
    }

    /**
     * Prints the game interface when the players queue is updated.
     * Updates the data of the local player
     */
    @Override
    public void updateInterfaceFromPlayerQueueChange() {
        setLocalPlayer();
        if(readOnlyModel.getGameState().isInLobby()){
            String message = "You are connected to game: ".concat(readOnlyModel.getGameId().toString());
            drawInterface(message, false);
        }
        else {
            drawInterface();
        }
    }


    /**
     * Prints the game interface when a tribes card selection update message is received
     */
    @Override
    public void updateInterfaceFromPlayerSelectTribeCards(){
        drawInterface();
    }

    /**
     * Prints the game interface when an offering card selection update message is received
     */
    @Override
    public void updateInterfaceFromPlayerSelectOfferingCard(){
        drawInterface();
    }

    /**
     * Prints the game interface when the game turn ends
     */
    @Override
    public void updateInterfaceFromEndTurn(){
        drawInterface();
    }

    /**
     * Prints the game interface when game starts
     */
    @Override
    public void updateInterfaceFromStartGame() {
        drawInterface();
    }

    /**
     * Prints the game interface when game ends regularly
     */
    @Override
    public void updateInterfaceFromEndGame() {
        drawInterface();
    }

    /**
     * Prints the game interface when game ends due to a player action or connection error
     */
    @Override
    public void updateInterfaceFromForcedEndGame(String disconnectedPlayer) {
        String errorMessage = "The game has ended due to disconnection of player " + disconnectedPlayer;
        drawInterface(errorMessage, true);
    }

    /**
     * Prints the game interface when a notification error is received.
     * @param exception     the exception describing the error occurred.
     */
    @Override
    public void updateInterfaceFromErrorMessage(InvalidOperationException exception) {
        ErrorType type = exception.getErrorType();
        if (type == ErrorType.DUPLICATE_COLOR)
            availableColors = ((ColorException)exception).getAvailableColors();

        String messageToDisplay = (type == ErrorType.UNKNOWN)
                ? exception.getMessage()
                : type.getMessage();

        drawInterface(messageToDisplay, true);
    }


    // Methods called by the CLI used to print data

    /**
     * Draws the CLI interface.
     *
     * @param message the message to display
     * @param isError if true the message to display is an error, false otherwise
     */
    private synchronized void drawInterface(String message, boolean isError){
        try {
            clearConsole();
            if(isError)
                printError(message);
            else
                printMessage(message);
            drawGameView();
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Draws the CLI interface.
     */
    private synchronized void drawInterface(){
        try {
            clearConsole();
            drawGameView();
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Clears the console.
     * This is called before printing the game interface
     */
    private void clearConsole(){
        //clear console
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
        // TODO: remove this loop for real terminal execution
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Draws the game configuration.
     */
    private void drawGameView()
    {
        // get game state to display the correct items
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
                drawOfferingCards();

                //draw lower row
                drawRow(false);

                // print the cards of the player
                drawLocalPlayer();

                // if the game has begun notify the players turn
                if(readOnlyModel.isPlayerTurn(localPlayer))
                    System.out.println("It's your turn!");
            }
        }
        else if (gameState.isGameEnded()) {
            resetColors();
            drawLocalRanking();
            drawGlobalRanking();
        }
        showPrompt();
    }

    /**
     * Prints the commands list
     */
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("- help, h: shows this menu");
        System.out.println("- get games, gg: get the list of starting games");
        System.out.println("- change nickname, cn: changes the player's nickname");
        System.out.println("- change color, cc: changes the player's color");
        System.out.println("- create, c: creates a new game");
        System.out.println("- join, j: joins an existing game");
        System.out.println("- pick offering card, po: choose the offering card to take");
        System.out.println("- pick cards, p: choose the cards to take");
        System.out.println("- view player, vp: shows a player's cards, food, and points");
        System.out.println("- close game, xxx: closes the current game");
        System.out.println("- exit, quit, q: closes the application");
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
     * Prints error message
     * @param errorMessage  the error generated by the server
     */
    private void printError(String errorMessage) {
        System.out.println(ANSI_RED + errorMessage + ANSI_RESET);
    }

    /**
     * Prints additional message when the game is drawn
     */
    private void printMessage(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
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
     * prints global ranking when game ends and player position
     */
    private void drawGlobalRanking(){
        List<RankingEntry> ranking = readOnlyModel.getRanking();
        if (!ranking.isEmpty()) {
            System.out.println("\n--- YOUR POSITION IN GLOBAL RANKING ---");
            RankingEntry userEntry = getUserEntry();
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
            for (RankingEntry entry: ranking) {
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

        int[] turnFood = SharedModelLogic.getTurnFoodPoints(readOnlyModel.getNumPlayers());

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
     * Prints specific selection message
     * @param offeringCard  the offering card that allows the user to pick cards
     */
    private void printTribeCardsSelectionMessage(OfferingCard offeringCard) {
        int upperCards = offeringCard.getNumCardsUpper();
        int lowerCards = offeringCard.getNumCardsLower();
        // print specific selection message
        StringBuilder pickMessage = new StringBuilder("You can pick ");
        List<String> pickableRows = new ArrayList<>();
        if (upperCards > 0) {
            pickableRows.add(upperCards + (upperCards == 1 ? " card" : " cards") + " from the upper row");
        }
        if (lowerCards > 0) {
            pickableRows.add(lowerCards + (lowerCards == 1 ? " card" : " cards") + " from the lower row");
        }
        pickMessage.append(String.join(" and ", pickableRows));
        System.out.println(pickMessage);
    }

    /**
     * Prints all the character and building cards in the row
     * @param upper if true prints the upper row, if false prints the lower row
     */
    private int printPickableTribeRow(boolean upper, int startingIndex){
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
    private void drawOfferingCards(){
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
     * prints character to signal that the cli is available for a new command
     */
    private void showPrompt() {
        System.out.print("\r> ");
        System.out.flush();
    }



    // methods used to implement user functions

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
                System.out.print(ANSI_RED + "Your nickname has more than 10 characters!\n" + ANSI_RESET);
            }
        }
        return nickname;
    }

    /**
     * Prints the list of available colors and
     * lets the user select one of them
     * @return  the selected color
     */
    private Color chooseColor() {
        // color selection
        while (true) {
            System.out.println("Available colors:");
            for (int i = 0; i < availableColors.size(); i++) {
                System.out.println((i + 1) + ") " + availableColors.get(i));
            }
            System.out.print("Choose a color number > ");

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < availableColors.size()) {
                    return availableColors.get(choice);
                } else {
                    System.out.println(ANSI_RED + "Invalid number. Please choose between 1 and " + availableColors.size() + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Please enter a valid number, not text." + ANSI_RESET);
            }
        }
    }

    /**
     * Sends the get id list request to server.
     */
    private void getGamesList(){
        try {
            serverAdapter.getGamesList().join();
        } catch (Exception e) {
            drawInterface("CLI error: " + e.getCause().getMessage(), true);
        }
    }

    /**
     * Asks the user for a new nickname and sets it to player
     */
    private void changeNickname(){
        if(readOnlyModel.getGameState() == GameState.NONE){
            String nickname = askNickname();
            localPlayer.setNickname(nickname);
        }
        else {
            drawInterface("Already in a game!", true);
        }
    }

    /**
     * Lets player choose new color
     */
    private void changeColor() {
        if(readOnlyModel.getGameState() == GameState.NONE){
            Color c = chooseColor();
            localPlayer.setColor(c);
        }
        else {
            drawInterface("Already in a game!", true);
        }
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
                serverAdapter.createGame(localPlayer, numPlayers).join();
            } else {
                drawInterface("Already in a game!", true);
            }
        }catch (Exception e) {
            drawInterface("CLI error: " + e.getMessage(), true);
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
                        drawInterface("Index out of bounds.", true);
                    }
                }
                //Otherwise, try to treat input as a UUID
                else {
                    try {
                        gameId = UUID.fromString(input);
                    } catch (IllegalArgumentException e) {
                        drawInterface("Invalid format. Please enter a number or a valid UUID.", true);
                    }
                }

                //If we successfully got a gameId, proceed
                if (gameId != null) {
                    System.out.println("Trying to connect...");
                    serverAdapter.joinGame(gameId, localPlayer).join();
                }
            } else {
                drawInterface("Already in a game!", true);
            }
        } catch (Exception e) {
            drawInterface("CLI error: " + e.getMessage(), true);
        }
    }

    /**
     * Sends server command to pick offering card
     */
    private void pickOfferingCard(){
        if (!readOnlyModel.getGameState().isGameStarted()) {
            drawInterface("The game hasn't started!", true);
            return;
        }
        try {
            System.out.print("Insert card letter > ");
            Character cardLetter = scanner.nextLine().trim().toUpperCase().charAt(0);

            readOnlyModel.validateOfferingCardTurnAction(localPlayer, cardLetter);

            // send request
            serverAdapter.pickOfferingCard(cardLetter).join();
        } catch (InvalidOperationException e) {
            drawInterface(e.getErrorType().getMessage(), true);
        } catch (Exception e) {
            drawInterface(e.getMessage(), true);
        }
    }

    /**
     * Gets the user selected cards and sends them to server
     */
    private void pickTribeCards() {
        if (!readOnlyModel.getGameState().isGameStarted()) {
            drawInterface("The game hasn't started!", true);
            return;
        }

        // get players offering card
        OfferingCard myOfferingCard = getPlayerOfferingCard();

        if (myOfferingCard == null) {
            // if not found, return
            drawInterface("No offering card selected.", true);
            return;
        }

        // calculate the number of cards the user can pick
        int totalCards = myOfferingCard.getNumCardsUpper() + myOfferingCard.getNumCardsLower();

        // if card with letter A, no card can be chosen
        if(totalCards == 0) {
            drawInterface("Yon can't pick any card!",true);
            return;
        }

        printTribeCardsSelectionMessage(myOfferingCard);

        // list of pickable cards
        List<GameCard> pickableCards = buildPickableCardsList(myOfferingCard);

        if (pickableCards.isEmpty()) {
            drawInterface("Yon can't pick any card!",true);
            return;
        }

        // cards selection
        Set<Integer> cardIndexes = tribeCardsSelection(totalCards, pickableCards.size());

        // build cards lists
        List<CharacterCard> characterCards = buildCharacterList(cardIndexes, pickableCards);
        List<BuildingCard> buildingCards = buildBuildingList(cardIndexes, pickableCards);

        //check if move is valid
        if(isMoveValid(characterCards, buildingCards)) {
            // call server method
            try {
                serverAdapter.pickTribeCards(characterCards, buildingCards).join();
            } catch (Exception e) {
                drawInterface("CLI error: " + e.getCause().getMessage(), true);
            }
        }
    }

    /**
     * Sends server command to close the current game
     */
    private void closeGame(){
        try {
            if (readOnlyModel.getGameId() == null) {
                drawInterface("Not in a game", true);
            } else {
                serverAdapter.closeGame().join();
            }
        }catch (Exception e) {
            drawInterface("CLI error: " + e.getCause().getMessage(), true);
        }
    }



    // Utility methods

    /**
     * Resets available color list to all colors
     */
    private void resetColors(){
        availableColors = Arrays.stream(Color.values()).toList();
    }

    /**
     * @return the entry of the local player in global ranking
     */
    private RankingEntry getUserEntry(){
        return readOnlyModel.getRanking().stream().filter(e -> e.getGameId().equals(readOnlyModel.getGameId())
                        && e.getNickname().equals(localPlayer.getNickname()))
                .findFirst().orElse(null);
    }


    /**
     * Builds the list of tribe cards the user can pick
     * @param offeringCard      the offering card selected by the user
     * @return                  the list of cards the user can pick
     */
    private List<GameCard> buildPickableCardsList(OfferingCard offeringCard) {
        List<GameCard> pickableCards = new ArrayList<>();
        int startingIndex = 1;
        if (offeringCard.getNumCardsUpper() > 0) {
            // add upper character cards
            pickableCards.addAll(readOnlyModel.getUpperTribeRow().stream()
                    .filter(c->c.getCardType().isCharacter()).toList());

            // add upper building cards
            pickableCards.addAll(readOnlyModel.getUpperBuildingRow());

            // print the upper row
            startingIndex = printPickableTribeRow(true, startingIndex);
        }

        if (offeringCard.getNumCardsLower() > 0) {
            // add lower character cards
            pickableCards.addAll(readOnlyModel.getLowerTribeRow().stream()
                    .filter(c->c.getCardType().isCharacter()).toList());

            // add lower building cards
            if(!readOnlyModel.getLowerBuildingRow().isEmpty())
                pickableCards.addAll(readOnlyModel.getLowerBuildingRow());

            // print the lower row
            printPickableTribeRow(false, startingIndex);
        }
        return pickableCards;
    }

    /**
     * Allows the user to select offering cards
     * @return  the set of the indexes of the cards selected
     */
    private Set<Integer> tribeCardsSelection(int totalCards, int pickableCardsSize){
        Set<Integer> cardIndexes = new HashSet<>();
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
                if (numCard >= 0 && numCard < pickableCardsSize) {
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
        return cardIndexes;
    }

    /**
     * Validates tribes card selection
     * @param characterCards    the character cards selected by the player
     * @param buildingCards     the building cards selected by the player
     * @return                  true if selection is valid, false otherwise
     */
    private boolean isMoveValid(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        try{
            readOnlyModel.validateTribeCardsTurnAction(localPlayer, characterCards, buildingCards);
            return true;
        } catch (InvalidOperationException e){
            printError(e.getErrorType().getMessage());
        } catch (Exception e) {
            printError(e.getMessage());
        }
        return false;
    }

    /**
     * @return the local player's offering card
     */
    private OfferingCard getPlayerOfferingCard(){
        OfferingCard myOfferingCard = readOnlyModel.getOfferingCards().stream()
                .filter(c->c.getPlayer()!= null && c.getPlayer().equals(localPlayer))
                .findFirst().orElse(null);
        if (myOfferingCard == null && localPlayer.hasBuilding2()) return readOnlyModel.getBuildingTwoOfferingCard();
        return myOfferingCard;
    }

    /**
     * @return the list of character cards selected by the user
     */
    private List<CharacterCard> buildCharacterList(Set<Integer> cardIndexes, List<GameCard> pickableCards) {
        List<CharacterCard> characterCards = new ArrayList<>();
        for(Integer i : cardIndexes) {
            GameCard pickedCard = pickableCards.get(i);
            if(!pickedCard.getIsBuilding())
                characterCards.add((CharacterCard) pickedCard);
        }
        return characterCards;
    }

    /**
     * @return the list of building cards selected by the user
     */
    private List<BuildingCard> buildBuildingList(Set<Integer> cardIndexes, List<GameCard> pickableCards) {
        List<BuildingCard> buildingCards = new ArrayList<>();
        for(Integer i : cardIndexes) {
            GameCard pickedCard = pickableCards.get(i);
            if(pickedCard.getIsBuilding())
                buildingCards.add((BuildingCard) pickedCard);
        }
        return buildingCards;
    }

    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    private void setLocalPlayer() {
        readOnlyModel.getOrderedPlayers().stream()
                .filter(p -> p.getNickname().equals(localPlayer.getNickname()))
                .findFirst().ifPresent(foundPlayer -> localPlayer = foundPlayer);
    }
}