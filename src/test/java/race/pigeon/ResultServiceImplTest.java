package race.pigeon;

import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.repository.PigeonRepository;
import race.pigeon.repository.ResultRepository;
import race.pigeon.service.impl.ResultServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;  // Correct import for JUnit assertions
import static org.mockito.Mockito.*;

class ResultServiceImplTest {

    @InjectMocks
    private ResultServiceImpl resultService;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private PigeonRepository pigeonRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadData_success() throws IOException, CsvException {
        // Mock input file
        MockMultipartFile file = new MockMultipartFile(
                "data", "test.csv", "text/csv",
                "2024-11-11 10:00:00,100,60,10,P12345".getBytes()
        );

        // Mock dependencies
        Competition competition = new Competition();
        competition.setId("C1");
        when(competitionRepository.findById("C1")).thenReturn(competition);

        Pigeon pigeon = new Pigeon();
        when(pigeonRepository.findByRingNumber("P12345")).thenReturn(Optional.of(pigeon));

        // Execute method
        resultService.uploadData(file, "C1");

        // Verify repository interactions
        ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository, times(1)).save(captor.capture());

        Result savedResult = captor.getValue();
        assertNotNull(savedResult);
        assertEquals(100, savedResult.getDistance());
        assertEquals(60, savedResult.getVitesse());
        assertEquals(10, savedResult.getPoint());
        assertEquals(pigeon, savedResult.getPigeon());
        assertEquals(competition, savedResult.getCompetition());
    }

    @Test
    void testCalculateAndUpdateDistance_success() {
        // Mock input data
        Competition competition = new Competition();
        competition.setLatitude(40.0);
        competition.setLongitude(30.0);

        appUser user = new appUser();
        user.setLatitude(41.0);
        user.setLongitude(31.0);

        Pigeon pigeon = new Pigeon();

        Result existingResult = new Result();
        when(resultRepository.findByCompetitionAndPigeon(competition, pigeon))
                .thenReturn(Optional.of(existingResult));

        // Execute method
        Result result = resultService.calculateAndUpdateDistance(competition, user, pigeon);

        // Verify the result
        assertNotNull(result);
        assertEquals(competition, result.getCompetition());
        assertEquals(pigeon, result.getPigeon());
        assertNotNull(result.getDistance());
        verify(resultRepository, times(1)).save(result);
    }

    @Test
    void testCalculateVitesse_success() {
        // Mock input data
        Competition competition = new Competition();
        competition.setDepartureTime(LocalDateTime.of(2024, 11, 10, 8, 0));

        Result result = new Result();
        result.setCompetition(competition);
        result.setHeureArrivee(LocalDateTime.of(2024, 11, 10, 10, 0));
        result.setDistance(120.0);

        // Execute method
        String message = resultService.calculateVitesse(result);

        // Verify result
        assertTrue(message.contains("Speed (Vitesse) updated successfully"));
        verify(resultRepository, times(1)).save(result);
    }
}
