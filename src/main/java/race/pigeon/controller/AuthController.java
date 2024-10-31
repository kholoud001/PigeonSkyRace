package race.pigeon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import race.pigeon.entities.model.Breeder;
import race.pigeon.service.BreederService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BreederService breederService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Breeder breeder) {
        try {
            // Ensure the ID is not set so MongoDB can auto-generate it
            breeder.setId(null); // Make sure ID is null or not set

            Breeder registeredBreeder = breederService.register(
                    breeder.getName(),
                    breeder.getPassword(),
                    breeder.getLoft().getName(),
                    breeder.getLoft().getLatitude(),
                    breeder.getLoft().getLongitude()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredBreeder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }



}