package race.pigeon.service;

import race.pigeon.model.entity.Pigeon;

import java.util.List;

public interface PigeonService {
    Pigeon addPigeon(Pigeon pigeon);

    List<Pigeon> getAllPigeons();

    List<Pigeon> findByRingNumbers(List<String> ringNumbers);

    Pigeon findById(String id);
}
