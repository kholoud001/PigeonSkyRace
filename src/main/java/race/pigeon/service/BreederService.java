package race.pigeon.service;

import race.pigeon.entities.model.Breeder;

public interface BreederService {
    Breeder register(String breederName, String password, String loftName, double latitude, double longitude);
}
