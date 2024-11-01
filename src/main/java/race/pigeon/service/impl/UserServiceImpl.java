package race.pigeon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import race.pigeon.model.enums.Role;
import race.pigeon.model.entity.User;
import race.pigeon.repository.UserRepository;
import race.pigeon.service.UserService;
import race.pigeon.util.JWTService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.Breeder);
        userRepository.save(user);
        return user;
    }

    public String verify(String username, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                return jwtService.generateToken(userDetails.getUsername());
            } else {
                return "Invalid password.";
            }
        } catch (UsernameNotFoundException e) {
            return "User not found: " + username;
        }
    }
}
