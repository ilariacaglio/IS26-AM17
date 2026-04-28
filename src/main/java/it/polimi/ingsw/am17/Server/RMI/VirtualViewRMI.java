package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public interface VirtualViewRMI extends Remote, VirtualView {
    //new
    void updateGameId(UUID gameId) throws RemoteException;
    void updateGamesIdList(List<UUID> gamesIdList) throws RemoteException;
    void updateEra(int era) throws RemoteException;
    void updatePlayerStack(Stack<Player> orderedPlayer) throws RemoteException;

    void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws RemoteException;

    void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws RemoteException;

    void updateEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws  RemoteException;

    void updateStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                         List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) throws  RemoteException;


}
