package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientUpdateMethods {

    public static void updateEra(ClientModel model, int era, UI userInterface){
        // call model to update era
        model.setCurrentEra(era);
        // UI communication
        // spostare nella CLI
        if(era == 1)
        {
            System.out.println("è iniziata la partita");
        }else
        {
            System.out.println("è iniziata la era successiva");
        }
    }

    public static void updatePlayerStack(Stack<Player> orderedPlayer, ClientModel model, UI userInterface){
        model.setOrderedPlayers(orderedPlayer);
        // UI communication
        userInterface.drawInterface(model);
    }


    public static void updateGameId(UUID gameId, ClientModel model, UI userInterface){
        model.setGameId(gameId);
        // UI communication
        userInterface.printGameId(gameId);
    }

    public static void updateGamesIdList(List<UUID> gamesIdList, ClientModel model, UI userInterface) {
        model.setGameIdList(gamesIdList);
        // TODO: call user interface
    }


    public static void updateStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards,
                                ClientModel model, UI userInterface) {

        model.setOrderedPlayers(players);
        model.setTribeCards(upperRow, lowerRow);
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setOfferingCards(offeringCards);

        userInterface.drawInterface(model);
    }


    public static void updateEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow,
                              List<BuildingCard> lowerBuildingRow, ClientModel model, UI userInterface) {
        for(Player player : players)
        {
            updatePlayerValue(model.getPlayer(player), player);
        }
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setTribeCards(upperRow, lowerRow);

        userInterface.drawInterface(model);
    }

    private static void updatePlayerValue(Player oldP, Player newP)
    {
        oldP.addPp(newP.getPp()- oldP.getPp());
        oldP.addFood(newP.getFood() - oldP.getFood());
    }


    public static void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard, ClientModel model, UI userInterface) {
        model.setPlayerOfferingCard(offeringCard, player);

        userInterface.drawInterface(model);
    }

    public static void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards
            , ClientModel model, UI userInterface) {
        //set new value for player
        for (int i = 0; i < model.orderedPlayer.size(); i++) {
            if (model.orderedPlayer.get(i).equals(player)) {
                model.orderedPlayer.set(i, player);
                break;
            }
        }

        model.offeringCards.stream()
                .filter(o -> o.getPlayer().equals(player))
                .findFirst()
                .ifPresent(o -> o.setPlayer(null));

        model.upperRow.removeAll(tribesCards);
        model.lowerRow.removeAll(tribesCards);

        model.upperBuildingRow.removeAll(buildingCards);
        model.lowerBuildingRow.removeAll(buildingCards);

        userInterface.drawInterface(model);
    }
}
