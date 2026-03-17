package it.polimi.ingsw.am17;

public class CharacterCard extends TribesCard{
    private int minPlayers;

    public CharacterCard(int era, int minPlayers){
        super(era);
        this.minPlayers = minPlayers;
    }


}
