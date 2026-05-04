package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Builder;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Inventor;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.InventorIconType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BuildingCardTest {
    Random r = new Random();
    /**
     * Generate and return a list of CharacterCard with number of cards as specified
     * @param hunter number of hunter you want to have in the list
     * @param art number of artist you want to have in the list
     * @param build number of builder you want to have in the list
     * @param inv number of inventor you want to have in the list
     * @param sham number of shaman you want to have in the list
     * @param bind number of binder you want to have in the list
     * @return List<CharacterCard>
     */
    public List<CharacterCard> createCharacterCardList(int hunter, int art, int build, int inv, int sham, int bind)
    {
        List<CharacterCard> cc = new ArrayList<>();

        for (int i = 0; i < hunter; i++) {
            cc.add(new CharacterCard(1, 1, CardType.HUNTER, null));
        }
        for (int i = 0; i < art; i++) {
            cc.add(new CharacterCard(1, 1, CardType.ARTIST, null));
        }
        for (int i = 0; i < build; i++) {
            cc.add(new Builder(1, 1, r.nextInt(5), r.nextInt(5), null));
        }
        for (int i = 0; i < inv; i++) {
            cc.add(new Inventor(1, 1, InventorIconType.values()[r.nextInt(10)], null));
        }
        for (int i = 0; i < sham; i++) {
            cc.add(new CharacterCard(1, 1, CardType.SHAMAN, null));
        }
        for (int i = 0; i < bind; i++) {
            cc.add(new CharacterCard(1, 1, CardType.BINDER, null));
        }

        return cc;
    }
}