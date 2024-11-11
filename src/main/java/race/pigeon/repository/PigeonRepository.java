package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Pigeon;

import java.util.List;

@Repository
public interface PigeonRepository extends MongoRepository<Pigeon, Integer> {
    List<Pigeon> findByRingNumberIn(List<String> ringNumbers);
}
