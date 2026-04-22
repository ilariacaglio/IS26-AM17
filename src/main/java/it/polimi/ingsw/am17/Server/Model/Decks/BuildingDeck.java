package it.polimi.ingsw.am17.Server.Model.Decks;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.am17.Server.Utility.CardParser.buildingCard13MParser;
import static it.polimi.ingsw.am17.Server.Utility.CardParser.buildingCard3MParser;

public class BuildingDeck {
    private final List<BuildingCard> buildingCardsEra1;
    private final List<BuildingCard> buildingCardsEra2;
    private final List<BuildingCard> buildingCardsEra3;

    public BuildingDeck(int numPlayers)
    {
        buildingCardsEra1 = new ArrayList<>();
        buildingCardsEra2 = new ArrayList<>();
        buildingCardsEra3 = new ArrayList<>();
        buildEra1Deck(numPlayers);
        buildEra2Deck(numPlayers);
        buildEra3Deck(numPlayers);
    }

    private void buildEra1Deck(int numPlayers){
        List<BuildingCard> temp = new ArrayList<>();
        //singleton building cards
        temp.add(new BuildingType10());
        temp.add(new BuildingType11());
        temp.add(new BuildingType12());
        temp.add(new BuildingType14());
        //parse from json and add cards 13M
        temp.addAll(buildingCard13MParser(1));
        //shuffle collection
        Collections.shuffle(temp);
        //add the right number of cards based on numPlayer
        if(numPlayers == 2) {
            buildingCardsEra1.add(temp.getFirst());
        }
        else {
            buildingCardsEra1.addAll(temp.subList(0,2));
        }
    }

    private void buildEra2Deck(int numPlayers){
        List<BuildingCard> temp = new ArrayList<>();
        //singleton building cards
        temp.add(new BuildingType4());
        temp.add(new BuildingType5());
        temp.add(new BuildingType6());
        temp.add(new BuildingType7());
        temp.add(new BuildingType8());
        temp.add(new BuildingType9());
        //parse from json and add cards 13M
        temp.addAll(buildingCard13MParser(2));
        //shuffle collection
        Collections.shuffle(temp);
        //add the right number of cards based on numPlayer
        if(numPlayers <= 3) {
            buildingCardsEra2.addAll(temp.subList(0,2));
        }
        else {
            buildingCardsEra2.addAll(temp.subList(0,3));
        }
    }

    private void buildEra3Deck(int numPlayers){
        List<BuildingCard> temp = new ArrayList<>();
        //singleton building cards
        temp.add(new BuildingType1());
        temp.add(new BuildingType2());
        //parse from json and add cards 3M
        temp.addAll(buildingCard3MParser());
        //shuffle collection
        Collections.shuffle(temp);
        //add the right number of cards based on numPlayer
        if(numPlayers == 2) {
            buildingCardsEra3.addAll(temp.subList(0,3));
        }
        else if (numPlayers == 3 || numPlayers == 4) {
            buildingCardsEra3.addAll(temp.subList(0,4));
        }
        else{
            buildingCardsEra3.addAll(temp.subList(0,5));
        }
    }

    public List<BuildingCard> drawAllEra1(){
        return Collections.unmodifiableList(buildingCardsEra1);
    }

    public List<BuildingCard> drawAllEra2(){
        return Collections.unmodifiableList(buildingCardsEra2);
    }

    public List<BuildingCard> drawAllEra3(){
        return Collections.unmodifiableList(buildingCardsEra3);
    }
}
