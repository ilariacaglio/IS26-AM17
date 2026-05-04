package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

public class EventCard extends TribesCard {
    private boolean Final;

    public EventCard(boolean Final, int era, CardType cardType, UUID id) {
        this.Final = Final;
        super(era,cardType, id);
    }

    public void computeScore(List<Player> list){}

    public boolean isFinal() {return Final;}
}
