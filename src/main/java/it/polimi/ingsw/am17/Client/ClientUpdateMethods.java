package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientUpdateMethods {

    public static void updateEra(ClientModel model, UI userInterface, int era) {
        // call model to update era
        model.setCurrentEra(era);
        // UI communication
        userInterface.printEra();
    }

    public static void updatePlayerStack(ClientModel model, UI userInterface, LinkedList<Player> orderedPlayer) {
        model.setOrderedPlayers(orderedPlayer);
        // UI communication
        userInterface.drawInterface(model);
    }

    public static void updateGameId(ClientModel model, UI userInterface, UUID gameId) {
        model.setGameId(gameId);
        // UI communication
        userInterface.printGameId(gameId);
    }

    public static void updateGamesIdList(ClientModel model, UI userInterface, List<UUID> gamesIdList) {
        model.setGameIdList(gamesIdList);
        userInterface.printGamesList();
    }

    public static void updateStartGame(ClientModel model, UI userInterface, Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) {

        model.setCurrentEra(1);
        model.setPickOfferingCardPhase(true);
        model.setNumPlayers(players.size());
        model.setOrderedPlayers(players);
        model.setAllPlayers(players.stream().toList()); // TODO: this creates an immutable object (later changed) FIX
        model.setTribeCards(upperRow, lowerRow);
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setOfferingCards(offeringCards);

        userInterface.drawInterface(model);
    }

    public static void updateEndTurn(ClientModel model, UI userInterface, Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        for(Player player : players) {
            updatePlayerValue(model.getPlayer(player), player);
        }
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setTribeCards(upperRow, lowerRow);
        model.setPickOfferingCardPhase(true);

        userInterface.drawInterface(model);
    }

    private static void updatePlayerValue(Player oldP, Player newP) {
        oldP.addPp(newP.getPp()- oldP.getPp());
        oldP.addFood(newP.getFood() - oldP.getFood());
    }

    public static void updatePlayerSelectOfferingCard(ClientModel model, UI userInterface, Player player, OfferingCard offeringCard) {
        model.setPlayerOfferingCard(offeringCard, player);
        userInterface.drawInterface(model);
    }

    public static void updatePlayerSelectTribeCards(ClientModel model, UI userInterface, Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) {
        model.setPlayerInStack(player);
        model.removePlayerFromOfferingCard(player);
        model.removeTribeCards(tribesCards);
        model.removeBuildingCards(buildingCards);
        model.setNullOfferingCardAPlayer();
        userInterface.drawInterface(model);
    }
}
