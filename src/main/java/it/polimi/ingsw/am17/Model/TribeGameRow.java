package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.NoSuchElementException;

public class TribeGameRow extends TribeCardRow {

    public void removeCard(TribesCard card) throws NoSuchElementException {
        if(getCards().contains(card)) {
            getCards().remove(card);
        } else { throw new NoSuchElementException("Card not found");}
    }


}
