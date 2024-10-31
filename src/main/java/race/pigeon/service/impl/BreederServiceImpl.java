package race.pigeon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import race.pigeon.entities.enums.Role;
import race.pigeon.entities.model.Breeder;
import race.pigeon.entities.model.Loft;
import race.pigeon.repository.BreederRepository;
import race.pigeon.repository.LoftRepository;
import race.pigeon.service.BreederService;
import race.pigeon.util.JwtUtil;

@Service
public class BreederServiceImpl implements BreederService {
    @Autowired
    private BreederRepository breederRepository;

    @Autowired
    private LoftRepository loftRepository;
//
//    @Override
//    public Breeder register(String breederName, String password, String loftName, double latitude, double longitude) {
//        if (breederRepository.existsByName(breederName)) {
//            throw new RuntimeException("Breeder name already exists.");
//        }
//        if (loftRepository.existsByName(loftName)) {
//            throw new RuntimeException("Loft name already exists.");
//        }
//
//        Loft loft = new Loft();
//        loft.setName(loftName);
//        loft.setLatitude(latitude);
//        loft.setLongitude(longitude);
//
//        loft = loftRepository.save(loft);
//
//        Breeder breeder = new Breeder();
//        breeder.setName(breederName);
//        breeder.setPassword(password);
//        breeder.setRole(Role.Breeder);
//        breeder.setLoft(loft);
//
//        return breederRepository.save(breeder);
//    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Breeder register(String breederName, String password, String loftName, double latitude, double longitude) {

        if (loftRepository.existsByName(loftName)) {
            throw new RuntimeException("Loft name already exists.");
        }
        Loft loft = new Loft();
        loft.setName(loftName);
        loft.setLatitude(latitude);
        loft.setLongitude(longitude);
        loft = loftRepository.save(loft);

        Breeder breeder = new Breeder();
        breeder.setName(breederName);
        breeder.setPassword(passwordEncoder.encode(password));
        breeder.setRole(Role.Breeder);
        breeder.setLoft(loft);

        breederRepository.save(breeder);

        //return jwtUtil.generateToken(breederName);
        return breeder;
    }

}
