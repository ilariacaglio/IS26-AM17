package it.polimi.ingsw.am17.Server.Utility;

import java.time.LocalDate;
import java.util.UUID;

public record RankingEntry(
        UUID gameId,
        LocalDate date,
        String nickname,
        int finalPoints) {

    @Override
    public String toString() {
        return String.format("[%s]\t[%s]\t%s\t%d",
                gameId.toString(),
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
