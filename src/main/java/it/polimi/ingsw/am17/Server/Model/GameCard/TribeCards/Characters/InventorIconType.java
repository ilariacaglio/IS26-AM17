package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

public enum InventorIconType {
    FLUTE,
    MORTAR,
    ROPE,
    LEATHER,
    BREAD,
    CANOE,
    HOOK,
    IDOL,
    NECKLACE,
    FLINT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
