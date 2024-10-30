package race.pigeon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import race.pigeon.entities.model.User;
import race.pigeon.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testConnectionAndInsert() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        user = userRepository.save(user);

        assertThat(userRepository.findById(user.getId())).isPresent();
    }
}
