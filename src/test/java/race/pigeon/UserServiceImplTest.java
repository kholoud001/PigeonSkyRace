package race.pigeon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import race.pigeon.model.entity.appUser;
import race.pigeon.model.enums.Role;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*; // Correct import for JUnit assertions
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testLoadUserByUsername_Success() {
        String username = "testUser";
        appUser appUser = new appUser();
        appUser.setUsername(username);
        appUser.setPassword("password123");
        appUser.setRole(Role.Breeder);

        when(appUserRepository.findByUsername(username)).thenReturn(appUser);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(appUserRepository.findByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));

        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_Success() {
        String username = "testUser";
        appUser appUser = new appUser();
        appUser.setUsername(username);
        appUser.setPassword("password123");
        appUser.setRole(Role.Breeder);

        when(appUserRepository.findByUsername(username)).thenReturn(appUser);

        appUser result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("password123", result.getPassword());

        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(appUserRepository.findByUsername(username)).thenReturn(null);

        appUser result = userService.findByUsername(username);

        assertNull(result);
        verify(appUserRepository, times(1)).findByUsername(username);
    }
}
