package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.ServerAdapter;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
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

import static it.polimi.ingsw.am17.CommonInterfaces.SharedModelLogic.getTurnFoodPoints;

public class CLI implements UI {
    private final ServerAdapter serverAdapter;
    private ClientModel readOnlyModel;
    private Player localPlayer;
    private boolean building2EffectUsed;
    List<Color> availableColors;
    Scanner scanner;

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public CLI (ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
        this.scanner = new Scanner(System.in);
        resetColors();
        building2EffectUsed = false;
    }

    @Override
    public void setModel(ClientModel model) {
        this.readOnlyModel = model;
    }

    @Override
    public void setAvailableColors(List<Color> availableColors) {this.availableColors=availableColors;}

    /**
     * Starts the cli and collects user commands
     */
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
        System.out.println("\nYou are connected to game: ".concat(gameId.toString()));
        showPrompt();
    }

    /**
     * Prints the games id list.
     */
    public void printGamesList(){
        System.out.println("\r\033[2KOpen games:");
        for(int i=0; i< readOnlyModel.getGamesIdList().size(); i++){
            System.out.println(i + "\t" + readOnlyModel.getGamesIdList().get(i));
        }
        showPrompt();
    }

    /**
     * Sends the get id list request to server.
     */
    private void getGamesList(){
        try {
            serverAdapter.getGamesList().join();
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getCause().getMessage());
        }
    }

    /**
     * Draws the game configuration.
     * @param errorMessage  the message to display
     */
    public synchronized void drawInterface(String errorMessage)
    {
        try{
            //clear console
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
            // TODO: remove this loop for real terminal execution
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }

            printError(errorMessage);

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
                    if(readOnlyModel.isPlayerTurn())
                        System.out.println("It's your turn!");
                }
            }
            else if (gameState.isGameEnded()) {
                resetColors();
                drawLocalRanking();
                drawGlobalRanking();
            }
            showPrompt();
        } catch (Exception e) {
            System.err.println("CLI error: " + e.getMessage());
        }
    }

    /**
     * Prints error message
     * @param errorMessage  the error generated by the server
     */
    private void printError(String errorMessage) {
        if(errorMessage != null && !errorMessage.isBlank()) {
            System.out.println(ANSI_RED + errorMessage + ANSI_RESET);
        }
    }

    /**
     * Resets available color list to all colors
     */
    private void resetColors(){
        setAvailableColors(Arrays.stream(Color.values()).toList());
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
     * @return the entry of the local player in global ranking
     */
    private RankingEntry getUserEntry(){
        return readOnlyModel.getRanking().stream().filter(e -> e.getGameId().equals(readOnlyModel.getGameId())
                        && e.getNickname().equals(localPlayer.getNickname()))
                .findFirst().orElse(null);
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

        int[] turnFood = getTurnFoodPoints(readOnlyModel.getNumPlayers());

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
    private void printSelectionMessage(OfferingCard offeringCard) {
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
            startingIndex = printPickableRow(true, startingIndex);
        }

        if (offeringCard.getNumCardsLower() > 0) {
            // add lower character cards
            pickableCards.addAll(readOnlyModel.getLowerTribeRow().stream()
                    .filter(c->c.getCardType().isCharacter()).toList());

            // add lower building cards
            if(!readOnlyModel.getLowerBuildingRow().isEmpty())
                pickableCards.addAll(readOnlyModel.getLowerBuildingRow());

            // print the lower row
            printPickableRow(false, startingIndex);
        }
        return pickableCards;
    }

    /**
     * Allows the user to select offering cards and validates user input
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
     * @param offeringCard      the players offering card
     * @param characterCards    the character cards selected by the player
     * @param buildingCards     the building cards selected by the player
     * @return                  true if selection is valid, false otherwise
     */
    private boolean isMoveValid(OfferingCard offeringCard, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        try{
            readOnlyModel.validatePickTribeCards(characterCards, buildingCards);
        } catch (InvalidOperationException e){
            printError(e.getErrorType().getMessage());
            return;
        } catch (Exception e) {
            printError(e.getMessage());
            return;
        }
    }

    /**
     * Gets the user selected cards and sends them to server
     */
    private void pickCards() {
        // check if it is the players turn
        if (!readOnlyModel.isPlayerTurn()) {
            printError(ErrorType.OUT_OF_TURN.getMessage());
            return;
        }

        // get players offering card
        // todo: method
        OfferingCard myOfferingCard = readOnlyModel.getOfferingCards().stream()
                .filter(c->c.getPlayer()!= null && c.getPlayer().equals(localPlayer))
                .findFirst().orElse(null);


        if (myOfferingCard == null) {
            // if player has BuildingType2
            if (localPlayer.hasBuilding2()) {
                myOfferingCard = readOnlyModel.getBuildingTwoOfferingCard();
            }
            else {
                // if not found, return
                System.out.println("No offering card chosen!");
                return;
            }
        }

        // calculate the number of cards the user can pick
        // todo: method
        int totalCards = myOfferingCard.getNumCardsUpper() + myOfferingCard.getNumCardsLower();

        // if card with letter A, no card can be chosen
        if(totalCards == 0) {
            System.out.println("You can't pick any card!");
            return;
        }

        printSelectionMessage(myOfferingCard);

        // list of pickable cards
        List<GameCard> pickableCards = buildPickableCardsList(myOfferingCard);

        if (pickableCards.isEmpty()) {
            System.out.println("You can't pick any card!");
            return;
        }

        // cards selection
        Set<Integer> cardIndexes = tribeCardsSelection(totalCards, pickableCards.size());

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
        if(isMoveValid(myOfferingCard, characterCards, buildingCards)) {
            // call server method
            try {
                serverAdapter.pickTribeCards(characterCards, buildingCards).join();
            } catch (Exception e) {
                System.err.println("CLI error: " + e.getCause().getMessage());
            }
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
                    System.out.println("Invalid number. Please choose between 1 and " + availableColors.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number, not text.");
            }
        }
    }

    /**
     * Lets player choose new color
     */
    private void changeColor() {
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
                serverAdapter.createGame(localPlayer, numPlayers).join();
            } else {
                System.out.print("Already in a game!");
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
                System.out.print("Not in a game");
            } else {
                serverAdapter.closeGame().join();
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
            if (!readOnlyModel.isPlayerTurn())
                throw new InvalidOperationException(ErrorType.OUT_OF_TURN);

            System.out.print("Insert card letter > ");
            Character cardLetter = scanner.nextLine().trim().toUpperCase().charAt(0);

            readOnlyModel.validatePickOfferingCard(cardLetter);

            // send request
            serverAdapter.pickOfferingCard(cardLetter).join();
        } catch (InvalidOperationException e) {
            drawInterface(e.getErrorType().getMessage());
        } catch (Exception e) {
            drawInterface(e.getMessage());
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
                    serverAdapter.joinGame(gameId, localPlayer).join();
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

    @Override
    public boolean isBuilding2EffectUsed() {
        return building2EffectUsed;
    }

    @Override
    public void setBuilding2EffectUsed(boolean building2EffectUsed) {
        this.building2EffectUsed = building2EffectUsed;
    }

    /**
     * prints character to signal that the cli is available for a new command
     */
    private void showPrompt() {
        System.out.print("\r> ");
        System.out.flush();
    }

    public void updateInterfaceFromPickTribes(){
        drawInterface(null);
    }
    public void updateInterfaceFromPickOffering(){
        drawInterface(null);
    }
    public void updateInterfaceFromEndTurn(){
        drawInterface(null);
    }
}