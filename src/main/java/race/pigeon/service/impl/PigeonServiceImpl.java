package race.pigeon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.repository.PigeonRepository;
import race.pigeon.service.PigeonService;

import java.util.List;
import java.util.Optional;

@Service
public class PigeonServiceImpl implements PigeonService {

    @Autowired
    private PigeonRepository pigeonRepository;

    @Override
    public Pigeon addPigeon(Pigeon pigeon) {
        return pigeonRepository.save(pigeon);
    }

    @Override
    public List<Pigeon> getAllPigeons(){
        return pigeonRepository.findAll();
    }

    @Override
    public List<Pigeon> findByRingNumbers(List<String> ringNumbers) {
        return pigeonRepository.findByRingNumberIn(ringNumbers);
    }

    @Override
    public Pigeon findById(String id) {
        Optional<Pigeon> pigeon = pigeonRepository.findById(id);
        return pigeon.orElse(null);
    }

}
