package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.am17.Utility.BuildingCardParser.buildingCard13MParser;
import static it.polimi.ingsw.am17.Utility.BuildingCardParser.buildingCard3MParser;

public class BuildingDeck {
    private final List<BuildingCard> buildingCards;

    public BuildingDeck(int numPlayer, int era)
    {
        buildingCards = new ArrayList<>();
        switch(era){
            case 1:
                buildEra1Deck();
                break;
            case 2:
                buildEra2Deck();
                break;
            case 3:
                buildEra3Deck();
                break;
            default:
                throw new IllegalArgumentException("Invalid era value");
        }
    }

    private void buildEra1Deck(){
        //singleton building cards
        buildingCards.add(new BuildingType10());
        buildingCards.add(new BuildingType11());
        buildingCards.add(new BuildingType12());
        buildingCards.add(new BuildingType14());
        //parse from json and add cards 13M
        buildingCards.addAll(buildingCard13MParser(1));
    }

    private void buildEra2Deck(){
        //singleton building cards
        buildingCards.add(new BuildingType4());
        buildingCards.add(new BuildingType5());
        buildingCards.add(new BuildingType6());
        buildingCards.add(new BuildingType7());
        buildingCards.add(new BuildingType8());
        buildingCards.add(new BuildingType9());
        //parse from json and add cards 13M
        buildingCards.addAll(buildingCard13MParser(2));
    }

    private  void buildEra3Deck(){
        //singleton building cards
        buildingCards.add(new BuildingType1());
        buildingCards.add(new BuildingType2());
        //parse from json and add cards 3M
        buildingCards.addAll(buildingCard3MParser());
    }
    public List<BuildingCard> drawAll(){
        return buildingCards;
    }
}
