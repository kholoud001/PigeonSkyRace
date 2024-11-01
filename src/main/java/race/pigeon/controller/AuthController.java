package race.pigeon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import race.pigeon.model.entity.User;
import race.pigeon.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);

        if (savedUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = userService.verify(user.getUsername(),user.getPassword());
        if (token.equals("Invalid password.") || token.startsWith("Loft not found")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
        }
        return ResponseEntity.ok(token);
    }

}