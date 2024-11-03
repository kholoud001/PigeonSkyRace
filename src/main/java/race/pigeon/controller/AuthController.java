package race.pigeon.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import race.pigeon.model.entity.LoginDto;
import race.pigeon.model.entity.RegisterDto;
import race.pigeon.model.entity.appUser;
import race.pigeon.model.enums.Role;
import race.pigeon.repository.AppUserRepository;

import java.time.Instant;
import java.util.HashMap;

@RestController
@RequestMapping("/account")
public class AuthController {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;


    @Autowired
    AppUserRepository appUserRepository ;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/profile")
    public ResponseEntity<Object> profile (Authentication auth) {
        var response = new HashMap<String, Object>();
        response.put("Username", auth.getName());
        response.put("Authorities", auth.getAuthorities());
        var appUser = appUserRepository.findByUsername (auth.getName());
        response.put("User", appUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Valid @RequestBody LoginDto loginDto,
            BindingResult result) {

        if (result.hasErrors()) {
            var errorsList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();
            for (var error : errorsList) {
                var fieldError = (FieldError) error;
                errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorsMap);
        }

        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            appUser User = appUserRepository.findByUsername(loginDto.getUsername());

            // Generate a JWT token (assuming createJwtToken method exists)
            String jwtToken = createJwtToken(User);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", User);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            System.out.println("There is an Exception:");
            ex.printStackTrace();
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterDto registerDto,
            BindingResult result) {

        if (result.hasErrors()) {
            var errorsList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();

            for (var error : errorsList) {
                var fieldError = (FieldError) error;
                errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorsMap);
        }

        var bCryptEncoder = new BCryptPasswordEncoder();
        appUser appUser = new appUser();
        appUser.setUsername(registerDto.getUsername());
        appUser.setRole(Role.Breeder);
        appUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));

        try {
            // Check if username/loftName are already used
            var otherUser = appUserRepository.findByUsername(registerDto.getUsername());
            if (otherUser != null) {
                return ResponseEntity.badRequest().body("Username already used");
            }
            otherUser = appUserRepository.findByLoftName(registerDto.getLoftName());
            if (otherUser != null) {
                return ResponseEntity.badRequest().body("Loft name already used");
            }

            // Save the new user
            appUserRepository.save(appUser);

            // Generate JWT token
            String jwtToken = createJwtToken(appUser);

            // Prepare response
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", appUser);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            System.out.println("There is an Exception: ");
            ex.printStackTrace();
        }
        return ResponseEntity.status(500).body("Internal Server Error");
    }


    private String createJwtToken(appUser appUser) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(24 * 3600))
                .subject(appUser.getUsername())
                .claim("role", appUser.getRole())
                .build();

        var encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey.getBytes()));

        var params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return encoder.encode(params).getTokenValue();
    }
}
