package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientSocket implements VirtualView {
    VirtualServerSocket virtualServer;

    public void start(String s, boolean gui) throws IOException {
        virtualServer = new VirtualServerSocket("localhost", 5000);
    }

    @Override
    public void updateGameId(UUID gameId) throws Exception {

    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws Exception {

    }

    @Override
    public void updateEra(int era) throws Exception {

    }

    @Override
    public void updatePlayerStack(Stack<Player> orderedPlayer) throws Exception {

    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws Exception {

    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws Exception {

    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception {

    }
}
