package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Pigeon;

@Repository
public interface PigeonRepository extends MongoRepository<Pigeon, Integer> {
}
