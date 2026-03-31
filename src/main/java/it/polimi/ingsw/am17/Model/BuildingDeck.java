package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.am17.Utility.BuildingCardParser.buildingCard13MParser;
import static it.polimi.ingsw.am17.Utility.BuildingCardParser.buildingCard3MParser;

public class BuildingDeck {
    private final List<BuildingCard> buildingCards;

    public BuildingDeck(int numPlayers, int era)
    {
        buildingCards = new ArrayList<>();
        switch(era){
            case 1:
                buildEra1Deck(numPlayers);
                break;
            case 2:
                buildEra2Deck(numPlayers);
                break;
            case 3:
                buildEra3Deck(numPlayers);
                break;
            default:
                throw new IllegalArgumentException("Invalid era value");
        }
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
            buildingCards.add(temp.getFirst());
        }
        else {
            buildingCards.addAll(temp.subList(0,2));
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
            buildingCards.addAll(temp.subList(0,2));
        }
        else {
            buildingCards.addAll(temp.subList(0,3));
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
            buildingCards.addAll(temp.subList(0,3));
        }
        else if (numPlayers == 3 || numPlayers == 4) {
            buildingCards.addAll(temp.subList(0,4));
        }
        else{
            buildingCards.addAll(temp.subList(0,5));
        }
    }
    public List<BuildingCard> drawAll(){
        return buildingCards;
    }
}
