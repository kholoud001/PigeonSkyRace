package race.pigeon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.repository.PigeonRepository;

@Service
public class PigeonService{

    @Autowired
    private PigeonRepository pigeonRepository;

    public Pigeon addPigeon(Pigeon pigeon) {
        return pigeonRepository.save(pigeon);
    }
}
