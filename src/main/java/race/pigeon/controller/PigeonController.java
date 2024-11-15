package race.pigeon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import race.pigeon.exception.ResourceNotFoundException;
import race.pigeon.exception.UnauthorizedAccessException;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.impl.PigeonServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/pigeon")
public class PigeonController {

    @Autowired
    private PigeonServiceImpl pigeonService;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/add")
    public ResponseEntity<Pigeon> addPigeon(@RequestBody Pigeon pigeon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        appUser user = appUserRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new UnauthorizedAccessException("User not found or unauthorized access.");
        }

        pigeon.setUser(user);
        Pigeon savedPigeon = pigeonService.addPigeon(pigeon);
        return new ResponseEntity<>(savedPigeon, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Pigeon>> getAllPigeons() {
        List<Pigeon> pigeons = pigeonService.getAllPigeons();
        return new ResponseEntity<>(pigeons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pigeon> getPigeonById(@PathVariable String id) {
        Pigeon pigeon = pigeonService.findById(id);
        return new ResponseEntity<>(pigeon, HttpStatus.OK);
    }
}
