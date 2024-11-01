package race.pigeon.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import race.pigeon.model.enums.Role;
import race.pigeon.model.entity.User;
import race.pigeon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class OrganizerSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedOrganizers();
        }
    }

    private void seedOrganizers() {
        User user = new User();
        user.setUsername("ichaoui56");
        user.setPassword(passwordEncoder.encode("i@123"));
        user.setRole(Role.Organizer);
        User savedUser = userRepository.save(user);

        System.out.println("Organizer seeded: " + savedUser);
    }
}
