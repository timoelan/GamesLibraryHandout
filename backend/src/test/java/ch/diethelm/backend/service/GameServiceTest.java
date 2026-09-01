package ch.diethelm.backend.service;

import ch.diethelm.backend.model.Game;
import ch.diethelm.backend.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * White-Box Unit Tests für {@link GameService}.
 *
 * <p>Das {@link GameRepository} wird mit Mockito gemockt, sodass die Service-Logik
 * ohne echte Datenbank getestet werden kann.</p>
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game existingGame;

    @BeforeEach
    void setUp() {
        existingGame = Game.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .imageUrl("old.png")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .build();
    }

    @Test
    void getAllGames_returnsListFromRepository() {
        List<Game> games = List.of(existingGame);
        when(gameRepository.findAll()).thenReturn(games);

        List<Game> result = gameService.getAllGames();

        assertThat(result).isEqualTo(games);
        verify(gameRepository).findAll();
    }

    @Test
    void getGameById_returnsGame_whenExists() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));

        Game result = gameService.getGameById(1L);

        assertThat(result).isEqualTo(existingGame);
    }

    @Test
    void getGameById_throwsNoSuchElementException_whenNotFound() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGameById(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void createGame_savesAndReturnsGame() {
        Game newGame = Game.builder()
                .title("New Game")
                .description("Description")
                .imageUrl("new.png")
                .releaseDate(LocalDate.of(2024, 5, 1))
                .build();
        when(gameRepository.save(newGame)).thenReturn(newGame);

        Game result = gameService.createGame(newGame);

        assertThat(result).isEqualTo(newGame);
        verify(gameRepository).save(newGame);
    }

    @Test
    void updateGame_overwritesAllFields_whenGameExists() {
        Game updatedData = Game.builder()
                .title("New Title")
                .description("New Description")
                .imageUrl("new.png")
                .releaseDate(LocalDate.of(2023, 6, 15))
                .build();
        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.updateGame(1L, updatedData);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getImageUrl()).isEqualTo("new.png");
        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2023, 6, 15));
        verify(gameRepository).save(existingGame);
    }

    @Test
    void updateGame_throwsException_whenIdDoesNotExist() {
        Game updatedData = Game.builder().title("New Title").build();
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.updateGame(99L, updatedData))
                .isInstanceOf(NoSuchElementException.class);

        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void deleteGame_deletesGame_whenExists() {
        when(gameRepository.existsById(1L)).thenReturn(true);

        gameService.deleteGame(1L);

        verify(gameRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteGame_throwsException_whenIdDoesNotExist() {
        when(gameRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> gameService.deleteGame(99L))
                .isInstanceOf(NoSuchElementException.class);

        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchByTitle_delegatesToRepository() {
        List<Game> games = List.of(existingGame);
        when(gameRepository.findByTitleContainingIgnoreCase("title")).thenReturn(games);

        List<Game> result = gameService.searchByTitle("title");

        assertThat(result).isEqualTo(games);
        verify(gameRepository).findByTitleContainingIgnoreCase("title");
    }
}
