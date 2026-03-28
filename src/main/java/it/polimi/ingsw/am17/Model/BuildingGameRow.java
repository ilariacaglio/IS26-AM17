package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;

import java.util.NoSuchElementException;

public class BuildingGameRow extends BuildingCardRow {

    public void removeCard(BuildingCard card) throws NoSuchElementException {
        if(getCards().contains(card)){
            getCards().remove(card);
        }
        else { throw new NoSuchElementException("Card not found"); }
    }

}
