package it.polimi.ingsw.am17.Server.Utility;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record RankingEntry(
        UUID gameId,
        LocalDate date,
        String nickname,
        int finalPoints) implements Serializable {

    @Override
    public String toString() {
        return String.format("[%s]\t%-15s\t%d",
                date.toString(),
                nickname,
                finalPoints);
    }

    public UUID getGameId() {
        return gameId;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getFinalPoints() {
        return finalPoints;
    }
}
