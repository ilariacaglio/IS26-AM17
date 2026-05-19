package it.polimi.ingsw.am17.Server.Model;

public enum GameState {
    NONE,
    LOBBY,
    ERA1,
    ERA2,
    ERA3,
    ENDED;

    /**
     * @return true if game is in an era, false otherwise
     */
    public boolean isGameStarted() {
        return this.equals(GameState.ERA1) || this.equals(GameState.ERA2) || this.equals(GameState.ERA3);
    }

    /**
     * @return true if game is ended, false otherwise
     */
    public boolean isGameEnded() {
        return this.equals(GameState.ENDED);
    }
}
