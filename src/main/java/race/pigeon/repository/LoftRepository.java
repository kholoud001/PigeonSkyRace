package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.entities.model.Loft;

@Repository
public interface LoftRepository extends MongoRepository<Loft, String> {
    boolean existsByName(String name);
}
