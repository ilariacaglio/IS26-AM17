package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import java.util.Queue;
import java.util.UUID;

public abstract class EventCard extends TribesCard {
    private final Boolean Final;

    public EventCard(Boolean Final, GameState era, CardType cardType, UUID id) {
        this.Final = Final;
        super(era, cardType, id);
    }

    public void computeScore(Queue<Player> list){}

    public Boolean isFinal() {return Final;}
}
