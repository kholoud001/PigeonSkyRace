package race.pigeon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.repository.PigeonRepository;
import race.pigeon.service.impl.PigeonServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Correct import for JUnit assertions
import static org.mockito.Mockito.*;

class PigeonServiceTest {

    @Mock
    private PigeonRepository pigeonRepository;

    @InjectMocks
    private PigeonServiceImpl pigeonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testAddPigeon() {
        // Arrange
        Pigeon pigeon = new Pigeon();
        pigeon.setId("1");
        pigeon.setRingNumber("R123");

        when(pigeonRepository.save(pigeon)).thenReturn(pigeon);

        // Act
        Pigeon result = pigeonService.addPigeon(pigeon);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("R123", result.getRingNumber());
        verify(pigeonRepository, times(1)).save(pigeon);
    }

    @Test
    void testGetAllPigeons() {
        // Arrange
        Pigeon pigeon1 = new Pigeon();
        pigeon1.setId("1");
        pigeon1.setRingNumber("R123");

        Pigeon pigeon2 = new Pigeon();
        pigeon2.setId("2");
        pigeon2.setRingNumber("R456");

        when(pigeonRepository.findAll()).thenReturn(Arrays.asList(pigeon1, pigeon2));

        // Act
        List<Pigeon> result = pigeonService.getAllPigeons();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(pigeonRepository, times(1)).findAll();
    }

    @Test
    void testFindByRingNumbers() {
        // Arrange
        List<String> ringNumbers = Arrays.asList("R123", "R456");
        Pigeon pigeon1 = new Pigeon();
        pigeon1.setId("1");
        pigeon1.setRingNumber("R123");

        Pigeon pigeon2 = new Pigeon();
        pigeon2.setId("2");
        pigeon2.setRingNumber("R456");

        when(pigeonRepository.findAll()).thenReturn(Arrays.asList(pigeon1, pigeon2));

        // Act
        List<Pigeon> result = pigeonService.findByRingNumbers(ringNumbers);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getRingNumber().equals("R123")));
        assertTrue(result.stream().anyMatch(p -> p.getRingNumber().equals("R456")));
        verify(pigeonRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        String pigeonId = "1";
        Pigeon pigeon = new Pigeon();
        pigeon.setId(pigeonId);
        pigeon.setRingNumber("R123");

        when(pigeonRepository.findById(pigeonId)).thenReturn(Optional.of(pigeon));

        // Act
        Pigeon result = pigeonService.findById(pigeonId);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("R123", result.getRingNumber());
        verify(pigeonRepository, times(1)).findById(pigeonId);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        String pigeonId = "1";

        when(pigeonRepository.findById(pigeonId)).thenReturn(Optional.empty());

        // Act
        Pigeon result = pigeonService.findById(pigeonId);

        // Assert
        assertNull(result);
        verify(pigeonRepository, times(1)).findById(pigeonId);
    }
}
