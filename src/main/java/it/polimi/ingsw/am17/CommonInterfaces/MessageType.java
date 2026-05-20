package it.polimi.ingsw.am17.CommonInterfaces;

public enum MessageType {
    // Client requests
    GET_GAMES_LIST,
    CREATE_GAME,
    JOIN_GAME,
    PICK_OFFERING_CARD,
    PICK_TRIBE_CARDS,

    // Server sends updates
    UPDATE_GAME_ID,
    UPDATE_GAMES_ID_LIST,
    UPDATE_ERA,
    UPDATE_PLAYERS_DATA,
    UPDATE_PLAYER_SELECT_OFFERING_CARD,
    UPDATE_PLAYER_SELECT_TRIBE_CARDS,
    UPDATE_END_TURN,
    UPDATE_START_GAME,
    UPDATE_ERROR,
    END_GAME,
    HEARTBEAT,
    CLOSE_GAME,
    UPDATE_RANKING
}
