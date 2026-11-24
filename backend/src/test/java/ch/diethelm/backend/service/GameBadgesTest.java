package ch.diethelm.backend.service;

import ch.diethelm.backend.model.Game;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameBadgesTest {

    private final Clock fixed = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"),
            ZoneId.of("Europe/Zurich"));

    @Test
    void releasedLastWeek_isNew() {
        Game game = Game.builder().title("X")
                .releaseDate(LocalDate.of(2026, 10, 20)).build();
        GameBadges badges = new GameBadges(fixed);
        assertTrue(badges.isNew(game));
    }

    @Test
    void releasedOneYearAgo_isNotNew() {
        Game game = Game.builder().title("X")
                .releaseDate(LocalDate.of(2025, 10, 27)).build();
        GameBadges badges = new GameBadges(fixed);
        assertFalse(badges.isNew(game));
    }
}