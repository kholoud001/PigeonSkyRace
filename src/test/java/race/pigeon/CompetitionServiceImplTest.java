package race.pigeon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import race.pigeon.model.entity.Competition;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.service.impl.CompetitionServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;  // Correct import
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CompetitionServiceImplTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private CompetitionServiceImpl competitionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testAddCompetition() {
        // Arrange
        Competition competition = new Competition();
        competition.setId("123");
        competition.setName("Race Championship");

        when(competitionRepository.save(competition)).thenReturn(competition);

        // Act
        Competition result = competitionService.addCompetition(competition);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Race Championship", result.getName());
        verify(competitionRepository, times(1)).save(competition); // Ensure save was called once
    }

    @Test
    void testFindById_Success() {
        // Arrange
        String competitionId = "123";
        Competition competition = new Competition();
        competition.setId(competitionId);
        competition.setName("Race Championship");

        when(competitionRepository.findById(competitionId)).thenReturn(competition);

        // Act
        Competition result = competitionService.findById(competitionId);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Race Championship", result.getName());
        verify(competitionRepository, times(1)).findById(competitionId);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        String competitionId = "123";

        when(competitionRepository.findById(competitionId)).thenReturn(null);

        // Act
        Competition result = competitionService.findById(competitionId);

        // Assert
        assertNull(result); // Use assertNull from the correct import
        verify(competitionRepository, times(1)).findById(competitionId);
    }
}
