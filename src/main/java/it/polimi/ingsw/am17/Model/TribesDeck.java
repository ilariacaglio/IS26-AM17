package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.EventCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static it.polimi.ingsw.am17.Utility.CardParser.*;

public class TribesDeck {
    private final List<TribesCard> tribeCards;
    private int currIndex;

    public TribesDeck(int numPlayers) {
        tribeCards =  new ArrayList<TribesCard>();
        currIndex = -1;
        //add cards to list
        tribeCards.addAll(artistsParser(numPlayers));
        tribeCards.addAll(bindersParser(numPlayers));
        tribeCards.addAll(buildersParser(numPlayers));
        tribeCards.addAll(huntersParser(numPlayers));
        tribeCards.addAll(inventorParser(numPlayers));
        tribeCards.addAll(shamansParser(numPlayers));
        tribeCards.addAll(foodEventParser(numPlayers));
        tribeCards.addAll(huntingEventParser(numPlayers));
        tribeCards.addAll(paintingEventParser(numPlayers));
        tribeCards.addAll(ritualEventParser(numPlayers));
        //sort list basing on the cards era
        tribeCards.sort(Comparator.comparing(TribesCard::getEra));
        //shuffle era cards sublists
        ShuffleEra1();
        ShuffleEra2();
        ShuffleEra3();
        //move final events to the end of the deck
        List<TribesCard> list = tribeCards.stream()
                .filter(c->!c.getCardType().isCharacter() && ((EventCard)c).isFinal())
                .toList();
        for (TribesCard card: list) {
            tribeCards.remove(card);
            tribeCards.add(card);
        }
    }

    public TribesCard Draw() {
        if (currIndex + 1 >= tribeCards.size()) {
            throw new IllegalStateException("No more cards left in the deck.");
        }

        currIndex++;
        return tribeCards.get(currIndex);
    }

    /// only to use for testing
    public List<TribesCard> getTribeCards() {
        return tribeCards;
    }

    private void ShuffleEra1(){
        int index = IntStream.range(0, tribeCards.size())
                .map(i -> tribeCards.size() - 1 - i)
                .filter(i -> tribeCards.get(i).getEra() == 1)
                .findFirst()
                .orElse(-1);
        if(index != -1){
            Collections.shuffle(tribeCards.subList(0, index));
        }
    }

    private void ShuffleEra2(){
        int startIndex = IntStream.range(0, tribeCards.size())
                .filter(i -> tribeCards.get(i).getEra() == 2)
                .findFirst()
                .orElse(-1);
        int endIndex = IntStream.range(0, tribeCards.size())
                .map(i -> tribeCards.size() - 1 - i)
                .filter(i -> tribeCards.get(i).getEra() == 2)
                .findFirst()
                .orElse(-1);
        if(startIndex != -1 && endIndex != -1){
            Collections.shuffle(tribeCards.subList(startIndex, endIndex));
        }
    }

    private void ShuffleEra3(){
        int index = IntStream.range(0, tribeCards.size())
                .filter(i -> tribeCards.get(i).getEra() == 3)
                .findFirst()
                .orElse(-1);
        if(index != -1){
            Collections.shuffle(tribeCards.subList(index, tribeCards.size()));
        }
    }
}