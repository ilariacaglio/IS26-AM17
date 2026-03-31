package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class PaintingEvent extends EventCard {
    private final int pointsLow;
    private final int pointsMax;
    private final int numLow;
    private final int numMax;

    public int getNumLow() {
        return numLow;
    }

    public int getNumMax() {
        return numMax;
    }

    public int getPointsLow() {
        return pointsLow;
    }

    public int getPointsMax() {
        return pointsMax;
    }

    public PaintingEvent(int pointsLow, int pointsMax, int numLow, int numMax, boolean Final, int era) {
        super(Final, era, CardType.PAINTING_EVENT);
        this.pointsLow = pointsLow;
        this.pointsMax = pointsMax;
        this.numLow = numLow;
        this.numMax = numMax;
    }

    @Override
    public void computeScore(List<Player> list){
        for (int i = 0; i<list.size(); i++){
            long numArtist = list.get(i).getPlayerCards().stream()
                    .filter(c-> c instanceof Artist)
                    .count();

            if(numArtist>=numMax){
                int pointsGain = Math.toIntExact(numArtist * pointsMax);
                list.get(i).addPp(pointsGain);
            }
            else {
                list.get(i).addPp(pointsLow*(-1));
            }
        }
    }
}
