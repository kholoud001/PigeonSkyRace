package race.pigeon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.PigeonService;

import java.util.List;

@RestController
@RequestMapping("/pigeon")
public class PigeonController {

    @Autowired
    private PigeonService pigeonService;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/add")
    public ResponseEntity<Pigeon> addPigeon(@RequestBody Pigeon pigeon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        appUser user = appUserRepository.findByUsername(currentUsername);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        pigeon.setUser(user);

        Pigeon savedPigeon = pigeonService.addPigeon(pigeon);
        return new ResponseEntity<>(savedPigeon, HttpStatus.CREATED);
    }

    @GetMapping
    public String home() { return "Home page"; }
}
