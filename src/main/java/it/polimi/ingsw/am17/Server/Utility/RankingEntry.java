package it.polimi.ingsw.am17.Server.Utility;

import java.time.LocalDate;
import java.util.UUID;

public record RankingEntry(
        UUID gameId,
        LocalDate date,
        String nickname,
        int finalPoints
) {}
