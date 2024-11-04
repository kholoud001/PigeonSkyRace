package race.pigeon.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import race.pigeon.model.enums.Role;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class OrganizerSeeder implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedOrganizers();
        }
    }

    private void seedOrganizers() {
        var bCryptEncoder = new BCryptPasswordEncoder();

        appUser user = new appUser();
        user.setUsername("ichaoui56");
        user.setPassword(bCryptEncoder.encode("i@123"));
        user.setRole(Role.Organizer);
        appUser savedUser = userRepository.save(user);

        System.out.println("Organizer seeded: " + savedUser);
    }
}
